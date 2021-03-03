package events.formatter.rjs1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import events.IMessage;
import events.Message;
import events.formatter.IProvideSchema;
import events.formatter.ISerializeMessage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import org.apache.commons.compress.utils.ByteUtils.OutputStreamByteConsumer;
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
  void testItThrowsOnInvalidJsonBySchema() throws Exception {
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

    IMessage message = new Message("name", new HashMap<String, String>(), 1, new DateTime(),
        "event");
    ByteArrayOutputStream emptyResponse = new ByteArrayOutputStream();
    emptyResponse.write("{}".getBytes(StandardCharsets.UTF_8));
    when(this.serializer.serialize(message)).thenReturn(emptyResponse);

    assertThrows(Exception.class, () -> {
      this.decorator.serialize(message);
    });
  }

  @Test
  void testItThrowsOnInvalidJson() throws Exception {
    when(this.provider.get()).thenReturn("{}");

    IMessage message = new Message("name", new HashMap<String, String>(), 1, new DateTime(),
        "event");
    ByteArrayOutputStream invalidResponse = new ByteArrayOutputStream();
    invalidResponse.write("invalid".getBytes(StandardCharsets.UTF_8));
    when(this.serializer.serialize(message)).thenReturn(invalidResponse);

    assertThrows(Exception.class, () -> {
      this.decorator.serialize(message);
    });
  }

  @Test
  void testItReturnsTheOriginalJsonIfItMatchesTheSchema() throws Exception {
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
    IMessage message = new Message("name", new HashMap<String, String>(), 1, new DateTime(),
        "event");
    ByteArrayOutputStream original = new ByteArrayOutputStream();
    original.write(json.getBytes(StandardCharsets.UTF_8));
    when(this.serializer.serialize(message)).thenReturn(original);

    assertEquals(this.decorator.serialize(message), original);
  }

}