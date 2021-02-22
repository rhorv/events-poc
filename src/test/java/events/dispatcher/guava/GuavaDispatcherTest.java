package events.dispatcher.guava;

import events.dispatcher.IHandle;
import events.IMessage;
import events.Message;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;

import static org.mockito.Mockito.*;

class GuavaDispatcherTest {

    private GuavaDispatcher dispatcher;
    private IHandle handler;

    @BeforeEach
    public void setUp() throws Exception {
        this.dispatcher = new GuavaDispatcher();
        this.handler = mock(IHandle.class);
    }

    @Test
    void testItDispatchesMessagesToSubscribedHandler() throws Exception {
        IMessage message = new Message("event_name", new HashMap<String, String>(), 1, new DateTime("2020-09-15T15:53:00+01:00"), "event");
        this.dispatcher.subscribe("event_name", this.handler);
        this.dispatcher.dispatch(message);
        verify(this.handler, Mockito.times(1)).handle(message);
    }

    @Test
    void testItAllowsHandlerToSubscribeForAMessageByName() throws Exception {
        IMessage message = new Message("event_name", new HashMap<String, String>(), 1, new DateTime("2020-09-15T15:53:00+01:00"), "event");
        this.dispatcher.dispatch(message);
        verify(this.handler, Mockito.times(0)).handle(message);

        this.dispatcher.subscribe("event_name", this.handler);
        this.dispatcher.dispatch(message);
        verify(this.handler, Mockito.times(1)).handle(message);
    }

}