package di.unito.it.prog3.server.storage;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Email.ID;
import di.unito.it.prog3.libs.email.Queue;
import di.unito.it.prog3.libs.net.Chrono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class ConcurrentFileBasedEmailStore implements EmailStore {

    /**
     * A map used to store and retrieve the locks.
     */
    private final ConcurrentMap<String, ReentrantReadWriteLock> queueLocks;


    /**
     * The store file extension.
     */
    private final String extension;


    /**
     * The store directory.
     */
    private final Path storeDir;


    /**
     * Sets the store base directory and the file extension.
     *
     * @param storeDir The store base directory.
     * @param extension The file extension.
     */
    public ConcurrentFileBasedEmailStore(String storeDir, String extension) {
        this.storeDir = Paths.get(storeDir);
        this.extension = extension;
        queueLocks = new ConcurrentHashMap<>();
    }


    /**
     * Check if a user is registered.
     * No locks used as users are preregistered.
     *
     * @param userMail the user's e-mail.
     * @return true if the user exists.
     * @throws EmailStoreException if the user does not exist.
     */
    @Override
    public boolean userExists(String userMail) throws EmailStoreException {
        String[] mailboxParts = userMail.split("@");
        String user = mailboxParts[0];
        String domain = mailboxParts[1];

        // Computing the path to the user folder
        Path userStore = Paths.get(getStoreDir().toString(), domain, user);

        // If it doesn't exist, the user is not registered
        if (!Files.exists(userStore)) {
            throw new EmailStoreException("Unknown user " + userMail);
        }

        // Creates queue paths if not already present
        for (Queue queue : Queue.values()) {
            Path queuePath = Paths.get(userStore.toString(), queue.asShortPath());
            if (!Files.exists(queuePath)) {
                try {
                    Files.createDirectories(queuePath);
                } catch (IOException e) {
                    throw new EmailStoreException("Could not create queue folders");
                }
            }
        }

        return true;
    }


    /**
     * Stores a new e-mail (must be a first write).
     * Lock exclusive queue to prevent inconsistent reads.
     *
     * @param email the e-mails that must be stored.
     * @throws EmailStoreException if the operation errors.
     */
    @Override
    public void store(Email email) throws EmailStoreException {
        Path queuePath = getQueuePath(email.getId());

        // Generating a relative id for the new e-mail
        UUID relativeId = UUID.randomUUID();
        email.setRelativeId(relativeId);

        // Computing the path of the store file
        Path emailPath = Paths.get(queuePath.toString(), relativeId + extension);

        // If it's not a fist write, fail
        if (Files.exists(emailPath)) {
            throw new EmailStoreException("Cannot store already existing e-mail");
        }

        WriteLock lock = getLock(email).writeLock();
        try {
            lock.lock();

            // Create the new file
            Files.createFile(emailPath);

            // Timestamp the e-mail
            email.setTimestamp(LocalDateTime.now());

            // Serialize e-mail to file
            serialize(email, emailPath);

        } catch (Exception e) {
            throw new EmailStoreException("Could not store e-mail", e);
        } finally {
            lock.unlock();
        }
    }


    /**
     * Updates an existing e-mail.
     * Lock exclusive queue to prevent inconsistent reads.
     *
     * @param email the e-mail (which already includes the necessary changes).
     * @throws EmailStoreException if the e-mail does not exist already.
     */
    @Override
    public void update(Email email) throws EmailStoreException {
        Objects.requireNonNull(email);
        Objects.requireNonNull(email.getId(), "Cannot update e-mail with missing id");
        Objects.requireNonNull(email.getRelativeId(), "Cannot update e-mail with missing relative id");

        // Retrieving the relative id
        ID id = email.getId();
        UUID relativeId = id.getRelativeId();

        // Computing e-mail path
        Path queuePath = getQueuePath(id);
        Path emailPath = Paths.get(queuePath.toString(), relativeId + extension);

        // Checking existence
        if (!Files.exists(emailPath)) {
            throw new EmailStoreException("Cannot update missing e-mail");
        }

        WriteLock lock = getLock(email).writeLock();
        try {
            lock.lock();

            // Serialize the modified e-mail to file
            serialize(email, emailPath);
        } finally {
            lock.unlock();
        }
    }


    /**
     * Deletes an e-mail or fails silently if it does not exist.
     * Lock exclusive queue to prevent inconsistent reads.
     *
     * @param id the ID of the e-mail that must be deleted.
     * @throws EmailStoreException if the operation fails.
     */
    @Override
    public void delete(ID id) throws EmailStoreException {
        Path path = getEmailPath(id);

        // If it does not exist, return ("fail" silently)
        if (!Files.exists(path)) {
            return;
        }

        WriteLock lock = getLock(id).writeLock();
        try {
            lock.lock();

            // Delete the e-mail
            Files.delete(path);
        } catch (IOException e) {
            throw new EmailStoreException("Could not delete " + id, e);
        } finally {
            lock.unlock();
        }
    }


    /**
     * Reads a specific e-mail from the store.
     * Lock shared queue to ensure no write is performed during reading.
     *
     * @param id the ID of the e-mail that must be read.
     * @return the deserialized e-mail.
     * @throws EmailStoreException if the operation fails.
     */
    @Override
    public Email read(ID id) throws EmailStoreException {
        Path path = getEmailPath(id);
        File file = path.toFile();

        ReadLock lock = getLock(id).readLock();
        try {
            lock.lock();

            // Check e-mail exists in the store
            if (!Files.exists(path)) {
                throw new EmailStoreException(id + " is missing");
            }

            // Deserialize the e-mail
            Email email = deserialize(file);

            // Setting id as it's not stored inside the file
            email.setId(id);

            return email;
        } finally {
            lock.unlock();
        }
    }


    /**
     * Batch read e-emails from a queue of a specific user.
     * Lock shared queue to prevent writes while the operation is being performed.
     *
     * @param direction the chronological direction [OLDER, NEWER]
     * @param pivot the timestamp that serves as base for the read operation
     * @param user the user whose e-mails must be read
     * @param queue the queue from which to read
     * @param many the maximum number of e-mail that must be read
     *
     * @return the e-mails if the operation succeeds
     * @throws EmailStoreException if the operation fails
     */
    @Override
    public List<Email> read(Chrono direction, LocalDateTime pivot, String user, Queue queue, int many) throws EmailStoreException {
        String baseId = user + "/" + queue.asShortPath() + "/";
        Path queuePath = getQueuePath(user, queue);

        // Determines the timestamp filter criteria
        Predicate<Email> timestampFilter = switch (direction) {
            case NEWER -> (email) -> email.getTimestamp().isAfter(pivot);
            case OLDER -> (email) -> email.getTimestamp().isBefore(pivot);
        };

        // Determines the file filter criteria
        BiPredicate<Path, BasicFileAttributes> fileCriteria = (path, attrs) -> {
            return attrs.isRegularFile()
                && path.getFileName().toString().split("\\.")[1].equals(extension.substring(1));
        };

        ReadLock lock = getLock(user, queue).readLock();
        try {
            lock.lock();

            return Files.find(queuePath, 1, fileCriteria)
                    .map(Path::toFile)
                    .sorted(Comparator.comparingLong(File::lastModified))
                    .map(file -> {
                        Email email = deserialize(file);
                        String relativeId = file.getName().split("\\.")[0];
                        email.setId(ID.fromString(baseId + relativeId));
                        return email;
                    })
                    .filter(timestampFilter)
                    .limit(many)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new EmailStoreException("Could not read e-mails from " + queue.name().toLowerCase(), e);
        } finally {
            lock.unlock();
        }
    }


    /**
     * Atomic read and update operation.
     * Lock exclusive queue to prevent inconsistent reads.
     *
     * @param id the id of the e-mail that must be updated.
     * @param change a consumer that performs the change.
     * @throws EmailStoreException if the operation fails.
     */
    @Override
    public void readAndUpdate(ID id, Consumer<Email> change) throws EmailStoreException {
        WriteLock lock = getLock(id).writeLock();
        try {
            lock.lock();

            // Deserialize the e-mail
            Email email = read(id);

            // Perform the change
            change.accept(email);

            // Store the modified e-mail
            update(email);
        } finally {
            lock.unlock();
        }
    }


    /**
     * Reads all e-mails in a queue of a specific user.
     *
     * @param user The user whose e-mails must be read.
     * @param queue The queue from which to read.
     * @return The deserialized e-mails.
     * @throws EmailStoreException if the operation fails.
     */
    @Override
    public List<Email> readAll(String user, Queue queue) throws EmailStoreException {
        return read(Chrono.OLDER, LocalDateTime.now(), user, queue, Integer.MAX_VALUE);
    }


    /**
     *  Reads all e-mails of a specific user (in any queue).
     *
     * @param user The user whose e-amails must be read.
     * @return The deserialized e-mails.
     * @throws EmailStoreException if the operation fails.
     */
    @Override
    public List<Email> readAll(String user) throws EmailStoreException {
        List<Email> emails = new ArrayList<>();

        // Reads all the existing queues
        for (Queue queue : Queue.values()) {
            List<Email> queueEmails = readAll(user, queue);
            emails.addAll(queueEmails);
        }

        return emails;
    }


    /**
     * @return The path of the store base directory.
     */
    protected Path getStoreDir() {
        return storeDir;
    }


    /**
     * Computes the path to the queue based on an ID.
     *
     * @param id The id from which to extract the path.
     * @return The path to the queue.
     */
    protected Path getQueuePath(ID id) {
        return getQueuePath(id.getMailbox(), id.getQueue());
    }


    /**
     * Computes the path to the queue based on the user and the queue itself.
     *
     * @param mailbox The user (userpart@domainpart).
     * @param queue The queue.
     * @return The path to the queue.
     */
    protected Path getQueuePath(String mailbox, Queue queue) {
        String[] mailboxParts = mailbox.split("@");
        String user = mailboxParts[0];
        String domain = mailboxParts[1];

        String path = storeDir
                + "/" + domain
                + "/" + user
                + "/" + queue.asShortPath();
        return Paths.get(path);
    }


    /**
     * Computes the path to an e-mail.
     *
     * @param id The id from which to extract the path.
     * @return The path.
     */
    protected Path getEmailPath(ID id) {
        String[] mailboxParts = id.getMailbox().split("@");
        String user = mailboxParts[0];
        String domain = mailboxParts[1];

        String path = storeDir
                + "/" + domain
                + "/" + user
                + "/" + id.getQueue().asShortPath()
                + "/" + id.getRelativeId() + extension;
        return Paths.get(path);
    }


    /**
     * Serializes an e-mail to file.
     *
     * @param email The e-mail that must be serialized.
     * @param path The destination path.
     */
    protected abstract void serialize(Email email, Path path);


    /**
     * Deserializes an e-mail from file.
     *
     * @param file The file that must be deserialized.
     * @return The deserialized e-mail.
     */
    protected abstract Email deserialize(File file);


    /**
     * Retrieves a lock based e-mail ID.
     *
     * @param id The id of the e-mail.
     * @return The lock.
     */
    private ReentrantReadWriteLock getLock(ID id) {
        return getLock(id.getMailbox(), id.getQueue());
    }


    /**
     * Retrieves a lock based on an e-mail.
     *
     * @param email The e-mail.
     * @return The lock.
     */
    private ReentrantReadWriteLock getLock(Email email) {
        return getLock(email.getMailbox(), email.getQueue());
    }


    /**
     * Retrieves a lock based on the user and the queue.
     *
     * @param user The user that owns the queue.
     * @param queue The queue whose lock must me retrieved.
     * @return The lock.
     */
    private ReentrantReadWriteLock getLock(String user, Queue queue) {
        return getLock(user + "/" + queue.asShortPath());
    }


    /**
     * Retrieves an existing lock or computes a new one if it doesn't exist already.
     *
     * @param key The key representation of the lock.
     * @return The lock.
     */
    private ReentrantReadWriteLock getLock(String key) {
        return queueLocks.computeIfAbsent(key, k -> new ReentrantReadWriteLock(true));
    }

}
