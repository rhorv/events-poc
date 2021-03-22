package events.formatter.edaJsonSerDes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import events.IMessage;
import events.Message;
import events.formatter.EdaFormatterConstants;
import events.formatter.Envelope;
import events.formatter.IProvideSchema;
import events.formatter.ISerializeMessage;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SerializedValidatorDecoratorTest {

  private SerializedValidatorDecorator decorator;
  private IProvideSchema provider;
  private ISerializeMessage serializer;

  @BeforeEach
  public void setUp() throws Exception {
    this.provider = mock(IProvideSchema.class);
    this.serializer = mock(ISerializeMessage.class);
    this.decorator = new SerializedValidatorDecorator(this.serializer, this.provider);
  }

  @Test
  void testItThrowsOnNonSchemaCompliantJson() throws Exception {
    when(this.provider.get()).thenReturn("{\n" +
        "  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n" +
        "  \"type\": \"object\",\n" +
        "  \"properties\": {\n" +
        "    \"somefield\": {\n" +
        "      \"type\": \"string\",\n" +
        "    }\n" +
        "  },\n" +
        "  \"required\": [ \"somefield\" ]\n" +
        "}");
    UUID uuid = UUID.randomUUID();
    IMessage message = new Message("name", uuid.toString(), new HashMap<String, String>(), 1, new DateTime(),
        "event");
    Envelope emptyResponse = Envelope.v1(uuid.toString(), EdaFormatterConstants.EDA_JSON_GENERIC,
        "{}".getBytes(StandardCharsets.UTF_8));
    when(this.serializer.serialize(message)).thenReturn(emptyResponse);

    assertThrows(Exception.class, () -> {
      this.decorator.serialize(message);
    });
  }

  @Test
  void testItThrowsOnInvalidJson() throws Exception {
    when(this.provider.get()).thenReturn("{}");

    UUID uuid = UUID.randomUUID();
    IMessage message = new Message("name", uuid.toString(), new HashMap<String, String>(), 1, new DateTime(),
        "event");
    Envelope invalidResponse = Envelope.v1(uuid.toString(), EdaFormatterConstants.EDA_JSON_GENERIC,
        "invalid".getBytes(StandardCharsets.UTF_8));
    when(this.serializer.serialize(message)).thenReturn(invalidResponse);

    assertThrows(Exception.class, () -> {
      this.decorator.serialize(message);
    });
  }

  @Test
  void testItReturnsTheOriginalEnvelopeIfItMatchesTheSchemaForTheBody() throws Exception {
    String json = "{ \"somefield\": \"somevalue\" }";
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
    // This message is irrelevant, we are only testing this decorator
    // The values it uses it will get from the mocks, we are just checking that they are unmodified
    UUID uuid = UUID.randomUUID();
    IMessage message = new Message("name", uuid.toString(), new HashMap<String, String>(), 1, new DateTime(),
        "event");

    Envelope original = Envelope.v1(uuid.toString(), EdaFormatterConstants.EDA_JSON_GENERIC,
        json.getBytes(StandardCharsets.UTF_8));
    when(this.serializer.serialize(message)).thenReturn(original);

    assertEquals(this.decorator.serialize(message), original);
  }

}