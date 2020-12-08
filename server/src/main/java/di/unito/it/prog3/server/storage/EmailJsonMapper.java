package di.unito.it.prog3.server.storage;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import di.unito.it.prog3.libs.email.Email;

import java.io.IOException;
import java.nio.file.Path;

public class EmailJsonMapper extends ObjectMapper {

    public EmailJsonMapper() {
        // configure to write timestamps as strings instead of structured objects
        registerModule(new JavaTimeModule());
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // configure to access private fields too
        setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    }

    public Email deserialize(Path path) throws IOException {
        return readValue(path.toFile(), Email.class);
    }

    public String serialize(Email email) throws JsonProcessingException {
        return writeValueAsString(email);
    }

    public void serialize(Email email, Path path) throws IOException {
        writeValue(path.toFile(), email);
    }

}
