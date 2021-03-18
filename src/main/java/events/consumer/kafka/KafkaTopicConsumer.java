package events.consumer.kafka;

import events.consumer.IConsume;
import events.dispatcher.IDispatch;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import events.formatter.SplitDeserialiser;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

public class KafkaTopicConsumer implements IConsume {

  private SplitDeserialiser deserialiser;
  private IDispatch dispatcher;
  private String server;
  private String topicName;

  public KafkaTopicConsumer(
          SplitDeserialiser deserialiser, IDispatch dispatcher, String server, String topicName) {
    this.deserialiser = deserialiser;
    this.dispatcher = dispatcher;
    this.server = server;
    this.topicName = topicName;
  }

  private Consumer<Long, byte[]> createConsumer(String server, String topicName) {
    final Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaExampleConsumer");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.LongDeserializer");
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

    // Create the consumer using props.
    final Consumer<Long, byte[]> consumer = new KafkaConsumer<>(props);

    // Subscribe to the topic.
    consumer.subscribe(Collections.singletonList(topicName));
    return consumer;
  }

  private void runConsumer(Integer giveUp) throws InterruptedException, IOException {
    Consumer<Long, byte[]> consumer = createConsumer(this.server, this.topicName);
    int noRecordsCount = 0;

    while (true) {
      final ConsumerRecords<Long, byte[]> consumerRecords = consumer.poll(Duration.ofSeconds(1000));

      if (consumerRecords.count() == 0) {
        noRecordsCount++;
        if (noRecordsCount > giveUp) {
          break;
        } else {
          continue;
        }
      }

      consumerRecords.forEach(
          record -> {
            try {
              System.out.printf(
                      "[x] Consumer Record:(Partition=%d, Offset=%d, Key=%d, Payload=%s)\n",
                      record.partition(), record.offset(), record.key(), new String(record.value(), StandardCharsets.UTF_8));

              ByteArrayInputStream payLoad = new ByteArrayInputStream(record.value());
              String serde = new String( record.headers().lastHeader("nameSerde").value(), StandardCharsets.UTF_8 );
              dispatcher.dispatch(deserialiser.deserialize(serde, payLoad));
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
