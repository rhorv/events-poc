package events.formatter.RJS1;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import events.IMessage;
import events.ISerializeMessage;
import events.formatter.IProvideSchema;

import java.util.UUID;

public class RJS1Serializer implements ISerializeMessage {

    public String serialize(IMessage message) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        GsonClassDto dto = new GsonClassDto();
        dto.id = UUID.randomUUID().toString();
        dto.name = message.getName();
        dto.payload = message.getPayload();
        dto.version = message.getVersion();
        dto.occurredAt = message.getOccurredAt().toString();
        dto.category = message.getCategory();
        String jsonString = gson.toJson(dto);
        return jsonString;
    }
}
