package di.unito.it.prog3.libs.net;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonMapper extends ObjectMapper {

    public JsonMapper() {
        // Write timestamps as strings instead of structured objects
        registerModule(new JavaTimeModule());
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Prevents JsonMapper from automatically closing the socket
        // when the serialization/deserialization completes
        JsonFactory defaultJsonFactory = getFactory();
        defaultJsonFactory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        defaultJsonFactory.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);

        // Excludes null fields
        setSerializationInclusion(Include.NON_NULL);

        // Register request types
        registerSubtypes(
                LoginRequest.class,
                ReadRequest.class,
                SendRequest.class,
                OpenRequest.class,
                DeletionRequest.class
        );
    }

}
