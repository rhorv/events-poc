package events.formatter.RJS1;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import events.formatter.IDeserializeMessage;
import events.IMessage;
import events.Message;
import events.formatter.IProvideSchema;
import org.joda.time.DateTime;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

public class RJS1Deserializer implements IDeserializeMessage {

    private IProvideSchema schemaProvider;

    public RJS1Deserializer(IProvideSchema schemaProvider) {
        this.schemaProvider = schemaProvider;
    }

    public IMessage deserialize(String body) throws Exception {

        JSONObject rawSchema = new JSONObject(new JSONTokener(this.schemaProvider.get()));
        Schema schema = SchemaLoader.load(rawSchema);
        schema.validate(new JSONObject(body)); // throws a ValidationException if this object is invalid

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        GsonClassDto dto = gson.fromJson(body, GsonClassDto.class);

        return new Message(dto.name, dto.payload, dto.version, new DateTime(dto.occurredAt), dto.category);
    }
}
