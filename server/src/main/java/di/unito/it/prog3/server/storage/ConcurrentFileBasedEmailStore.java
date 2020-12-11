package di.unito.it.prog3.server.storage;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Email.ID;
import di.unito.it.prog3.libs.email.Queue;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.function.Predicate;

public abstract class ConcurrentFileBasedEmailStore implements EmailStore {

    private final String extension;
    private final Path storeDir;

    public ConcurrentFileBasedEmailStore(String storeDir, String extension) {
        this.storeDir = Paths.get(storeDir);
        this.extension = extension;
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

        emailPath = Paths.get(queueDir.toString(), UUID.randomUUID().toString() + ".json");

        FileLock lock = lockExclusive(queueDir);


        try {
            Files.createFile(emailPath);
        } catch (IOException e) {
            throw new EmailStoreException("Could not create " + email.getId() + " file", e);
        }

        serialize(email, emailPath);

        lock.release();
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

    private FileLock lockExclusive(Path path) throws IOException {
        FileChannel channel = FileChannel.open(path, StandardOpenOption.APPEND);
        return channel.lock(0, Long.MAX_VALUE, false);
    }

    private FileLock lockShared(Path path) throws IOException {
        FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);
        return channel.lock(0, Long.MAX_VALUE, true);
    }

    protected abstract void serialize(Email email, Path path) throws EmailStoreException;

    protected abstract Email deserialize(Path path) throws EmailStoreException;

}
