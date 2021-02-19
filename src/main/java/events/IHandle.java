package events;

public interface IHandle {
    public void handle(IMessage message) throws Exception;
}
