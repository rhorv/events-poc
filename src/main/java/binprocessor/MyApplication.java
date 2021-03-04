package binprocessor;

import events.consumer.IConsume;
import events.dispatcher.IDispatch;
import events.dispatcher.IHandle;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MyApplication {

  public static void main(String[] args) {

    ApplicationContext appContext =
        new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

    IConsume consumer = (IConsume) appContext.getBean("messageConsumer");
    IDispatch dispatcher = (IDispatch) appContext.getBean("dispatcher");
    dispatcher.subscribe(
        "file_arrived", (IHandle) appContext.getBean("cardBinDefinitionProcessManager"));
    dispatcher.subscribe(
        "bin_definition_process_finished",
        (IHandle) appContext.getBean("cardBinDefinitionProcessManager"));
    dispatcher.subscribe(
        "organize_visa_source", (IHandle) appContext.getBean("organizeVisaSource"));
    dispatcher.subscribe(
        "visa_source_organized", (IHandle) appContext.getBean("startCardBinDefinitionProcessing"));
    dispatcher.subscribe(
        "start_visa_bin_processing", (IHandle) appContext.getBean("cardBinDefinitionRegister"));

    try {
      consumer.consume();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
