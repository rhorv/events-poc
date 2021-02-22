package events.consumer.rabbitmq;

import com.rabbitmq.client.*;
import events.consumer.IConsume;
import events.formatter.IDeserializeMessage;
import events.dispatcher.IDispatch;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RabbitMQConsumer implements IConsume {

    private Connection connection;
    private IDeserializeMessage formatter;
    private IDispatch dispatcher;
    private ConnectionFactory connectionFactory;
    private String queueName;

    public RabbitMQConsumer(
            IDeserializeMessage formatter,
            IDispatch dispatcher,
            ConnectionFactory connectionFactory,
            String queueName
    ) throws Exception {
        this.formatter = formatter;
        this.dispatcher = dispatcher;
        this.connectionFactory = connectionFactory;
        this.queueName = queueName;
    }

    private Connection connect() throws Exception {
        if (this.connection == null) {
            this.connection = this.connectionFactory.newConnection();
        }
        return this.connection;
    }

    public void consume() throws Exception {

        Connection connection = connect();
        Channel channel = connection.createChannel();
        channel.basicConsume(this.queueName, false, "myConsumerTag",
                new DefaultConsumer(channel) {
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope,
                                               AMQP.BasicProperties properties,
                                               byte[] body)
                            throws IOException {
                        String routingKey = envelope.getRoutingKey();
                        String contentType = properties.getContentType();
                        long deliveryTag = envelope.getDeliveryTag();
                        String message = new String(body, StandardCharsets.UTF_8);
                        System.out.println("[x] Received '" + message + "'");
                        try {
                            dispatcher.dispatch(formatter.deserialize(message));
                            channel.basicAck(deliveryTag, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
