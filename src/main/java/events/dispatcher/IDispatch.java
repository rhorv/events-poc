package events.dispatcher;

import events.IMessage;

public interface IDispatch {

  public void dispatch(IMessage message);

  public void subscribe(String messageName, IHandle handler);
}
