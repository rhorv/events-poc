package events.formatter.hav1;

import events.IMessage;
import events.Message;
import events.formatter.IDeserializeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;

public class Hav1Deserializer implements IDeserializeMessage {

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

  public IMessage deserialize(ByteArrayInputStream body) throws Exception {
    Parser parser = new Parser();
    Schema avroSchema = parser.parse(this.schema);
    GenericRecord record = avroToJson(body, avroSchema);

    return new Message(
        record.get("name").toString(),
        new HashMap<>(),
        Integer.valueOf(record.get("version").toString()),
        new DateTime(record.get("occurredAt")),
        record.get("category").toString());
  }

  public GenericRecord avroToJson(ByteArrayInputStream json, Schema schema) throws IOException {
    DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema);
    String data = IOUtils.toString(json);
    Decoder decoder = DecoderFactory.get().jsonDecoder(schema, data);
    return reader.read(null, decoder);
  }
}
