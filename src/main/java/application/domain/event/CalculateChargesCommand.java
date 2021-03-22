package application.domain.event;

import events.IMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.joda.time.DateTime;

public class CalculateChargesCommand extends Command implements IMessage {

  public static final String NAME = "calculate_charges";
  private String transactionId;
  private DateTime occurredAt;

  public CalculateChargesCommand(String transactionId) {
    // this.transactionId = UUID.randomUUID().toString();
    // this.consequenceOf = transactionId;
    this.transactionId = transactionId;
    this.occurredAt = new DateTime();
  }

  public Map<String, String> getPayload() {
    Map<String, String> payload = new HashMap<String, String>();
    payload.put("transactionId", this.transactionId.toString());
    return payload;
  }

  public static CalculateChargesCommand fromMessage(IMessage message) {
    if (!message.getName().matches(CalculateChargesCommand.NAME)) {
      throw new RuntimeException();
    }
    Map<String, String> payload = message.getPayload();
    return new CalculateChargesCommand(payload.get("transactionId"));
  }

  public String getName() {
    return NAME;
  }

  public String getEventId() {
    return transactionId;
  }

  public DateTime getOccurredAt() {
    return this.occurredAt;
  }

  public Integer getVersion() {
    return 1;
  }
}
