package events.formatter.rjs1;

import events.IMessage;
import events.formatter.IProvideSchema;
import events.formatter.ISerializeMessage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import events.formatter.formatterConstants;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

public class SerializedValidatorDecorator implements ISerializeMessage {

  private ISerializeMessage messageSerializer;
  private IProvideSchema schemaProvider;

  public SerializedValidatorDecorator(
      ISerializeMessage messageSerializer, IProvideSchema schemaProvider) {
    this.messageSerializer = messageSerializer;
    this.schemaProvider = schemaProvider;
  }

  public ByteArrayOutputStream serialize(IMessage message) throws Exception {
    ByteArrayOutputStream output = this.messageSerializer.serialize(message);

    JSONObject rawSchema = new JSONObject(new JSONTokener(this.schemaProvider.get()));
    Schema schema = SchemaLoader.load(rawSchema);
    schema.validate(
        new JSONObject(
            new String(
                output.toByteArray(),
                StandardCharsets.UTF_8))); // throws a ValidationException if this object is invalid

    return output;
  }

  @Override
  public String nameSerde() {
    return formatterConstants.RSJ1;
  }
}
