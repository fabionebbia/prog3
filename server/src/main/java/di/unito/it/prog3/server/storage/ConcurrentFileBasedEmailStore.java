package di.unito.it.prog3.server.storage;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Email.ID;
import di.unito.it.prog3.libs.email.Queue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.*;
import java.util.function.Predicate;

public abstract class ConcurrentFileBasedEmailStore implements EmailStore {

    private final ConcurrentMap<String, ReentrantReadWriteLock> queueLocks;

    private final String extension;
    private final Path storeDir;

    public ConcurrentFileBasedEmailStore(String storeDir, String extension) {
        this.storeDir = Paths.get(storeDir);
        this.extension = extension;
        queueLocks = new ConcurrentHashMap<>();
    }

    @Override
    public boolean userExists(String userMail) {
        String[] mailboxParts = userMail.split("@"); // TODO can this error?
        String user = mailboxParts[0];
        String domain = mailboxParts[1];

        Path userStore = Paths.get(getStoreDir().toString(), domain, user);
        return Files.exists(userStore);
    }

    @Override
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

        try {
            WriteLock lock = getQueueWriteLock(email);
            lock.tryLock();

            Path created = Files.createFile(emailPath);

            serialize(email, emailPath);

            BasicFileAttributes attrs = Files.readAttributes(created, BasicFileAttributes.class);
            FileTime creationTime = attrs.creationTime();
            LocalDateTime timestamp = LocalDateTime.ofInstant(creationTime.toInstant(), ZoneId.systemDefault());

            email.setTimestamp(timestamp);
        } catch (IOException e) {
            throw new EmailStoreException("Could not create " + email.getId() + " file", e);
        }
    }

    @Override
    public void delete(ID id) throws EmailStoreException {
        Path path = getPath(id);

        if (!Files.exists(path)) {
            throw new EmailStoreException("Could not delete missing " + id);
        }

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new EmailStoreException("Could not delete " + id, e);
        }
    }

    @Override
    public Email read(ID id) throws EmailStoreException {
        Path path = getPath(id);
        File file = path.toFile();

        if (!Files.exists(path)) {
            throw new EmailStoreException(id + " is missing");
        }

        return deserialize(file.toPath());
    }

    @Override
    public List<Email> read(ID offset, int many) {
        List<Email> emails = new ArrayList<>();
        Path queuePath = getPath(offset).getParent();

        try {

            int n = 0;
            Files.list(queuePath)
                    .map(Path::toFile)
                    .sorted(Comparator.comparingLong(File::lastModified))
                    .map(File::getName)
                    .takeWhile(new Predicate<String>() {
                        @Override
                        public boolean test(String s) {
                            return false;
                        }
                    })
                    .forEach(System.out::println);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return emails;
    }

    @Override
    public List<Email> readAll(Queue queue) {
        return null;
    }

    protected Path getStoreDir() {
        return storeDir;
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

    private ReentrantReadWriteLock getQueueLock(Email email) {
        return getQueueLock(email.getMailbox(), email.getQueue());
    }

    private ReentrantReadWriteLock getQueueLock(String mailbox, Queue queue) {
        String key = mailbox + "." + queue.asShortPath();
        System.out.println("Queue lock: " + key);
        return queueLocks.computeIfAbsent(key, k -> new ReentrantReadWriteLock());
    }

    private ReentrantReadWriteLock.ReadLock getQueueReadLock(Email email) {
        return getQueueLock(email).readLock();
    }

    private ReentrantReadWriteLock.WriteLock getQueueWriteLock(Email email) {
        return getQueueLock(email).writeLock();
    }

}
