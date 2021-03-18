package events.formatter.hav1;

import events.IMessage;
import events.Message;
import events.formatter.Envelope;
import events.formatter.IDeserializeMessage;
import events.formatter.IProvideSchema;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.util.Utf8;
import org.joda.time.DateTime;

public class Hav1Deserializer implements IDeserializeMessage {

  private IProvideSchema schemaProvider;
  public static final String NAME = "hav1";

  public Hav1Deserializer(IProvideSchema schemaProvider) {
    this.schemaProvider = schemaProvider;
  }

  public IMessage deserialize(Envelope envelope) throws Exception {

    if (!envelope.compatibleWith(1)) {
      throw new Exception();
    }

    Parser parser = new Parser();
    Schema avroSchema = parser.parse(this.schemaProvider.get());
    GenericRecord record = avroToRecord(envelope.getBody(), avroSchema);

    Map<String, String> payload = new HashMap<>();
    for (Map.Entry<Utf8, Utf8> entry : ((Map<Utf8, Utf8>) record.get("payload")).entrySet()) {
      payload.put(entry.getKey().toString(), entry.getValue().toString());
    }

    return new Message(
        record.get("name").toString(),
        payload,
        Integer.valueOf(record.get("version").toString()),
        new DateTime(record.get("occurred_at").toString()),
        record.get("category").toString());
  }

  public GenericRecord avroToRecord(byte[] data, Schema schema) throws IOException {
    DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema);
    Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
    return reader.read(null, decoder);
  }

}