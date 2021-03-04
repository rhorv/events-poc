package binprocessor.handler;

import binprocessor.domain.event.BinDefinitionProcessFinishedEvent;
import binprocessor.domain.event.StartVisaBinProcessingCommand;
import events.IMessage;
import events.dispatcher.IHandle;
import events.publisher.IPublish;

public class CardBinDefinitionRegister implements IHandle {
  private IPublish publisher;

  public CardBinDefinitionRegister(IPublish publisher) {
    this.publisher = publisher;
  }

  public void handle(IMessage message) throws Exception {
    StartVisaBinProcessingCommand command = StartVisaBinProcessingCommand.fromMessage(message);

    // do magic

    // publish deltas
    this.publisher.publish(new BinDefinitionProcessFinishedEvent(command.getProcessId(), "visa"));
  }
}
