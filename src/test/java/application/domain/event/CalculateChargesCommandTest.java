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

class CalculateChargesCommandTest {

  private Map<String, String> validPayload;
  private Map<String, String> invalidPayload;

  @BeforeEach
  public void setUp() throws Exception {
    this.validPayload = new HashMap<String, String>();
    this.validPayload.put("transactionId", UUID.randomUUID().toString());

    this.invalidPayload = new HashMap<String, String>();
  }


  @Test
  void testItFailsToCreateOnInCompatibleMessageName() {
    IMessage message = new Message("event_name", this.validPayload, 1,
        new DateTime("2020-09-15T15:53:00+01:00"), "event");
    assertThrows(Exception.class, () -> {
      CalculateChargesCommand.fromMessage(message);
    });
  }

  @Test
  void testItFailsToCreateOnInCompatiblePayload() {
    IMessage message = new Message(CalculateChargesCommand.NAME, this.invalidPayload, 1,
        new DateTime("2020-09-15T15:53:00+01:00"), "event");
    assertThrows(Exception.class, () -> {
      TransactionClearedEvent.fromMessage(message);
    });
  }

  @Test
  void testItSucceedsInCreatingTheEventOnCompatibleMessage() throws Exception {
    IMessage message = new Message(CalculateChargesCommand.NAME, this.validPayload, 1,
        new DateTime("2020-09-15T15:53:00+01:00"), "event");
    CalculateChargesCommand event = CalculateChargesCommand.fromMessage(message);
    assertEquals(event.getName(), CalculateChargesCommand.NAME);
    assertEquals(event.getTransactionId().toString(), this.validPayload.get("transactionId"));
  }
}