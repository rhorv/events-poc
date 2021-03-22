package events.formatter.edaJsonSerDes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import events.IMessage;
import events.Message;
import events.formatter.Envelope;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EdaJsonSerializerTest {

  private EdaJsonSerializer serializer;

  @BeforeEach
  public void setUp() throws Exception {
    this.serializer = new EdaJsonSerializer(new EdaJsonHardCodedSchemaProvider());
  }

  @Test
  void testItSerializesCorrectlyIntoValidJson() throws Exception {

    HashMap<String, String> payload = new HashMap<String, String>();
    payload.put("field", "value");
    IMessage message = new Message("event_name", UUID.randomUUID().toString(), payload, 1,
        new DateTime("2020-09-15T15:53:00+01:00"), "event");

    Envelope serializedMessage = this.serializer.serialize(message);

    JsonParser parser = new JsonParser();
    JsonElement tree = parser.parse(new String(serializedMessage.getBody(), StandardCharsets.UTF_8));

    assertEquals(((JsonObject) tree).get("name").getAsString(), "event_name");
    assertEquals(((JsonObject) tree).get("category").getAsString(), "event");
    assertEquals(Integer.valueOf(((JsonObject) tree).get("version").getAsString()), 1);
    assertEquals(((JsonObject) tree).get("payload").getAsJsonObject().get("field").getAsString(),
        "value");
    assertEquals(((JsonObject) tree).get("payload").getAsJsonObject().size(), 1);
    assertEquals(new DateTime(((JsonObject) tree).get("occurred_at").getAsString()),
        new DateTime("2020-09-15T15:53:00+01:00"));
  }

}