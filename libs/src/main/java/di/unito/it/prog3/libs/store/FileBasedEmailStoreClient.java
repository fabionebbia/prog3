package di.unito.it.prog3.libs.store;

import di.unito.it.prog3.libs.pojos.Email;
import di.unito.it.prog3.libs.pojos.ID;
import di.unito.it.prog3.libs.utils.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class FileBasedEmailStoreClient extends FileBasedEmailStore {

    public FileBasedEmailStoreClient(String storeDir, String extension) {
        super(storeDir, extension);
    }

    @Override
    public void store(Email email) throws EmailStoreException {
        Path path = getEmailPath(email.getID());

        File directory = path.getParent().toFile();
        if (!directory.exists() && !directory.mkdirs()) {
            throw new EmailStoreException("Could not create directories");
        }

        File file = path.toFile();
        if (file.exists()) {
            throw new EmailStoreException("Email already exists");
        }

        try {
            if (!file.createNewFile()) {
                throw new EmailStoreException("Could not create e-mail file");
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(email.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(ID email) throws EmailStoreException {
        Path path = getEmailPath(email);
        File file = path.toFile();
        if (!file.exists()) {
            Log.warn(email + " deletion requested, but file was missing already");
        } else if (!file.delete()) {
            throw new EmailStoreException("Could not delete e-email");
        }
    }

    @Override
    public Email read(ID email) throws EmailStoreException {
        Path path = getEmailPath(email);
        File file = path.toFile();
        if (!file.exists()) {
            throw new EmailStoreException("Cannot read missing " + email + " e-mail");
        }
        return null;
    }

    @Override
    public List<Email> read(Queue queue, ID offset, int many) {
        return null;
    }

    @Override
    public List<Email> readAll(Queue queue) {
        return null;
    }
}
