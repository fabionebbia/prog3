package di.unito.it.prog3.libs.store;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Email.ID;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class LocalFileBasedEmailStore extends FileBasedEmailStore {


    public LocalFileBasedEmailStore(String storeDir, String extension) {
        super(storeDir, extension);
    }

    @Override
    public boolean userExists(String userMail) {
        Path userStore = Paths.get(getStoreDir().toString(), userMail);
        System.out.println(userStore);
        return Files.exists(userStore);
    }

    @Override
    public void store(Email email) throws EmailStoreException {
        Path emailPath = getPath(email);

        Path queueDir = emailPath.getParent();
        if (!Files.exists(queueDir)) {
            try {
                Files.createDirectories(queueDir);
            } catch (IOException e) {
                throw new EmailStoreException("Could not create queue directory " + queueDir, e);
            }
        }

        if (Files.exists(emailPath)) {
            throw new EmailStoreException("E-mail already exists" + email);
        }

        emailPath = Paths.get(queueDir.toString(), UUID.randomUUID().toString() + ".json");
        /* try {

            Files.createFile(emailPath);
        } catch (IOException e) {
            throw new EmailStoreException("Could not create " + email.getID() + " file", e);
        } */

        serialize(email, emailPath);
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

    protected abstract void serialize(Email email, Path path) throws EmailStoreException;

    protected abstract Email deserialize(Path path) throws EmailStoreException;

}
