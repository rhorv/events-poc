package binprocessor.domain.event;

import events.IMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.joda.time.DateTime;

public class VisaSourceOrganizedEvent extends Event implements IMessage {

  public static final String NAME = "visa_source_organized";
  private UUID processId;
  private String ardefPath;
  private String regardefPath;
  private String nrpPath;
  private Integer version = 1;
  private DateTime occurredAt;

  public VisaSourceOrganizedEvent(
      UUID processId, String ardefPath, String regardDefPath, String nrpPath) {
    this.processId = processId;
    this.ardefPath = ardefPath;
    this.regardefPath = regardDefPath;
    this.nrpPath = nrpPath;
    this.occurredAt = new DateTime();
  }

  public static VisaSourceOrganizedEvent fromMessage(IMessage message) {
    if (!message.getName().matches(VisaSourceOrganizedEvent.NAME)) {
      throw new RuntimeException();
    }
    Map<String, String> payload = message.getPayload();
    return new VisaSourceOrganizedEvent(
        UUID.fromString(payload.get("processId")),
        payload.get("ardefPath"),
        payload.get("regardefPath"),
        payload.get("nrpPath"));
  }

  public UUID getProcessId() {
    return processId;
  }

  public String getArdefPath() {
    return ardefPath;
  }

  public String getRegardefPath() {
    return regardefPath;
  }

  public String getNrpPath() {
    return nrpPath;
  }

  public Map<String, String> getPayload() {
    Map<String, String> payload = new HashMap<String, String>();
    payload.put("processId", this.processId.toString());
    payload.put("ardefPath", this.ardefPath);
    payload.put("regardefPath", this.regardefPath);
    payload.put("nrpPath", this.nrpPath);
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
