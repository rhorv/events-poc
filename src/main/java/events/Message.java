package events;

import java.util.HashMap;
import java.util.Map;
import org.joda.time.DateTime;

public class Message implements IMessage {

  private static enum Category {
    event,
    command
  }

  private String name;
  private String eventId;
  private Map<String, String> payload = new HashMap<String, String>();
  private Integer version;
  private DateTime occurredAt;
  private Category category;

  public Message(
      String name,
      String eventId,
      Map<String, String> payload,
      Integer version,
      DateTime occurredAt,
      String category) {
    this.name = name;
    this.eventId = eventId;
    this.payload = payload;
    this.version = version;
    this.occurredAt = occurredAt;
    this.category = Category.valueOf(category);
  }

  public String getName() {
    return name;
  }

  public String getEventId() {
    return eventId;
  }

  public Map<String, String> getPayload() {
    return payload;
  }

  public Integer getVersion() {
    return version;
  }

  public DateTime getOccurredAt() {
    return occurredAt;
  }

  public String getCategory() {
    return category.toString();
  }
}
