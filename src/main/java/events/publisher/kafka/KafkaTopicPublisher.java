package events.publisher.kafka;

import events.IMessage;
import events.IPublish;
import events.ISerializeMessage;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;
import java.util.Properties;

public class KafkaTopicPublisher implements IPublish {

    private ISerializeMessage formatter;
    private Producer<Long, String> producer;
    private String topicName;

    public KafkaTopicPublisher(ISerializeMessage formatter, Map<String, String> properties)  {
        this.formatter = formatter;
        this.producer = createProducer(properties.get("server"));
        this.topicName = properties.get("topicName");
    }

    private Producer<Long, String> createProducer(String server) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                server);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    void runProducer(IMessage message) throws Exception {
        long time = System.currentTimeMillis();

        try {
            for (long index = time; index < time + 1; index++) {
                final ProducerRecord<Long, String> record =
                        new ProducerRecord<>(this.topicName, index,
                                this.formatter.serialize(message));

                RecordMetadata metadata = this.producer.send(record).get();

                long elapsedTime = System.currentTimeMillis() - time;
                System.out.printf("[x] sent record(key=%s value=%s) " +
                                "meta(partition=%d, offset=%d) time=%d\n",
                        record.key(), record.value(), metadata.partition(),
                        metadata.offset(), elapsedTime);

            }
        } finally {
            producer.flush();
//            producer.close();
        }
    }

    public void publish(IMessage message) throws Exception {
        runProducer(message);
    }
}
