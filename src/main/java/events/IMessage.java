package events;

import org.joda.time.DateTime;

import java.util.Map;

public interface IMessage {
    public String getName();
    public Map<String, String> getPayload();
    public Integer getVersion();
    public DateTime getOccurredAt();
    public String getCategory();
}
