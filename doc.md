

GET /login/{email}
    boolean userExists(String userMail);

POST /email
    void store(Email email) throws EmailStoreException;

DELETE /email/{id}
    void delete(ID email) throws EmailStoreException;

GET /email/n=1,id={id}
    Email read(ID email) throws EmailStoreException, FileNotFoundException;

GET /email/n={many},id={offset}
    List<Email> read(ID offset, int many)  throws EmailStoreException;

    List<Email> readAll(Queue queue)  throws EmailStoreException;

    interface ServerEmailStore extends EmailStore {

        boolean userExists(String user);

        void createUser(String user);

    }