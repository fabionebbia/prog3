package di.unito.it.prog3.libs.store;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Email.ID;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class FileBasedEmailStore extends LocalEmailStore {

    private final String extension;
    private final Path storeDir;

    public FileBasedEmailStore(String storeDir, String extension) {
        this.storeDir = Paths.get(storeDir);
        this.extension = extension;
    }

    protected Path getStoreDir() {
        return storeDir;
    }

    protected Path getPath(Email email) {
        return getPath(email.getId());
    }

    protected Path getPath(ID id) {
        String path = storeDir
                + "/" + id.getMailbox().getUser()
                + "/" + id.getMailbox().getDomain()
                + "/" + id.getQueue().asShortPath()
                + "/" + id.getRelativeId() + extension;
        return Paths.get(path);
    }
}
