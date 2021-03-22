package application;

import events.Message;
import events.publisher.IPublish;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Example2: In this example we will have 3 versions of the same message (1,2,3) with different.
 * payloads We will publish all 3. The application then is configured to use v2 messages of this
 * event So it will interpret all 3 of them as version 2
 */
public class Example2 {

  public static void main(String[] args) {

    ApplicationContext appContext =
        new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

    IPublish publisher = (IPublish) appContext.getBean("messagePublisher");

    // v1 payload
    Map<String, String> v1payload = new HashMap<String, String>();
    v1payload.put("transactionId", UUID.randomUUID().toString());

    // v2 payload
    Map<String, String> v2payload = new HashMap<String, String>();
    v2payload.put("transactionId", UUID.randomUUID().toString());
    v2payload.put("reason", "some reason");

    // v3 payload
    Map<String, String> v3payload = new HashMap<String, String>();
    v3payload.put("transactionId", UUID.randomUUID().toString());
    v3payload.put("reason", "another reason");
    v3payload.put("feedback", "5");

    try {
      publisher.publish(
          new Message("transaction_cancelled", v1payload.get("transactionId"), v1payload, 1, new DateTime(), "event"));
      publisher.publish(
          new Message("transaction_cancelled", v2payload.get("transactionId"), v2payload, 2, new DateTime(), "event"));
      publisher.publish(
          new Message("transaction_cancelled", v3payload.get("transactionId"), v3payload, 3, new DateTime(), "event"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
