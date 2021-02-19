package events;

public interface IDispatch {
    public void dispatch(IMessage message);
    public void subscribe(String messageName, IHandle handler);
}
