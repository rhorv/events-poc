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

class TransactionCancelledEventTest {

  private Map<String, String> validPayload;
  private Map<String, String> validv3Payload;
  private Map<String, String> invalidPayload;

  @BeforeEach
  public void setUp() throws Exception {
    this.validPayload = new HashMap<String, String>();
    this.validPayload.put("transactionId", UUID.randomUUID().toString());

    this.validv3Payload = new HashMap<String, String>();
    this.validv3Payload.put("transactionId", UUID.randomUUID().toString());
    this.validv3Payload.put("reason", "my reason");
    this.validv3Payload.put("anythingelse", "anything");

    this.invalidPayload = new HashMap<String, String>();
  }


  @Test
  void testItFailsToCreateOnInCompatibleMessageName() {
    IMessage message = new Message("event_name", this.validPayload, 1,
        new DateTime("2020-09-15T15:53:00+01:00"), "event");
    assertThrows(Exception.class, () -> {
      TransactionCancelledEvent.fromMessage(message);
    });
  }

  @Test
  void testItFailsToCreateOnInCompatiblePayload() {
    IMessage message = new Message(TransactionCancelledEvent.NAME, this.invalidPayload, 1,
        new DateTime("2020-09-15T15:53:00+01:00"), "event");
    assertThrows(Exception.class, () -> {
      TransactionCancelledEvent.fromMessage(message);
    });
  }

  @Test
  void testItSucceedsInCreatingAVersion2EventOnCompatibleV1Message() throws Exception {
    IMessage message = new Message(TransactionCancelledEvent.NAME, this.validPayload, 1,
        new DateTime("2020-09-15T15:53:00+01:00"), "event");
    TransactionCancelledEvent event = TransactionCancelledEvent.fromMessage(message);
    assertEquals(event.getName(), TransactionCancelledEvent.NAME);
    assertEquals(event.getTransactionId().toString(), this.validPayload.get("transactionId"));
    assertEquals(event.getReason(), "unspecified");
  }

  @Test
  void testItSucceedsInCreatingAVersion2EventOnCompatibleV2OrHigherMessage() throws Exception {
    IMessage message = new Message(TransactionCancelledEvent.NAME, this.validv3Payload, 3,
        new DateTime("2020-09-15T15:53:00+01:00"), "event");
    TransactionCancelledEvent event = TransactionCancelledEvent.fromMessage(message);
    assertEquals(event.getName(), TransactionCancelledEvent.NAME);
    assertEquals(event.getTransactionId().toString(), this.validv3Payload.get("transactionId"));
    assertEquals(event.getReason(), this.validv3Payload.get("reason"));
  }

}