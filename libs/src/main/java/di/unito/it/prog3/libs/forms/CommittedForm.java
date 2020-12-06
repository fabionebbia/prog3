package di.unito.it.prog3.libs.forms;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CommittedForm<K extends Enum<K>> {

    @JsonSerialize(keyUsing = CommittedForm.FormKeySerializer.class)
    private final Map<K, Object> fields;

    public CommittedForm(Map<K, FormField<?>> fields) {
        this.fields = new LinkedHashMap<>();

        for (Map.Entry<K, FormField<?>> entry : fields.entrySet()) {
            this.fields.put(entry.getKey(), entry.getValue().get());
        }
    }

    // "any getter" needed for serialization
    @JsonAnyGetter
    public Map<K, Object> any() {
        return fields;
    }

    @JsonAnySetter
    public void set(K key, Object value) {
        throw new UnsupportedOperationException("");
        //fields.put(key, value);
    }


    public static final class FormKeySerializer<K  extends Enum<K>> extends StdSerializer<K> {

        public FormKeySerializer() {
            super(TypeFactory.unknownType());
        }

        @Override
        public void serialize(K value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            StringBuilder sb = new StringBuilder();

            String[] tokens = value.name().toLowerCase().split("_+");
            sb.append(tokens[0]);

            for(int i = 1; i < tokens.length; i++) {
                String capitalizedToken = tokens[i]
                        .substring(0, 1)
                        .toUpperCase()
                        .concat(tokens[i].substring(1));
                sb.append(capitalizedToken);
            }

            gen.writeFieldName(sb.toString());
        }
    }

}
