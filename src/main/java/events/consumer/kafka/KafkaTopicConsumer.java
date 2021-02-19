package events.consumer.kafka;

import events.IConsume;
import events.IDeserializeMessage;
import events.IDispatch;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class KafkaTopicConsumer implements IConsume {

    private IDeserializeMessage formatter;
    private IDispatch dispatcher;
    private Consumer<Long, String> consumer;

    public KafkaTopicConsumer(IDeserializeMessage formatter, IDispatch dispatcher, Map<String, String> properties) {
        this.formatter = formatter;
        this.dispatcher = dispatcher;
        this.consumer = createConsumer(properties.get("server"), properties.get("topicName"));
    }

    private Consumer<Long, String> createConsumer(String server, String topicName) {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                server);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                "KafkaExampleConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest");

        // Create the consumer using props.
        final Consumer<Long, String> consumer =
                new KafkaConsumer<>(props);

        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(topicName));
        return consumer;
    }

    private void runConsumer(Integer giveUp) throws InterruptedException {
        int noRecordsCount = 0;

        while (true) {
            final ConsumerRecords<Long, String> consumerRecords =
                    this.consumer.poll(1000);

            if (consumerRecords.count()==0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else continue;
            }

            consumerRecords.forEach(record -> {
                System.out.printf("[x] Consumer Record:(%d, %s, %d, %d)\n",
                        record.key(), record.value(),
                        record.partition(), record.offset());
                try {
                    dispatcher.dispatch(formatter.deserialize(record.value()));
                    consumer.commitAsync();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }
        consumer.close();
        System.out.println("DONE");
    }

    public void consume() throws Exception {
        runConsumer(100);
    }
}
