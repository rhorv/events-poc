package application;

import application.domain.Currency;
import application.domain.Money;
import application.domain.event.TransactionClearedEvent;
import events.publisher.IPublish;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.UUID;


public class QuickPublish {
    public static void main(String[] args) {

        ApplicationContext appContext
                = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

        IPublish publisher = (IPublish) appContext.getBean("messagePublisher");
        try {
            publisher.publish(new TransactionClearedEvent(UUID.randomUUID(), new Money(123, Currency.valueOf("GBP"))));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}