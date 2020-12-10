package di.unito.it.prog3.server.storage;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Email.ID;
import di.unito.it.prog3.libs.email.Queue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface EmailStore {

    boolean userExists(String userMail);

    void store(Email email) throws EmailStoreException, IOException;

    void delete(ID email) throws EmailStoreException;

    Email read(ID email) throws EmailStoreException, FileNotFoundException;

    List<Email> read(ID offset, int many)  throws EmailStoreException;

    List<Email> readAll(Queue queue)  throws EmailStoreException;

}
