package di.unito.it.prog3.server.storage;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Email.ID;
import di.unito.it.prog3.libs.email.Queue;
import di.unito.it.prog3.libs.net.Chrono;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface EmailStore {

    boolean userExists(String userMail) throws EmailStoreException;

    void store(Email email) throws Exception;

    void delete(ID email) throws EmailStoreException;

    Email read(ID email) throws EmailStoreException, FileNotFoundException;

    List<Email> read(String mailbox, Queue queue, int many) throws EmailStoreException;

    List<Email> readNewer(ID offset) throws EmailStoreException;

    List<Email> read(Chrono direction, ID offset, int many)  throws EmailStoreException;

    List<Email> readAll(String mailbox, Queue queue) throws EmailStoreException, IOException;

}
