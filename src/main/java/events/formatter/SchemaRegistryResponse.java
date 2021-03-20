package events.formatter;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SchemaRegistryResponse {
        public final String subject;
        public final int subjectVersion;
        public final int id;
        public final String schema;

        public SchemaRegistryResponse(@JsonProperty("subject") String subject,
                              @JsonProperty("version") int subjectVersion,
                              @JsonProperty("id") int id,
                              @JsonProperty("schema") String schema) {
            this.subject = subject;
            this.subjectVersion = subjectVersion;
            this.id = id;
            this.schema= schema;
        }
}
