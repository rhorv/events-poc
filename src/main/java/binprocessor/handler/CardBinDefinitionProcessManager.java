package binprocessor.handler;

import binprocessor.domain.BinDefinitionProcessTracker;
import binprocessor.domain.IClassifyFilesToScheme;
import binprocessor.domain.ITrackBinDefinitionProcesses;
import binprocessor.domain.event.BinDefinitionProcessFinishedEvent;
import binprocessor.domain.event.FileArrivedEvent;
import binprocessor.domain.event.OrganizeVisaSourceCommand;
import events.IMessage;
import events.dispatcher.IHandle;
import events.publisher.IPublish;
import java.util.UUID;

public class CardBinDefinitionProcessManager implements IHandle {

  private IPublish publisher;
  private ITrackBinDefinitionProcesses tracker;
  private IClassifyFilesToScheme schemeClassifier;

  public CardBinDefinitionProcessManager(
      IPublish publisher,
      ITrackBinDefinitionProcesses tracker,
      IClassifyFilesToScheme schemeClassifier) {
    this.publisher = publisher;
    this.tracker = tracker;
    this.schemeClassifier = schemeClassifier;
  }

  public void handle(IMessage message) throws Exception {

    if (message.getName().equals(FileArrivedEvent.NAME)) {
      this.onFileArrived(message);
    } else {
      this.onProcessFinished(message);
    }
  }

  private void onFileArrived(IMessage message) throws Exception {
    FileArrivedEvent event = FileArrivedEvent.fromMessage(message);
    BinDefinitionProcessTracker activeTracker =
        this.tracker.getActiveTracker(this.schemeClassifier.getSchemeNameFor(event.getPath()));

    if (!activeTracker.hasActiveProcess()) {
      UUID id = activeTracker.registerStartedProcess();
      this.tracker.save(activeTracker);
      this.publisher.publish(new OrganizeVisaSourceCommand(id));
    } else {
      if (activeTracker.hasStartedProcess() && !activeTracker.hasWaitingProcess()) {
        UUID id = activeTracker.registerWaitingProcess();
        this.tracker.save(activeTracker);
      }
    }
  }

  private void onProcessFinished(IMessage message) throws Exception {
    BinDefinitionProcessFinishedEvent event =
        BinDefinitionProcessFinishedEvent.fromMessage(message);
    BinDefinitionProcessTracker activeTracker = this.tracker.getActiveTracker(event.getScheme());

    if (activeTracker.hasWaitingProcess()) {
      activeTracker.finish(event.getProcessId());
      UUID id = activeTracker.startWaitingProcess();
      this.tracker.save(activeTracker);
      this.publisher.publish(new OrganizeVisaSourceCommand(id));
    } else {
      activeTracker.finish(event.getProcessId());
      this.tracker.save(activeTracker);
    }
  }
}
