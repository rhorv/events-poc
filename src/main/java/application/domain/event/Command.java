package application.domain.event;

public abstract class Command {

  public String getCategory() {
    return "command";
  }
}
