package application.handler;

import application.domain.event.CalculateChargesCommand;
import application.domain.event.TransactionClearedEvent;
import events.IHandle;
import events.IMessage;
import events.IPublish;

public class DoSomething implements IHandle {

    private IPublish publisher;

    public DoSomething(IPublish publisher) {
        this.publisher = publisher;
    }

    public void handle(IMessage message) throws Exception {
        TransactionClearedEvent event = TransactionClearedEvent.fromMessage(message);
        System.out.println("[x] Handled: " + event.getName() + " transaction id:" + event.getTransactionId().toString());

        publisher.publish(new CalculateChargesCommand(event.getTransactionId()));
    }
}