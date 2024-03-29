package events.publisher;

import events.IMessage;
import java.util.ArrayList;
import java.util.List;

public class Buffer implements IPublish {

  private List<IMessage> messages = new ArrayList<IMessage>();
  private IPublish publisher;

  public Buffer(IPublish publisher) {
    this.publisher = publisher;
  }

  public void publish(IMessage message) {
    messages.add(message);
  }

  public void flush() throws Exception {
    for (IMessage message : this.messages) {
      this.publisher.publish(message);
    }
  }
}
