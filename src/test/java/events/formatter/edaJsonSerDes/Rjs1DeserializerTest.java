package events.formatter.edaJsonSerDes;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import events.IMessage;
import events.formatter.EDAFormatterConstants;
import events.formatter.Envelope;
import events.formatter.IProvideSchema;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Rjs1DeserializerTest {

  private Rjs1Deserializer deserializer;
  private IProvideSchema provider;

  @BeforeEach
  public void setUp() throws Exception {
    this.provider = mock(IProvideSchema.class);
    this.deserializer = new Rjs1Deserializer(this.provider);
  }

  @Test
  void testItThrowsOnNotValidJsonString() {
    String invalidJson = "invalid";
    when(this.provider.get()).thenReturn("{}");
    assertThrows(
        Exception.class,
        () -> {
          this.deserializer.deserialize(
              Envelope.v1(UUID.randomUUID().toString(), EDAFormatterConstants.RSJ1,
                  invalidJson.getBytes(StandardCharsets.UTF_8)));
        });
  }

  @Test
  void testItThrowsOnNonSchemaCompliantJson() {
    String nonCompliantJson = "{}";
    when(this.provider.get())
        .thenReturn(
            "{\n"
                + "  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n"
                + "  \"type\": \"object\",\n"
                + "  \"properties\": {\n"
                + "    \"somefield\": {\n"
                + "      \"type\": \"string\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"required\": [ \"somefield\" ]\n"
                + "}");
    assertThrows(
        Exception.class,
        () -> {
          this.deserializer.deserialize(
              Envelope.v1(UUID.randomUUID().toString(), EDAFormatterConstants.RSJ1,
                  nonCompliantJson.getBytes(StandardCharsets.UTF_8)));
        });
  }

  @Test
  void testItThrowsOnNonCompatibleEnvelope() throws Exception {
    String json = "{}";
    when(this.provider.get()).thenReturn("{}");

    Map<String, String> header = new HashMap<String, String>();
    header.put("headerVersion", "0");

    assertThrows(
        Exception.class,
        () -> {
          this.deserializer
              .deserialize(new Envelope(header, json.getBytes(StandardCharsets.UTF_8)));
        });
  }

  @Test
  void testItDeserializesCorrectlyIntoAMessageForValidJson() throws Exception {
    String validJson =
        "{\"id\": \"622178f0-7dc8-41b6-88a9-2ce0f0934066\",\"name\": \"event_name\",\"category\": \"event\",\"payload\": {\"field\": \"value\"},\"occurred_at\": \"2020-09-15T15:53:00+01:00\",\"version\": 1}";
    when(this.provider.get()).thenReturn(new EDAJsonHardCodedSchemaProvider().get());

    IMessage message =
        this.deserializer.deserialize(
            Envelope.v1(UUID.randomUUID().toString(), EDAFormatterConstants.RSJ1,
                validJson.getBytes(StandardCharsets.UTF_8)));
    assertEquals(message.getName(), "event_name");
    assertEquals(message.getCategory(), "event");
    assertEquals(message.getVersion(), 1);
    assertEquals(message.getOccurredAt(), new DateTime("2020-09-15T15:53:00+01:00"));
    assertEquals(message.getPayload().get("field"), "value");
    assertEquals(message.getPayload().size(), 1);
  }
}
