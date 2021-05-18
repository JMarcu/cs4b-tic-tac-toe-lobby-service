package services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageExecutor extends Thread{
    private static MessageExecutor instance;

    private ExecutorService executor;
    private LinkedBlockingQueue<Runnable> queue; 
    
    private MessageExecutor(){
        executor = Executors.newFixedThreadPool(20);
        queue = new LinkedBlockingQueue<Runnable>();
    }

    public void queueMessageHandler(Runnable handler) throws InterruptedException{
        // queue.put(handler);
        executor.execute(handler);
    }
    
    public void run() {
        // while(true){
        //     if(!queue.isEmpty()){
        //         try {
        //             executor.execute(queue.take());
        //         } catch (InterruptedException e) {
        //             e.printStackTrace();
        //         }
        //     }
        // }
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
