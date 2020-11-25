package di.unito.it.prog3.libs.store;

import di.unito.it.prog3.libs.pojos.Email;
import di.unito.it.prog3.libs.pojos.ID;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class FileBasedEmailStore implements EmailStore {

    private final String extension;
    private final String storeDir;

    public FileBasedEmailStore(String storeDir, String extension) {
        this.extension = extension;
        this.storeDir = storeDir;
    }

    protected Path getEmailPath(ID id) {
        String path = storeDir
                + "/" + id.getMailbox().getUser()
                + "/" + id.getMailbox().getDomain()
                + "/" + id.getQueue().asPath()
                + "/" + id.getRelativeID() + extension;
        return Paths.get(path);
    }

}
