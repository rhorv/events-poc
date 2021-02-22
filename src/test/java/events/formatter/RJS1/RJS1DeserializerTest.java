package events.formatter.RJS1;

import events.IMessage;
import events.formatter.IProvideSchema;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class RJS1DeserializerTest {

    private RJS1Deserializer deserializer;
    private IProvideSchema provider;

    @BeforeEach
    public void setUp() throws Exception {
        this.provider = mock(IProvideSchema.class);
        this.deserializer = new RJS1Deserializer(this.provider);
    }
    @Test
    void testItThrowsOnNotValidJsonString() {
        String invalidJson = "invalid";
        when(this.provider.get()).thenReturn("{}");
        assertThrows(Exception.class, () -> {
            this.deserializer.deserialize(invalidJson);
        });
    }

    @Test
    void testItThrowsOnInvalidJsonBySchema() {
        String nonCompliantJson = "{}";
        when(this.provider.get()).thenReturn("{\n" +
                "  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"somefield\": {\n" +
                "      \"type\": \"string\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"required\": [ \"somefield\" ]\n" +
                "}");
        assertThrows(Exception.class, () -> {
            this.deserializer.deserialize(nonCompliantJson);
        });
    }

    @Test
    void testItDeserializesCorrectlyIntoAMessageForValidJson() throws Exception {
        String validJson = "{\n" +
                "\"id\": \"622178f0-7dc8-41b6-88a9-2ce0f0934066\",\n" +
                "\"name\": \"event_name\",\n" +
                "\"category\": \"event\",\n" +
                "\"payload\": {\n" +
                "  \"field\": \"value\"\n" +
                "},\n" +
                "\"occurred_at\": \"2020-09-15T15:53:00+01:00\",\n" +
                "\"version\": 1\n" +
                "}\n";
        when(this.provider.get()).thenReturn(new HardCodedSchemaProvider().get());

        IMessage message = this.deserializer.deserialize(validJson);
        assertEquals(message.getName(), "event_name");
        assertEquals(message.getCategory(), "event");
        assertEquals(message.getVersion(), 1);
        assertEquals(message.getOccurredAt(), new DateTime("2020-09-15T15:53:00+01:00"));
        assertEquals(message.getPayload().get("field"), "value");
        assertEquals(message.getPayload().size(), 1);
    }
}