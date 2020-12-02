package di.unito.it.prog3.libs.store;

import di.unito.it.prog3.libs.pojos.Email;
import di.unito.it.prog3.libs.pojos.ID;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBasedEmailStoreClient extends FileBasedEmailStore {

    private final EmailJsonMapper json;

    public FileBasedEmailStoreClient(String storeDir, String extension) {
        super(storeDir, extension);
        json = new EmailJsonMapper();
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
            throw new EmailStoreException("E-mail already exists" + email.getID());
        }

        try {
            Files.createFile(emailPath);
        } catch (IOException e) {
            throw new EmailStoreException("Could not create " + email.getID() + " file", e);
        }

        try {
            json.serialize(email, emailPath);
        } catch (IOException e) {
            throw new EmailStoreException("Could not serialize " + email.getID(), e);
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

        try {
            return json.deserialize(file);
        } catch (IOException e) {
            throw new EmailStoreException("Could not read " + id, e);
        }
    }

    @Override
    public List<Email> read(ID offset, int many) {
        List<Email> emails = new ArrayList<>();
        Path offsetPath = getPath(offset);
        Path queuePath = offsetPath.getParent();

        try {


            Files.list(queuePath)
                    .sorted()
                    .map(Path::getFileName)
                    .map(Path::toString)
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
}
