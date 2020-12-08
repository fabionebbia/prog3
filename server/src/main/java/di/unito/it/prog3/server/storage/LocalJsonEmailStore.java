package di.unito.it.prog3.server.storage;

import di.unito.it.prog3.libs.email.Email;

import java.io.IOException;
import java.nio.file.Path;

public class LocalJsonEmailStore extends LocalFileBasedEmailStore {

    private final EmailJsonMapper json;

    public LocalJsonEmailStore(String storeDir) {
        super(storeDir, ".json");
        json = new EmailJsonMapper();
    }

    @Override
    protected void serialize(Email email, Path path) throws EmailStoreException {
        try {
            json.serialize(email, path);
        } catch (IOException e) {
            throw new EmailStoreException("Could not serialize e-mail " + email + " to path " + path, e);
        }
    }

    @Override
    protected Email deserialize(Path path) throws EmailStoreException {
        try {
            return json.deserialize(path);
        } catch (IOException e) {
            throw new EmailStoreException("Could not deserialize e-mail from path " + path, e);
        }
    }

}
