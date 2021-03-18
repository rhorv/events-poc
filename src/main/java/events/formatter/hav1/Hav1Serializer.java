package events.formatter.hav1;

import events.IMessage;
import events.formatter.Envelope;
import events.formatter.IProvideSchema;
import events.formatter.ISerializeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;

public class Hav1Serializer implements ISerializeMessage {

  private IProvideSchema schemaProvider;
  private static final String NAME = "hav1";

  public Hav1Serializer(IProvideSchema schemaProvider) {
    this.schemaProvider = schemaProvider;
  }

  public Envelope serialize(IMessage message) throws Exception {

    String id = UUID.randomUUID().toString();
    Parser parser = new Parser();
    Schema avroSchema = parser.parse(this.schemaProvider.get());
    GenericRecord avroRecord = new Record(avroSchema);
    avroRecord.put("id", id);
    avroRecord.put("name", message.getName());
    avroRecord.put("category", message.getCategory());
    avroRecord.put("occurred_at", message.getOccurredAt().toString());
    avroRecord.put("version", message.getVersion());
    avroRecord.put("payload", message.getPayload());

    byte[] output = this
        .jsonToAvro(avroRecord.toString(), new Parser().parse(this.schemaProvider.get()));
    return Envelope.v1(id, NAME, output);
  }

  public byte[] jsonToAvro(String json, Schema schema) throws IOException {
    DatumReader<Object> reader = new GenericDatumReader<>(schema);
    GenericDatumWriter<Object> writer = new GenericDatumWriter<>(schema);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    Decoder decoder = DecoderFactory.get().jsonDecoder(schema, json);
    Encoder encoder = EncoderFactory.get().binaryEncoder(output, null);
    Object datum = reader.read(null, decoder);
    writer.write(datum, encoder);
    encoder.flush();
    return output.toByteArray();
  }
}