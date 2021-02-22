package application;

import events.consumer.IConsume;
import events.dispatcher.IDispatch;
import events.dispatcher.IHandle;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


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