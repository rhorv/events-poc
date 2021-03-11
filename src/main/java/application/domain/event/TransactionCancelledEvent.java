package application.domain.event;

import events.IMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.joda.time.DateTime;

public class TransactionCancelledEvent extends Event implements IMessage {

  public static final String NAME = "transaction_cancelled";
  private UUID transactionId;
  private String reason = "";
  private DateTime occurredAt;
  private Integer version = 2;

  public TransactionCancelledEvent(UUID transactionId, String reason) {
    this.transactionId = transactionId;
    this.reason = reason;
    this.occurredAt = new DateTime();
  }

  public String getReason() {
    return reason;
  }

  public static TransactionCancelledEvent fromMessage(IMessage message) {
    if (!message.getName().matches(TransactionCancelledEvent.NAME)) {
      throw new RuntimeException();
    }
    Map<String, String> payload = message.getPayload();
    if (message.getVersion() >= 2) {
      return new TransactionCancelledEvent(
          UUID.fromString(payload.get("transactionId")), payload.get("reason"));
    } else {
      return new TransactionCancelledEvent(
          UUID.fromString(payload.get("transactionId")), "unspecified");
    }
  }

  public Map<String, String> getPayload() {
    Map<String, String> payload = new HashMap<String, String>();
    payload.put("transactionId", this.transactionId.toString());
    payload.put("reason", this.reason);
    return payload;
  }

  public String getName() {
    return NAME;
  }

  public String getEventId() { return transactionId.toString(); }

  public DateTime getOccurredAt() {
    return this.occurredAt;
  }

  public Integer getVersion() {
    return this.version;
  }
}
