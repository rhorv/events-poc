package events.formatter.rjs1;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import events.IMessage;
import events.formatter.ISerializeMessage;
import events.formatter.formatterConstants;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class Rjs1Serializer implements ISerializeMessage {

  @Override
  public String nameSerde() {
    return formatterConstants.RSJ1;
  }

  public ByteArrayOutputStream serialize(IMessage message) throws Exception {
    GsonClassDto dto = new GsonClassDto();
    dto.id = UUID.randomUUID().toString();
    dto.name = message.getName();
    dto.payload = message.getPayload();
    dto.version = message.getVersion();
    dto.occurredAt = message.getOccurredAt().toString();
    dto.category = message.getCategory();

    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create();
    String jsonString = gson.toJson(dto);
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    stream.write(jsonString.getBytes());
    return stream;
  }
}
