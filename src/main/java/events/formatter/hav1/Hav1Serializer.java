package events.formatter.hav1;

import events.IMessage;
import events.formatter.ISerializeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

  private String schema =
      "{\n"
          + "    \"type\" : \"record\",\n"
          + "    \"name\" : \"hav1\",\n"
          + "    \"namespace\" : \"com.worldpay.poc\",\n"
          + "    \"fields\" : [{\"name\" : \"id\", \n"
          + "                \"type\" : \"string\", \n"
          + "                \"logicalType\" : \"uuid\"},\n"
          + "\n"
          + "\t\t\t\t{\"name\" : \"name\", \n"
          + "                \"type\" : \"string\", \n"
          + "                \"default\" : \"NONE\"},\t\t\t\t\n"
          + "\n"
          + "                {\"name\" : \"payload\", \n"
          + "                \"type\" : {\n"
          + "\t\t\t\t\t\"type\": \"map\",\n"
          + "\t\t\t\t\t\"values\": \"string\" }\n"
          + "\t\t\t\t},\n"
          + "\n"
          + "                {\"name\" : \"category\", \n"
          + "                \"type\" : {\n"
          + "\t\t\t\t\t\"type\": \"enum\", \n"
          + "\t\t\t\t\t\"name\": \"category\",\n"
          + "\t\t\t\t\t\"symbols\" : [\"event\", \"command\"]}\n"
          + "\t\t\t\t},\n"
          + "\n"
          + "                {\"name\" : \"occurred_at\", \n"
          + "                \"type\" : \"string\"},\n"
          + "\n"
          + "                {\"name\" : \"version\", \n"
          + "                \"type\" : \"int\"}]\n"
          + "} ";

  public ByteArrayOutputStream serialize(IMessage message) throws Exception {

    Parser parser = new Parser();
    Schema avroSchema = parser.parse(this.schema);
    GenericRecord avroRecord = new Record(avroSchema);
    avroRecord.put("id", UUID.randomUUID().toString());
    avroRecord.put("name", message.getName());
    avroRecord.put("category", message.getCategory());
    avroRecord.put("occurred_at", message.getOccurredAt().toString());
    avroRecord.put("version", message.getVersion());
    avroRecord.put("payload", message.getPayload());

    byte[] output = this.jsonToAvro(avroRecord.toString(), new Parser().parse(this.schema));
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    stream.write(output);
    return stream;
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
