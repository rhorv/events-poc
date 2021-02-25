package application.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import application.domain.Currency;
import application.domain.Money;
import application.domain.event.CalculateChargesCommand;
import application.domain.event.TransactionClearedEvent;
import events.IMessage;
import events.Message;
import events.publisher.IPublish;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class DoSomethingHandlerTest {

  private class StubPublisher implements IPublish {

    public ArrayList<IMessage> messages = new ArrayList<>();

    public void publish(IMessage message) throws Exception {
      this.messages.add(message);
    }
  }

  private TransactionChargeHandler handler;
  private StubPublisher publisher;

  @BeforeEach
  public void setUp() throws Exception {
    this.publisher = new StubPublisher();
    this.handler = new TransactionChargeHandler(this.publisher);
  }

  @Test
  void testItThrowsOnNotCompatibleMessage() {
    IMessage message = new Message("event_name", new HashMap<String, String>(), 1,
        new DateTime("2020-09-15T15:53:00+01:00"), "event");
    assertThrows(Exception.class, () -> {
      this.handler.handle(message);
    });
  }

  @Test
  void testItPublishesCalculateChargesCommandOnSuccess() throws Exception {
    IMessage message = new TransactionClearedEvent(UUID.randomUUID(),
        new Money(123, Currency.valueOf("GBP")));
    this.handler.handle(message);
    assertEquals(this.publisher.messages.size(), 1);
    assertEquals(this.publisher.messages.get(0).getName(), CalculateChargesCommand.NAME);
  }
}