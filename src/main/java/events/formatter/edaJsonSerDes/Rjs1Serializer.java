package events.formatter.edaJsonSerDes;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import events.IMessage;
import events.formatter.Envelope;
import events.formatter.IProvideSchema;
import events.formatter.ISerializeMessage;
import events.formatter.EDAFormatterConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Rjs1Serializer implements ISerializeMessage {
  private IProvideSchema schemaProvider;

  public Rjs1Serializer(IProvideSchema schemaProvider) {
    this.schemaProvider = schemaProvider;
  }

  public Envelope serialize(IMessage message) throws Exception {
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
    Map<String, String> header = new HashMap<>();
    return Envelope.v1(dto.id, EDAFormatterConstants.RSJ1, jsonString.getBytes());
  }
}
