package application.handler;

import application.domain.event.CalculateChargesCommand;
import application.domain.event.TransactionClearedEvent;
import events.IMessage;
import events.dispatcher.IHandle;
import events.publisher.IPublish;

public class TransactionChargeHandler implements IHandle {

  private IPublish publisher;

  public TransactionChargeHandler(IPublish publisher) {
    this.publisher = publisher;
  }

  public void handle(IMessage message) throws Exception {
    TransactionClearedEvent event = TransactionClearedEvent.fromMessage(message);
    System.out.println(
        "[x] Handled: "
            + event.getName()
            + " transaction id:"
            + event.getTransactionId().toString());

    publisher.publish(new CalculateChargesCommand(event.getTransactionId()));
  }
}
