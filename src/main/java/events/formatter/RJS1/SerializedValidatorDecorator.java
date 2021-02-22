package events.formatter.RJS1;

import events.IMessage;
import events.formatter.ISerializeMessage;
import events.formatter.IProvideSchema;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

public class SerializedValidatorDecorator implements ISerializeMessage {

    private ISerializeMessage messageSerializer;
    private IProvideSchema schemaProvider;

    public SerializedValidatorDecorator(ISerializeMessage messageSerializer, IProvideSchema schemaProvider) {
        this.messageSerializer = messageSerializer;
        this.schemaProvider = schemaProvider;
    }

    public String serialize(IMessage message) {
        String output = this.messageSerializer.serialize(message);

        JSONObject rawSchema = new JSONObject(new JSONTokener(this.schemaProvider.get()));
        Schema schema = SchemaLoader.load(rawSchema);
        schema.validate(new JSONObject(output)); // throws a ValidationException if this object is invalid

        return output;
    }
}
