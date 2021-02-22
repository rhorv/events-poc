package events.formatter.rjs1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import events.IMessage;
import events.Message;
import events.formatter.IProvideSchema;
import events.formatter.ISerializeMessage;
import java.util.HashMap;
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
  void testItThrowsOnInvalidJsonBySchema() {
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
    when(this.serializer.serialize(message)).thenReturn("{}");

    assertThrows(Exception.class, () -> {
      this.decorator.serialize(message);
    });
  }

  @Test
  void testItThrowsOnInvalidJson() {
    when(this.provider.get()).thenReturn("{}");

    IMessage message = new Message("name", new HashMap<String, String>(), 1, new DateTime(),
        "event");
    when(this.serializer.serialize(message)).thenReturn("invalid");

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
    IMessage message = new Message("name", new HashMap<String, String>(), 1, new DateTime(),
        "event");
    when(this.serializer.serialize(message)).thenReturn(json);

    assertEquals(this.decorator.serialize(message), json);
  }

}