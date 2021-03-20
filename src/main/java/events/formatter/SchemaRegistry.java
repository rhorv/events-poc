package events.formatter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Supplier;

public class SchemaRegistry implements IProvideSchema {

    public String getSchema(String subject) throws IOException, InterruptedException {
        // create a client
        var client = HttpClient.newHttpClient();

        // Prepare URL for request, you need to provide URL of Schema Registry and name of kafka topic
        String url = String.format("%s/subjects/%s-value/versions/latest", "http://schema-registry:8081", subject);

        // create a request
        var request = HttpRequest.newBuilder(URI.create(url))
                .header("accept", "application/json")
                .build();

        // use the client to send the request
        HttpResponse<Supplier<SchemaRegistryResponse>> response = client.send(request, new JsonBodyHandler<>(SchemaRegistryResponse.class));

        // the response:
        return response.body().get().schema;
    }

    @Override
    public String get() {
        try {
            return getSchema("edaAvroGenericEvent");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // ToDo, Error Handling
        return "{}";
    }
}
