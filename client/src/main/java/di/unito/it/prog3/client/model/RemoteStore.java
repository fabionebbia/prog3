package di.unito.it.prog3.client.model;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.store.EmailStore;
import di.unito.it.prog3.libs.store.EmailStoreException;
import di.unito.it.prog3.libs.store.Queue;

import java.io.FileNotFoundException;
import java.util.List;

public class RemoteStore implements EmailStore {

    @Override
    public boolean userExists(String userMail) {
        return true;
    }

    @Override
    public void store(Email email) throws EmailStoreException {

    }

    @Override
    public void delete(Email.ID email) throws EmailStoreException {

    }

    @Override
    public Email read(Email.ID email) throws EmailStoreException, FileNotFoundException {
        return null;
    }

    @Override
    public List<Email> read(Email.ID offset, int many) throws EmailStoreException {
        return null;
    }

    @Override
    public List<Email> readAll(Queue queue) throws EmailStoreException {
        return null;
    }
}
