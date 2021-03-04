package binprocessor.domain.event;

import events.IMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.joda.time.DateTime;

public class StartVisaBinProcessingCommand extends Command implements IMessage {
  public static final String NAME = "start_visa_bin_processing";
  private UUID processId;
  private String ardefPath;
  private String regardefPath;
  private String nrpPath;
  private Integer version = 1;
  private DateTime occurredAt;

  public StartVisaBinProcessingCommand(
      UUID processId, String ardefPath, String regardDefPath, String nrpPath) {
    this.ardefPath = ardefPath;
    this.regardefPath = regardDefPath;
    this.nrpPath = nrpPath;
    this.processId = processId;
    this.occurredAt = new DateTime();
  }

  public static StartVisaBinProcessingCommand fromMessage(IMessage message) {
    if (!message.getName().matches(StartVisaBinProcessingCommand.NAME)) {
      throw new RuntimeException();
    }
    Map<String, String> payload = message.getPayload();
    return new StartVisaBinProcessingCommand(
        UUID.fromString(payload.get("processId")),
        payload.get("ardefPath"),
        payload.get("regardefPath"),
        payload.get("nrpPath")
    );
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
