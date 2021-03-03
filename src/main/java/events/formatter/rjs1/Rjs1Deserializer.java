package events.formatter.rjs1;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import events.IMessage;
import events.Message;
import events.formatter.IDeserializeMessage;
import events.formatter.IProvideSchema;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Rjs1Deserializer implements IDeserializeMessage {

  private IProvideSchema schemaProvider;

  public Rjs1Deserializer(IProvideSchema schemaProvider) {
    this.schemaProvider = schemaProvider;
  }

  public IMessage deserialize(ByteArrayInputStream body) throws Exception {

    String messageBody = byteArrayToString(body);
    JSONObject rawSchema = new JSONObject(new JSONTokener(this.schemaProvider.get()));
    Schema schema = SchemaLoader.load(rawSchema);
    schema.validate(
        new JSONObject(
            messageBody)); // throws a ValidationException if this object is invalid

    GsonBuilder builder = new GsonBuilder();
    Gson gson =
        builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    GsonClassDto dto = gson.fromJson(messageBody, GsonClassDto.class);

    return new Message(
        dto.name, dto.payload, dto.version, new DateTime(dto.occurredAt), dto.category);
  }

  private String byteArrayToString(ByteArrayInputStream in) {
    int n = in.available();
    byte[] bytes = new byte[n];
    in.read(bytes, 0, n);
    return new String(bytes, StandardCharsets.UTF_8);
  }
}
