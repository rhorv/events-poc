package binprocessor.service.infrastructure.binprocesstracker;

import binprocessor.domain.BinDefinitionProcessTracker;
import binprocessor.domain.ITrackBinDefinitionProcesses;
import java.util.HashMap;
import java.util.Map;

public class InMemoryBinProcessTrackerRepository implements ITrackBinDefinitionProcesses {

  private Map<String, BinDefinitionProcessTracker> trackers =
      new HashMap<String, BinDefinitionProcessTracker>();

  public BinDefinitionProcessTracker getActiveTracker(String scheme) {
    if (!this.trackers.containsKey(scheme)) {
      this.trackers.put(scheme, new BinDefinitionProcessTracker(scheme));
    }
    return this.trackers.get(scheme);
  }

  public void save(BinDefinitionProcessTracker process) {
    this.trackers.put(process.getScheme(), process);
  }
}
