package events.formatter;

import java.io.ByteArrayInputStream;
import java.util.Map;

import events.IMessage;

public class SplitDeserialiser {

    private Map<String, IDeserializeMessage> deserialisers;

    public SplitDeserialiser(Map<String, IDeserializeMessage> deserialisers) {
        this.deserialisers = deserialisers;
    }

    public IMessage deserialize(String serdes, ByteArrayInputStream body) throws Exception {
        return deserialisers.get(serdes).deserialize(body);
    }
}