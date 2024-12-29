package pl.app.common.shared.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/*
 * Changing the serialization and deserialization of the  org.bson.types.ObjectId object to look more or less like this: {"id":"669ba617e3f38965343a34ec",...}
 * */
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .serializers(new ObjectIdSerializer(), new InstantSerializer())
                .deserializers(new ObjectIdDeserializer(), new InstantDeserializer())
                ;
    }

    public static class ObjectIdSerializer extends StdSerializer<ObjectId> {

        public ObjectIdSerializer() {
            super(ObjectId.class);
        }

        @Override
        public void serialize(ObjectId objectId, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(objectId.toHexString());
        }

    }

    public static class ObjectIdDeserializer extends StdDeserializer<ObjectId> {

        public ObjectIdDeserializer() {
            super(ObjectId.class);
        }

        @Override
        public ObjectId deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            String value = jsonParser.getValueAsString();
            if (Objects.isNull(value) || value.length() != 24) {
                return null;
            } else {
                return new ObjectId(value);
            }
        }

    }

    public static class InstantSerializer extends StdSerializer<Instant> {

        public InstantSerializer() {
            super(Instant.class);
        }

        @Override
        public void serialize(Instant object, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
            jsonGenerator.writeString(formatter.format(object));
        }
    }

    public static class InstantDeserializer extends StdDeserializer<Instant> {

        public InstantDeserializer() {
            super(Instant.class);
        }

        @Override
        public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            String value = jsonParser.getValueAsString();
            if (Objects.isNull(value)) {
                return null;
            } else {
                return Instant.from(DateTimeFormatter.ISO_INSTANT.parse(value));
            }
        }

    }

    public static class PageDeserializer extends StdDeserializer<Page<?>> implements ContextualDeserializer {

        private JavaType wrapperType;
        private JavaType contentType;

        public PageDeserializer() {
            super(Page.class);
        }

        public PageDeserializer(JavaType wrapperType, JavaType contentType) {
            super(Page.class);
            this.wrapperType = wrapperType;
            this.contentType = contentType;
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
            JavaType wrapperType = ctxt.getContextualType();
            JavaType contentType = wrapperType.containedType(0);
            return new PageDeserializer(wrapperType, contentType);
        }

        @Override
        public Page<?> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            JsonNode contentNode = node.get("content");
            ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
            CollectionType listType = mapper.getTypeFactory().constructCollectionType(List.class, contentType);
            List<?> content = mapper.readValue(contentNode.traverse(mapper), listType);

            int number = node.get("number").asInt();
            int size = node.get("size").asInt();
            long totalElements = node.get("totalElements").asLong();

            Pageable pageable = PageRequest.of(number, size);
            return new PageImpl<>(content, pageable, totalElements);
        }
    }
}
