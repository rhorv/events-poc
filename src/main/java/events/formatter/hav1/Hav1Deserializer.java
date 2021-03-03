package events.formatter.hav1;

import events.IMessage;
import events.Message;
import events.formatter.IDeserializeMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;

public class Hav1Deserializer implements IDeserializeMessage {
  private Parser parser;
  private Schema avroSchema;
  private GenericRecord avroRecord;
  private GenericDatumReader<GenericRecord> avroReader;
  private DecoderFactory decoderFactory;

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
          + "                \"type\" : \"string\"},\n"
          + "\n"
          + "                {\"name\" : \"occurred_at\", \n"
          + "                \"type\" : \"string\"},\n"
          + "\n"
          + "                {\"name\" : \"version\", \n"
          + "                \"type\" : \"int\"}]\n"
          + "} ";

  public Hav1Deserializer() {
    this.parser = new Parser();
    this.avroSchema = parser.parse(this.schema);
    this.avroRecord = new GenericData.Record(avroSchema);
    this.avroReader = new GenericDatumReader<>(avroSchema);
    this.decoderFactory = DecoderFactory.get();
  }

  public IMessage deserialize(ByteArrayInputStream body) throws Exception {
    BinaryDecoder avroDecoder = decoderFactory.binaryDecoder(body, null);
    avroReader.read(avroRecord, avroDecoder);

    return new Message(
        avroRecord.get("name").toString(),
        new HashMap<>(),
        Integer.valueOf(avroRecord.get("version").toString()),
        new DateTime(avroRecord.get("occurredAt")),
        avroRecord.get("category").toString());
  }

  public GenericRecord avroToJson(ByteArrayInputStream json, Schema schema) throws IOException {
    DatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
    String data = IOUtils.toString(json);
    Decoder decoder = DecoderFactory.get().jsonDecoder(schema, data);
    return reader.read(null, decoder);
  }
}
