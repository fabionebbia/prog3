package di.unito.it.prog3.server.storage;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import di.unito.it.prog3.libs.email.Email;

import java.io.IOException;
import java.nio.file.Path;

public class ConcurrentJsonEmailStore extends ConcurrentFileBasedEmailStore {

    private final ObjectReader reader;
    private final ObjectWriter writer;

    public ConcurrentJsonEmailStore(String storeDir) {
        super(storeDir, ".json");

        ObjectMapper mapper = new ObjectMapper();

        // configure to write timestamps as strings instead of structured objects
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // configure to access private fields too
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        // Immutable and thread safe
        reader = mapper.reader();
        writer = mapper.writer();
    }

    @Override
    protected void serialize(Email email, Path path) throws EmailStoreException {
        try {
            writer.writeValue(path.toFile(), email);
        } catch (IOException e) {
            throw new EmailStoreException("Could not serialize e-mail " + email + " to path " + path, e);
        }
    }

    @Override
    protected Email deserialize(Path path) throws EmailStoreException {
        try {
            return reader.readValue(path.toFile(), Email.class);
        } catch (IOException e) {
            throw new EmailStoreException("Could not deserialize e-mail from path " + path, e);
        }
    }

}
