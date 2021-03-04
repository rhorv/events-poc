package binprocessor.domain;

import binprocessor.domain.BinDefinitionProcess.BinProcessState;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BinDefinitionProcessTracker {

  private List<BinDefinitionProcess> processes = new ArrayList<BinDefinitionProcess>();
  private String scheme;

  public BinDefinitionProcessTracker(String scheme) {
    this.scheme = scheme;
  }

  public String getScheme() {
    return scheme;
  }

  public UUID registerWaitingProcess() throws Exception {
    if (this.hasWaitingProcess()) {
      throw new Exception();
    }
    BinDefinitionProcess newProcess =
        new BinDefinitionProcess(UUID.randomUUID(), BinProcessState.WAITING);
    this.add(newProcess);
    return newProcess.getId();
  }

  public UUID registerStartedProcess() throws Exception {
    if (this.hasActiveProcess()) {
      throw new Exception();
    }
    BinDefinitionProcess newProcess =
        new BinDefinitionProcess(UUID.randomUUID(), BinProcessState.STARTED);
    this.processes.add(newProcess);
    return newProcess.getId();
  }

  private void add(BinDefinitionProcess process) throws Exception {
    if (this.exists(process.getId())) {
      throw new Exception();
    }
    this.processes.add(process);
  }

  private void remove(BinDefinitionProcess otherProcess) throws Exception {
    for (BinDefinitionProcess process : this.processes) {
      if (process.equals(otherProcess)) {
        this.processes.remove(process);
        return;
      }
    }
    throw new Exception();
  }

  private boolean exists(UUID id) {
    for (BinDefinitionProcess process : this.processes) {
      if (process.getId().equals(id)) {
        return true;
      }
    }
    return false;
  }

  public boolean hasWaitingProcess() {
    return this.count(BinProcessState.WAITING) > 0;
  }

  public boolean hasActiveProcess() {
    return this.hasStartedProcess() || this.hasWaitingProcess();
  }

  public boolean hasStartedProcess() {
    return this.count(BinProcessState.STARTED) > 0;
  }

  public void finish(UUID id) throws Exception {
    BinDefinitionProcess process = this.getById(id);
    this.remove(process);
    this.add(new BinDefinitionProcess(id, BinProcessState.FINISHED));
  }

  private void start(UUID id) throws Exception {
    if (this.hasStartedProcess()) {
      throw new Exception();
    }
    BinDefinitionProcess process = this.getById(id);
    if (!process.is(BinProcessState.WAITING)) {
      throw new Exception();
    }
    this.remove(process);
    this.add(new BinDefinitionProcess(id, BinProcessState.STARTED));
  }

  private BinDefinitionProcess getById(UUID id) throws Exception {
    for (BinDefinitionProcess process : this.processes) {
      if (process.getId().equals(id)) {
        return process;
      }
    }
    throw new Exception();
  }

  private BinDefinitionProcess getByState(BinProcessState state) throws Exception {
    for (BinDefinitionProcess process : this.processes) {
      if (process.is(state)) {
        return process;
      }
    }
    throw new Exception();
  }

  public Integer countRunning() {
    return this.count(BinProcessState.STARTED);
  }

  public Integer countWaiting() {
    return this.count(BinProcessState.WAITING);
  }

  private Integer count(BinProcessState state) {
    Integer counter = 0;
    for (BinDefinitionProcess process : this.processes) {
      if (process.is(state)) {
        counter++;
      }
    }
    return counter;
  }

  public boolean isWaiting(UUID id) throws Exception {
    return this.getById(id).is(BinProcessState.WAITING);
  }

  public boolean isRunning(UUID id) throws Exception {
    return this.getById(id).is(BinProcessState.STARTED);
  }

  public UUID startWaitingProcess() throws Exception {
    BinDefinitionProcess process = this.getByState(BinProcessState.WAITING);
    this.start(process.getId());
    return process.getId();
  }
}
