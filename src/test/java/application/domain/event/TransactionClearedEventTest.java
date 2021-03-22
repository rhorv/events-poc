package application.domain.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import events.IMessage;
import events.Message;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionClearedEventTest {

  private Map<String, String> validPayload;
  private Map<String, String> invalidPayload;

  @BeforeEach
  public void setUp() throws Exception {
    this.validPayload = new HashMap<String, String>();
    this.validPayload.put("transactionId", UUID.randomUUID().toString());
    this.validPayload.put("interchangeFeeAmount", "123");
    this.validPayload.put("interchangeFeeCurrency", "GBP");

    this.invalidPayload = new HashMap<String, String>();
  }


  @Test
  void testItFailsToCreateOnInCompatibleMessageName() {
    IMessage message = new Message("event_name", this.validPayload.get("transactionId"), this.validPayload, 1,
        new DateTime("2020-09-15T15:53:00+01:00"), "event");
    assertThrows(Exception.class, () -> {
      TransactionClearedEvent.fromMessage(message);
    });
  }

  @Test
  void testItFailsToCreateOnInCompatiblePayload() {
    IMessage message = new Message(TransactionClearedEvent.NAME, UUID.randomUUID().toString(), this.invalidPayload, 1,
        new DateTime("2020-09-15T15:53:00+01:00"), "event");
    assertThrows(Exception.class, () -> {
      TransactionClearedEvent.fromMessage(message);
    });
  }

  @Test
  void testItSucceedsInCreatingTheEventOnCompatibleMessage() throws Exception {
    IMessage message = new Message(TransactionClearedEvent.NAME, this.validPayload.get("transactionId"), this.validPayload, 1,
        new DateTime("2020-09-15T15:53:00+01:00"), "event");
    TransactionClearedEvent event = TransactionClearedEvent.fromMessage(message);
    assertEquals(event.getName(), TransactionClearedEvent.NAME);
    assertEquals(event.getEventId().toString(), this.validPayload.get("transactionId"));
    assertEquals(event.getInterchangeFee().getAmount(),
        Integer.valueOf(this.validPayload.get("interchangeFeeAmount")));
    assertEquals(event.getInterchangeFee().getCurrency().toString(),
        this.validPayload.get("interchangeFeeCurrency"));
  }
}