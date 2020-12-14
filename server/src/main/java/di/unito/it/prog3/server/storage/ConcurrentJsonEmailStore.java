package di.unito.it.prog3.server.storage;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.net.JsonMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ConcurrentJsonEmailStore extends ConcurrentFileBasedEmailStore {

    private final ObjectReader reader;
    private final ObjectWriter writer;


    /**
     * Sets up the e-mail store to use json a file type.
     *
     * @param storeDir The base store directory.
     */
    public ConcurrentJsonEmailStore(String storeDir) {
        super(storeDir, ".json");

        JsonMapper mapper = new JsonMapper();

        // configure to access private fields too
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        // do not serialize information that must be computed to file
        String[] ignoreFields = { "id" /*, "timestamp" */};
        mapper.addMixIn(Object.class, PropertyFilterMixIn.class);
        FilterProvider writeFilter = new SimpleFilterProvider()
                .addFilter("filter properties by name",
                        SimpleBeanPropertyFilter.serializeAllExcept(ignoreFields));

        // Immutable and thread safe
        reader = mapper.reader();
        writer = mapper.writer(writeFilter);
    }


    // A filter for properties that should be volatile
    @JsonFilter("filter properties by name")
    static class PropertyFilterMixIn {}


    /**
     * Serializes the given e-mail to the given path.
     *
     * @param email The e-mail that must be serialized.
     * @param path The destination path.
     */
    @Override
    protected void serialize(Email email, Path path) {
        try {
            writer.writeValue(path.toFile(), email);
        } catch (IOException e) {
            throw new RuntimeException("Could not serialize e-mail " + email + " to path " + path, e);
        }
    }


    /**
     * Deserializes an e-mail from the given file.
     *
     * @param file The file that must be deserialized.
     * @return The deserialized e-mail.
     */
    @Override
    protected Email deserialize(File file) {
        try {
            return reader.readValue(file, Email.class);
        } catch (IOException e) {
            throw new RuntimeException("Could not deserialize e-mail from path " + file.getPath(), e);
        }
    }

}
