package events.formatter;

import events.IMessage;

public interface IDeserializeMessage {

  public IMessage deserialize(String body) throws Exception;
}
