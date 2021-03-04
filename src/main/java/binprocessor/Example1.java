package binprocessor;

import binprocessor.domain.event.FileArrivedEvent;
import events.publisher.IPublish;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Example1 {

  public static void main(String[] args) {

    ApplicationContext appContext =
        new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

    IPublish publisher = (IPublish) appContext.getBean("messagePublisher");
    try {
      publisher.publish(new FileArrivedEvent("visa"));
      publisher.publish(new FileArrivedEvent("visa2"));
      publisher.publish(new FileArrivedEvent("visa3"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
