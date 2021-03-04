package binprocessor.domain.event;

import events.IMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.joda.time.DateTime;

public class BinDefinitionProcessFinishedEvent extends Event implements IMessage {

  private enum SchemeType {
    visa,
    mastercard
  }

  public static final String NAME = "bin_definition_process_finished";
  private UUID processId;
  private SchemeType scheme;
  private Integer version = 1;
  private DateTime occurredAt;

  public BinDefinitionProcessFinishedEvent(UUID processId, String scheme) {
    this.processId = processId;
    this.scheme = SchemeType.valueOf(scheme);
    this.occurredAt = new DateTime();
  }

  public static BinDefinitionProcessFinishedEvent fromMessage(IMessage message) {
    if (!message.getName().matches(BinDefinitionProcessFinishedEvent.NAME)) {
      throw new RuntimeException();
    }
    Map<String, String> payload = message.getPayload();
    return new BinDefinitionProcessFinishedEvent(
        UUID.fromString(payload.get("processId")),
        payload.get("scheme")
    );
  }

  public UUID getProcessId() {
    return processId;
  }

  public String getScheme() {
    return scheme.toString();
  }

  public Map<String, String> getPayload() {
    Map<String, String> payload = new HashMap<String, String>();
    payload.put("processId", this.processId.toString());
    payload.put("scheme", this.scheme.toString());
    return payload;
  }

  public String getName() {
    return NAME;
  }

  public DateTime getOccurredAt() {
    return this.occurredAt;
  }

  public Integer getVersion() {
    return this.version;
  }
}
