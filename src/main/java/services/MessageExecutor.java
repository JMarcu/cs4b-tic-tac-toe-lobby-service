package services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageExecutor{

    private static MessageExecutor instance;

    private ExecutorService executor;
    
    private MessageExecutor(){
        executor = Executors.newFixedThreadPool(20);
    }

    public void queueMessageHandler(Runnable handler) throws InterruptedException{
        executor.execute(handler);
    }

   /** 
    * Creates an instance of this object type. The method checks if there is a preexisting instance. 
    * If there is a preexisting instance of this object type, the method returns the existing object. 
    * If there isn't a preexisting instance of this object type, the method creates a new instance and returns it.
    * @return   
    */
    public static MessageExecutor getInstance(){
        if(instance == null){
            instance = new MessageExecutor();
        }

        return instance; 
    }
}
