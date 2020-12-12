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
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.stream.Collectors;

public abstract class ConcurrentFileBasedEmailStore implements EmailStore {

    private final ConcurrentMap<String, ReentrantReadWriteLock> queueLocks;

    private final String extension;
    private final Path storeDir;

    public ConcurrentFileBasedEmailStore(String storeDir, String extension) {
        this.storeDir = Paths.get(storeDir);
        this.extension = extension;
        queueLocks = new ConcurrentHashMap<>();
    }

    @Override // No lock needed as users are preregistered
    public boolean userExists(String userMail) throws EmailStoreException {
        String[] mailboxParts = userMail.split("@"); // TODO can this error?
        String user = mailboxParts[0];
        String domain = mailboxParts[1];

        Path userStore = Paths.get(getStoreDir().toString(), domain, user);

        if (!Files.exists(userStore)) {
            throw new EmailStoreException("Unknown user " + userMail);
        }

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

    @Override // Lock exclusive queue
    public void store(Email email) throws Exception {
        Path emailPath = getPath(email);

        Path queueDir = emailPath.getParent();
        if (!Files.exists(queueDir)) {
            try {
                Files.createDirectories(queueDir);
            } catch (IOException e) {
                throw new EmailStoreException("Could not create queue directory " + queueDir, e);
            }
        }

        if (email.getRelativeId() == null) {
            email.setRelativeId(UUID.randomUUID());
        }

        emailPath = Paths.get(queueDir.toString(), email.getRelativeId() + ".json");

        FileTime creationTime;
        WriteLock lock = getQueueLock(email).writeLock();
        try {
            lock.lock();

            Path file = Files.createFile(emailPath);

            serialize(email, file);

            creationTime = Files.getLastModifiedTime(file);
            /*BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
            FileTime creationTime = attrs.creationTime();
            LocalDateTime timestamp = LocalDateTime.ofInstant(creationTime.toInstant(), ZoneId.systemDefault());*/

            //email.setTimestamp(timestamp);
        } catch (IOException e) {
            throw new EmailStoreException("Could not create " + email.getId() + " file", e);
        } finally {
            lock.unlock();
        }

        LocalDateTime timestamp = LocalDateTime.ofInstant(creationTime.toInstant(), ZoneId.systemDefault());
        email.setTimestamp(timestamp);
    }

    @Override // Lock exclusive queue
    public void delete(ID id) throws EmailStoreException {
        Path path = getPath(id);

        if (!Files.exists(path)) {
            throw new EmailStoreException("Could not delete missing " + id);
        }

        WriteLock lock = getQueueLock(id).writeLock();
        try {
            lock.lock();;
            Files.delete(path);
        } catch (IOException e) {
            throw new EmailStoreException("Could not delete " + id, e);
        } finally {
            lock.unlock();
        }
    }

    @Override // Lock shared email
    public Email read(ID id) throws EmailStoreException {
        Path path = getPath(id);
        File file = path.toFile();

        ReadLock lock = getEmailLock(id).readLock();
        try {
            lock.lock();

            if (!Files.exists(path)) {
                throw new EmailStoreException(id + " is missing");
            }
        } finally {
            lock.unlock();
        }

        return deserialize(file);
    }

    @Override // Lock shared queue
    public List<Email> read(String mailbox, Queue queue, int many) throws EmailStoreException {
        List<Email> emails = new ArrayList<>();
        Path queuePath = getQueuePath(mailbox, queue);

        ReadLock lock = getQueueLock(mailbox, queue).readLock();
        try {
            lock.lock();

            List<Path> emailPathsOrdered = Files.list(queuePath)
                    .map(Path::toFile)
                    .sorted(Comparator.comparingLong(File::lastModified).reversed())
                    .map(File::toPath)
                    .limit(many)
                    .collect(Collectors.toList());

            int toBeDeserialized = Math.min(many, emailPathsOrdered.size());
            for (int i = 0; i < toBeDeserialized; i++) {
                Path emailPath = emailPathsOrdered.get(i);
                Email deserializedEmail = deserialize(emailPath);

                ID id = ID.fromString(
                        mailbox
                                + "/" + queue.asShortPath()
                                + "/" + emailPath.getFileName().toString().split("\\.")[0]);
                deserializedEmail.setId(id);

                FileTime lastModifiedTime = Files.getLastModifiedTime(emailPath);
                LocalDateTime timestamp = LocalDateTime.ofInstant(lastModifiedTime.toInstant(), ZoneId.systemDefault());
                deserializedEmail.setTimestamp(timestamp);

                emails.add(deserializedEmail);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return emails;
    }

    @Override // Lock shared queue
    public List<Email> read(Chrono direction, ID offset, int many) {
        List<Email> emails = new ArrayList<>();
        Path offsetPath = getPath(offset);
        Path queuePath = offsetPath.getParent();

        ReadLock lock = getQueueLock(offset).readLock();
        try {
            lock.lock();

            // From most recent to oldest
            List<Path> emailPathsOrdered = Files.list(queuePath)
                    .map(Path::toFile)
                    .sorted(Comparator.comparingLong(File::lastModified).reversed())
                    .map(File::toPath)
                    .collect(Collectors.toList());

            // Reach and discard offset
            for (Path emailPath : emailPathsOrdered) {
                emailPathsOrdered.remove(emailPath);
                if (Files.isSameFile(emailPath, offsetPath)) {
                    break;
                }
            }

            // Deserialize min{many, emailPathsOrder.size()} e-mails
            int toBeDeserialized = Math.min(many, emailPathsOrdered.size());
            for (int i = 0; i < toBeDeserialized; i++) {
                Path emailPath = emailPathsOrdered.get(i);
                Email deserializedEmail = deserialize(emailPath);

                emails.add(deserializedEmail);
            }
        } catch (IOException | EmailStoreException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return emails;
    }

    @Override
    public List<Email> readNewer(ID offset) throws EmailStoreException {
        List<Email> emails = new ArrayList<>();
        Path offsetPath = getPath(offset);
        Path queuePath = offsetPath.getParent();

        ReadLock lock = getQueueLock(offset).readLock();
        try {
            lock.lock();

            List<Path> emailPathsOrdered = Files.list(queuePath)
                    .map(Path::toFile)
                    .sorted(Comparator.comparingLong(File::lastModified).reversed())
                    .map(File::toPath)
                    .collect(Collectors.toList());

            // Reach and discard offset
            for (Path emailPath : emailPathsOrdered) {
                if (Files.isSameFile(emailPath, offsetPath)) {
                    break;
                }

                Email deserializedEmail = deserialize(emailPath);

                ID id = ID.fromString(
                        offset.getMailbox()
                                + "/" + offset.getQueue().asShortPath()
                                + "/" + emailPath.getFileName().toString().split("\\.")[0]);
                deserializedEmail.setId(id);

                FileTime lastModifiedTime = Files.getLastModifiedTime(emailPath);
                LocalDateTime timestamp = LocalDateTime.ofInstant(lastModifiedTime.toInstant(), ZoneId.systemDefault());
                deserializedEmail.setTimestamp(timestamp);

                emails.add(deserializedEmail);
            }
        } catch (IOException e) {
            throw new EmailStoreException("Could not retrieve new e-mails");
        } finally {
            lock.unlock();
        }

        return emails;
    }

    @Override
    public List<Email> readAll(String mailbox, Queue queue) throws IOException, EmailStoreException {
        List<Email> emails = new ArrayList<>();
        Path queuePath = getQueuePath(mailbox, queue);

        ReadLock lock = getQueueLock(mailbox, queue).readLock();
        try {
            lock.lock();

            List<Path> emailPathsOrdered = Files.list(queuePath)
                    .map(Path::toFile)
                    .sorted(Comparator.comparingLong(File::lastModified).reversed())
                    .map(File::toPath)
                    .collect(Collectors.toList());

            for (Path emailPath : emailPathsOrdered) {
                Email deserializedEmail = deserialize(emailPath);

                ID id = ID.fromString(
                        mailbox
                                + "/" + queue.asShortPath()
                                + "/" + emailPath.getFileName().toString().split("\\.")[0]);
                deserializedEmail.setId(id);

                FileTime lastModifiedTime = Files.getLastModifiedTime(emailPath);
                LocalDateTime timestamp = LocalDateTime.ofInstant(lastModifiedTime.toInstant(), ZoneId.systemDefault());
                deserializedEmail.setTimestamp(timestamp);

                emails.add(deserializedEmail);
            }
        } finally {
            lock.unlock();
        }

        return emails;
    }

    protected Path getStoreDir() {
        return storeDir;
    }

    protected Path getQueuePath(ID id) {
        return getQueuePath(id.getMailbox(), id.getQueue());
    }

    protected Path getQueuePath(String mailbox, Queue queue) {
        String[] mailboxParts = mailbox.split("@"); // TODO can this error?
        String user = mailboxParts[0];
        String domain = mailboxParts[1];

        String path = storeDir
                + "/" + domain
                + "/" + user
                + "/" + queue.asShortPath();
        return Paths.get(path);
    }

    protected Path getPath(Email email) {
        return getPath(email.getId());
    }

    protected Path getPath(ID id) {
        String[] mailboxParts = id.getMailbox().split("@"); // TODO can this error?
        String user = mailboxParts[0];
        String domain = mailboxParts[1];

        String path = storeDir
                + "/" + domain
                + "/" + user
                + "/" + id.getQueue().asShortPath()
                + "/" + id.getRelativeId() + extension;
        return Paths.get(path);
    }

    protected abstract void serialize(Email email, Path path) throws EmailStoreException;

    protected abstract Email deserialize(Path path) throws EmailStoreException;

    protected abstract Email deserialize(File file) throws EmailStoreException;


    private ReentrantReadWriteLock getQueueLock(ID id) {
        return getQueueLock(id.getMailbox(), id.getQueue());
    }

    private ReentrantReadWriteLock getQueueLock(Email email) {
        return getQueueLock(email.getMailbox(), email.getQueue());
    }

    private ReentrantReadWriteLock getQueueLock(String mailbox, Queue queue) {
        String key = mailbox + "/" + queue.asShortPath();
        return getLock(key);
    }

    private ReentrantReadWriteLock getEmailLock(ID id) {
        String key = id.getMailbox() + "/" + id.getQueue().asShortPath() + "/" + id.getRelativeId();
        return getLock(key);
    }

    private ReentrantReadWriteLock getLock(String key) {
        System.out.println("Queue lock: " + key);
        return queueLocks.computeIfAbsent(key, k -> new ReentrantReadWriteLock(true));
    }

}
