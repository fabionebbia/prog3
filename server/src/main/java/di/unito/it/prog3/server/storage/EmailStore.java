package di.unito.it.prog3.server.storage;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Email.ID;
import di.unito.it.prog3.libs.email.Queue;
import di.unito.it.prog3.libs.net.Chrono;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface EmailStore {

    boolean userExists(String user) throws EmailStoreException;

    void store(Email email) throws EmailStoreException;

    void update(Email email) throws EmailStoreException;

    void delete(ID email) throws EmailStoreException;

    Email read(ID email) throws EmailStoreException, FileNotFoundException;

    void readAndUpdate(ID email, Consumer<Email> change) throws EmailStoreException;

    List<Email> read(String mailbox, Queue queue, int many) throws EmailStoreException;

    List<Email> read(Chrono direction, ID offset, int many)  throws EmailStoreException;

    List<Email> read(Chrono direction, LocalDateTime pivot, String user, Queue queue, int many) throws EmailStoreException;

    List<Email> readAll(String user, Queue queue) throws EmailStoreException;

    List<Email> readAll(String mailbox) throws EmailStoreException;

}
