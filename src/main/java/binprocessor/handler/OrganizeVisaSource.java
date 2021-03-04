package binprocessor.handler;

import binprocessor.domain.event.OrganizeVisaSourceCommand;
import binprocessor.domain.event.VisaSourceOrganizedEvent;
import events.IMessage;
import events.dispatcher.IHandle;
import events.publisher.IPublish;

public class OrganizeVisaSource implements IHandle {
  private IPublish publisher;

  public OrganizeVisaSource(IPublish publisher) {
    this.publisher = publisher;
  }

  public void handle(IMessage message) throws Exception {
    OrganizeVisaSourceCommand command = OrganizeVisaSourceCommand.fromMessage(message);

    // move file around

    this.publisher.publish(
        new VisaSourceOrganizedEvent(
            command.getProcessId(), "ardefFilePath", "regardefFilePath", "nrpFilePath"));
  }
}
