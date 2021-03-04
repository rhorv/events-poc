package binprocessor.domain.event;

import events.IMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.joda.time.DateTime;

public class FileArrivedEvent extends Event implements IMessage {

  public static final String NAME = "file_arrived";
  private String path;
  private Integer version = 1;
  private DateTime occurredAt;

  public FileArrivedEvent(String path) {
    this.path = path;
    this.occurredAt = new DateTime();
  }

  public static FileArrivedEvent fromMessage(IMessage message) {
    if (!message.getName().matches(FileArrivedEvent.NAME)) {
      throw new RuntimeException();
    }
    Map<String, String> payload = message.getPayload();
    return new FileArrivedEvent(
        payload.get("path")
    );
  }

  public String getPath() {
    return path;
  }

  public Map<String, String> getPayload() {
    Map<String, String> payload = new HashMap<String, String>();
    payload.put("path", this.path);
    return payload;
  }

  public String getName() {
    return NAME;
  }

  public DateTime getOccurredAt() {
    return this.occurredAt;
  }

  public Integer getVersion() {
    return this.version;
  }
}
