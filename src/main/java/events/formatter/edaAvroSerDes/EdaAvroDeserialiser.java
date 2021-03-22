package events.formatter.edaAvroSerDes;

import events.IMessage;
import events.Message;
import events.formatter.Envelope;
import events.formatter.IDeserializeMessage;
import events.formatter.IProvideSchema;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

public class EdaAvroDeserialiser implements IDeserializeMessage {
    private Schema.Parser parser;
    private Schema avroSchema;
    private GenericRecord avroRecord;
    private GenericDatumReader<GenericRecord> avroReader;
    private DecoderFactory decoderFactory;
    private IProvideSchema schemaProvider;

    public EdaAvroDeserialiser(IProvideSchema schemaProvider) throws IOException {
        this.schemaProvider = schemaProvider;
        this.parser = new Schema.Parser();
        this.avroSchema = parser.parse( schemaProvider.get() );
        this.avroRecord = new GenericData.Record(avroSchema);
        this.avroReader = new GenericDatumReader<>(avroSchema);
        this.decoderFactory = DecoderFactory.get();
    }

    @Override
    public IMessage deserialize(Envelope envelope) throws Exception {
        if (!envelope.compatibleWith(1)) {
            throw new Exception();
        }

        BinaryDecoder avroDecoder = decoderFactory.binaryDecoder(envelope.getBody(), null);
        avroReader.read(avroRecord, avroDecoder);

        return new Message(
                avroRecord.get("name").toString(),
                avroRecord.get("eventId").toString(),
                (Map<String, String>) avroRecord.get("payload"),
                Integer.valueOf(avroRecord.get("version").toString()),
                new DateTime(avroRecord.get("occurredAt").toString()),
                avroRecord.get("category").toString());
    }

    public GenericRecord avroToJson(ByteArrayInputStream json, Schema schema) throws IOException {
        DatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
        String data = IOUtils.toString(json);
        Decoder decoder = DecoderFactory.get().jsonDecoder(schema, data);
        return reader.read(null, decoder);
    }
}
