package di.unito.it.prog3.libs.net;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapper extends ObjectMapper {

    public JsonMapper() {
        // Prevents JsonMapper from automatically closing the socket
        // on read/write operation completion
        JsonFactory defaultJsonFactory = getFactory();
        defaultJsonFactory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        defaultJsonFactory.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    }

}
