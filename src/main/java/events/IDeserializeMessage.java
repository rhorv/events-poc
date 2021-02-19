package events;

public interface IDeserializeMessage {
    public IMessage deserialize(String body) throws Exception;
}
