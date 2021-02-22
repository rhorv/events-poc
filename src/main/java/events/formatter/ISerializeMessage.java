package events.formatter;

import events.IMessage;

public interface ISerializeMessage {
    public String serialize(IMessage message);
}
