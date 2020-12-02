package di.unito.it.prog3.libs.store;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Email.ID;

import java.io.FileNotFoundException;
import java.util.List;

public interface EmailStore {

    void store(Email email) throws EmailStoreException;

    void delete(ID email) throws EmailStoreException;

    Email read(ID email) throws EmailStoreException, FileNotFoundException;

    List<Email> read(ID offset, int many)  throws EmailStoreException;

    List<Email> readAll(Queue queue)  throws EmailStoreException;

    interface ServerEmailStore extends EmailStore {

        boolean userExists(String user);

        void createUser(String user);

    }

}
