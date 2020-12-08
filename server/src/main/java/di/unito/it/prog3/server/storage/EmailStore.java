package di.unito.it.prog3.server.storage;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Email.ID;
import di.unito.it.prog3.libs.email.Queue;

import java.io.FileNotFoundException;

public interface EmailStore<NoResponse, BooleanResponse, ObjectResponse, ObjectListResponse> {

    BooleanResponse userExists(String userMail);

    NoResponse store(Email email) throws EmailStoreException;

    NoResponse delete(ID email) throws EmailStoreException;

    ObjectResponse read(ID email) throws EmailStoreException, FileNotFoundException;

    ObjectListResponse read(ID offset, int many)  throws EmailStoreException;

    ObjectListResponse readAll(Queue queue)  throws EmailStoreException;

}
