package events.publisher.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import events.IMessage;
import events.formatter.ISerializeMessage;
import events.publisher.IPublish;
import java.io.ByteArrayOutputStream;

public class RabbitMqPublisher implements IPublish {

  private Connection connection;
  private ISerializeMessage formatter;
  private ConnectionFactory connectionFactory;
  private String exchangeName;

  public RabbitMqPublisher(ISerializeMessage formatter, ConnectionFactory connectionFactory,
      String exchangeName) throws Exception {
    this.formatter = formatter;
    this.exchangeName = exchangeName;
    this.connectionFactory = connectionFactory;
  }

  private Connection connect() throws Exception {
    if (this.connection == null) {
      this.connection = this.connectionFactory.newConnection();
    }
    return this.connection;
  }

  public void publish(IMessage message) throws Exception {
    Connection connection = connect();
    Channel channel = connection.createChannel();
    channel.exchangeDeclare(this.exchangeName, "fanout", true);
    ByteArrayOutputStream body = this.formatter.serialize(message);
    channel.basicPublish(this.exchangeName, "", null, body.toByteArray());
    System.out.println("[x] Published '" + body + "'");
  }
}
