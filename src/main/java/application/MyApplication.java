package application;

import application.domain.Currency;
import application.domain.Money;
import application.domain.event.TransactionClearedEvent;
import events.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.UUID;


public class MyApplication {
    public static void main(String[] args) {

        ApplicationContext appContext
                = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

        IConsume consumer = (IConsume) appContext.getBean("messageConsumer");
        IDispatch dispatcher = (IDispatch) appContext.getBean("dispatcher");
        dispatcher.subscribe("transaction_cleared", (IHandle) appContext.getBean("doSomething"));

        try {
            consumer.consume();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}