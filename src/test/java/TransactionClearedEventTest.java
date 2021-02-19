import application.domain.event.TransactionClearedEvent;
import events.IHandle;
import events.IMessage;
import events.Message;
import events.dispatcher.guava.GuavaDispatcher;
import events.formatter.RJS1.RJS1Serializer;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class TransactionClearedEventTest {

    private Map<String, String> validPayload;
    private Map<String, String> invalidPayload;

    @BeforeEach
    public void setUp() throws Exception {
        this.validPayload = new HashMap<String, String>();
        this.validPayload.put("transactionId", UUID.randomUUID().toString());
        this.validPayload.put("interchangeFeeAmount", "123");
        this.validPayload.put("interchangeFeeCurrency", "GBP");

        this.invalidPayload = new HashMap<String, String>();
    }


    @Test
    void testItFailsToCreateOnInCompatibleMessageName() {
        IMessage message = new Message("event_name", this.validPayload, 1, new DateTime("2020-09-15T15:53:00+01:00"), "event");
        assertThrows(Exception.class, () -> {
            TransactionClearedEvent.fromMessage(message);
        });
    }

    @Test
    void testItFailsToCreateOnInCompatiblePayload() {
        IMessage message = new Message(TransactionClearedEvent.NAME, this.invalidPayload, 1, new DateTime("2020-09-15T15:53:00+01:00"), "event");
        assertThrows(Exception.class, () -> {
            TransactionClearedEvent.fromMessage(message);
        });
    }

    @Test
    void testItSucceedsInCreatingTheEventOnCompatibleMessage() throws Exception {
        IMessage message = new Message(TransactionClearedEvent.NAME, this.validPayload, 1, new DateTime("2020-09-15T15:53:00+01:00"), "event");
        TransactionClearedEvent event = TransactionClearedEvent.fromMessage(message);
        assertEquals(event.getName(), TransactionClearedEvent.NAME);
        assertEquals(event.getTransactionId().toString(), this.validPayload.get("transactionId"));
        assertEquals(event.getInterchangeFee().getAmount(), Integer.valueOf(this.validPayload.get("interchangeFeeAmount")));
        assertEquals(event.getInterchangeFee().getCurrency().toString(), this.validPayload.get("interchangeFeeCurrency"));
    }
}