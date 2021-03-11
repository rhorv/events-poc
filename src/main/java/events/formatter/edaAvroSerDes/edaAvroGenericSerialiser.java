package events.formatter.edaAvroSerDes;

import events.IMessage;
import events.formatter.ISerializeMessage;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class edaAvroGenericSerialiser implements ISerializeMessage {
    private Schema.Parser parser;
    private Schema avroSchema;
    private GenericDatumWriter<GenericRecord> avroWriter;
    private EncoderFactory encoderFactory;

    public edaAvroGenericSerialiser() throws IOException {
        this.parser = new Schema.Parser();
        this.avroSchema = parser.parse( new File("./src/main/avro/events-poc.avsc"));
        this.avroWriter = new GenericDatumWriter<>(avroSchema);
        this.encoderFactory = EncoderFactory.get();
    }

    public ByteArrayOutputStream serialize(IMessage message) throws Exception {

        GenericRecord avroRecord = new GenericData.Record(avroSchema);
        avroRecord.put("eventId", UUID.randomUUID().toString());
        avroRecord.put("name", message.getName());
        avroRecord.put("category", message.getCategory());
        avroRecord.put("occurredAt", message.getOccurredAt().toString());
        avroRecord.put("version", message.getVersion());
        avroRecord.put("payload", message.getPayload());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BinaryEncoder avroEncoder = encoderFactory.binaryEncoder(stream, null);
        avroWriter.write(avroRecord, avroEncoder);
        avroEncoder.flush();
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
