package events.dispatcher.guava;

import com.google.common.eventbus.EventBus;
import events.dispatcher.IDispatch;
import events.dispatcher.IHandle;
import events.IMessage;

import java.util.HashMap;
import java.util.Map;

public class GuavaDispatcher implements IDispatch {

    private Map<String, EventBus> bus = new HashMap<String, EventBus>();

    public void subscribe(String messageName, IHandle handler) {
        if (!bus.containsKey(messageName)) {
            bus.put(messageName, new EventBus());
        }

        EventBus channel = bus.get(messageName);
        channel.register(new GuavaListener(handler));
    }

    public void dispatch(IMessage message) {
        if (!bus.containsKey(message.getName())) {
            return;
        }
        EventBus channel = bus.get(message.getName());
        channel.post(message);
    }
}
