package binprocessor.domain;

public interface ITrackBinDefinitionProcesses {
  public BinDefinitionProcessTracker getActiveTracker(String scheme);

  public void save(BinDefinitionProcessTracker process);
}
