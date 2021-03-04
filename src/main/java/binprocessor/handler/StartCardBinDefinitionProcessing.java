package binprocessor.handler;

import binprocessor.domain.event.StartVisaBinProcessingCommand;
import binprocessor.domain.event.VisaSourceOrganizedEvent;
import events.IMessage;
import events.dispatcher.IHandle;
import events.publisher.IPublish;

public class StartCardBinDefinitionProcessing implements IHandle {

  private IPublish publisher;

  public StartCardBinDefinitionProcessing(IPublish publisher) {
    this.publisher = publisher;
  }

  public void handle(IMessage message) throws Exception {
    VisaSourceOrganizedEvent event = VisaSourceOrganizedEvent.fromMessage(message);
    this.publisher.publish(
        new StartVisaBinProcessingCommand(
            event.getProcessId(),
            event.getArdefPath(),
            event.getRegardefPath(),
            event.getNrpPath()));
  }
}
