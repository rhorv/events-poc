package binprocessor.domain;

import java.util.UUID;

public class BinDefinitionProcess {

  public enum BinProcessState {
    STARTED,
    WAITING,
    FINISHED
  }

  private UUID id;
  private BinProcessState status;

  public BinDefinitionProcess(UUID id, BinProcessState startingState) {
    this.id = id;
    this.status = startingState;
  }

  public UUID getId() {
    return id;
  }

  public boolean is(BinProcessState state) {
    return this.status.equals(state);
  }
}
