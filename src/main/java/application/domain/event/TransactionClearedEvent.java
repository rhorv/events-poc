package application.domain.event;

import application.domain.Currency;
import application.domain.Money;
import events.IMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.joda.time.DateTime;

public class TransactionClearedEvent extends Event implements IMessage {

  public static final String NAME = "transaction_cleared";
  private UUID transactionId;
  private Money interchangeFee;
  private DateTime occurredAt;
  private Integer version = 1;

  public TransactionClearedEvent(UUID transactionId, Money interchangeFee) {
    this.transactionId = transactionId;
    this.interchangeFee = interchangeFee;
    occurredAt = new DateTime();
  }

  public UUID getTransactionId() {
    return transactionId;
  }

  public Money getInterchangeFee() {
    return interchangeFee;
  }

  public static TransactionClearedEvent fromMessage(IMessage message) {
    if (!message.getName().matches(TransactionClearedEvent.NAME)) {
      throw new RuntimeException();
    }
    Map<String, String> payload = message.getPayload();
    return new TransactionClearedEvent(
        UUID.fromString(payload.get("transactionId")),
        new Money(
            Integer.valueOf(payload.get("interchangeFeeAmount")),
            Currency.valueOf(payload.get("interchangeFeeCurrency"))
        )
    );
  }

  public Map<String, String> getPayload() {
    Map<String, String> payload = new HashMap<String, String>();
    payload.put("transactionId", this.transactionId.toString());
    payload.put("interchangeFeeAmount", this.interchangeFee.getAmount().toString());
    payload.put("interchangeFeeCurrency", this.interchangeFee.getCurrency().toString());
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
