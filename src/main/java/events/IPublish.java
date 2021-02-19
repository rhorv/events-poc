package events;

public interface IPublish {
    public void publish(IMessage message) throws Exception;
}
