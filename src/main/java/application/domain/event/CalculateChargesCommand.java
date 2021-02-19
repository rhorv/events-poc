package application.domain.event;

import application.domain.Currency;
import application.domain.Money;
import events.IMessage;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CalculateChargesCommand extends Command implements IMessage {
    public static final String NAME = "calculate_charges";
    private UUID transactionId;
    private DateTime occurredAt;

    public CalculateChargesCommand(UUID transactionId) {
        this.transactionId = transactionId;
        this.occurredAt = new DateTime();
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public Map<String, String> getPayload() {
        Map<String, String> payload = new HashMap<String , String >();
        payload.put("transactionId", this.transactionId.toString());
        return payload;
    }

    public static CalculateChargesCommand fromMessage(IMessage message) {
        if (!message.getName().matches(CalculateChargesCommand.NAME)) {
            throw new RuntimeException();
        }
        Map<String, String> payload = message.getPayload();
        return new CalculateChargesCommand(
                UUID.fromString(payload.get("transactionId"))
        );
    }

    public String getName() {
        return NAME;
    }

    public DateTime getOccurredAt() {
        return this.occurredAt;
    }

    public Integer getVersion() {
        return 1;
    }
}
