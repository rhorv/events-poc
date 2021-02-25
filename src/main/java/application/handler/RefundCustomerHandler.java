package application.handler;

import application.domain.event.TransactionCancelledEvent;
import events.IMessage;
import events.dispatcher.IHandle;
import events.publisher.IPublish;

public class RefundCustomerHandler implements IHandle {

  private IPublish publisher;

  public RefundCustomerHandler(IPublish publisher) {
    this.publisher = publisher;
  }

  public void handle(IMessage message) throws Exception {
    TransactionCancelledEvent event = TransactionCancelledEvent.fromMessage(message);
    System.out.println(
        "[x] Handled: "
            + event.getName()
            + " transaction id:"
            + event.getTransactionId().toString()
            + " Incoming message version: "
            + message.getVersion()
            + " Own message version: "
            + event.getVersion());
  }
}
