package binprocessor.domain.event;

import events.IMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.joda.time.DateTime;

public class OrganizeVisaSourceCommand extends Command implements IMessage {
  public static final String NAME = "organize_visa_source";
  private UUID processId;
  private Integer version = 1;
  private DateTime occurredAt;

  public OrganizeVisaSourceCommand(UUID processId) {
    this.processId = processId;
    this.occurredAt = new DateTime();
  }

  public static OrganizeVisaSourceCommand fromMessage(IMessage message) {
    if (!message.getName().matches(OrganizeVisaSourceCommand.NAME)) {
      throw new RuntimeException();
    }
    Map<String, String> payload = message.getPayload();
    return new OrganizeVisaSourceCommand(
        UUID.fromString(payload.get("processId"))
    );
  }

  public Map<String, String> getPayload() {
    Map<String, String> payload = new HashMap<String, String>();
    payload.put("processId", this.processId.toString());
    return payload;
  }

  public UUID getProcessId() {
    return processId;
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
