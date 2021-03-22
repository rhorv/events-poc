package events.formatter.edaAvroSerDes;

import events.formatter.IProvideSchema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EdaAvroFileBasedSchemaProvider implements IProvideSchema {
    @Override
    public String get() {
        String schema = "";
        try {
            schema = Files.readString(Path.of("./src/main/avro/events-poc.avsc"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return schema;
    }
}
