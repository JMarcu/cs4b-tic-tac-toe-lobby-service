import java.io.*;
import java.nio.channels.FileChannel;
import org.apache.catalina.startup.Tomcat;

public class App {
    private final String SERVICE_NAME = "lobby-service";
    
    public static void main(String[] args) throws Exception {
        final App app = new App();
        app.launchServer();
    }

    private void launchServer() {
        //This is an embedded Tomcat webserver which will host the service.
        Tomcat tomcat = new Tomcat();

        /*Bind to proper port. Heroku will inject this through a system variable,
          but we use a default value for local development. */
        String webPort = System.getenv("PORT");
        if(webPort == null || webPort.isEmpty()) {
            webPort = "4205";
        }
        tomcat.setPort(Integer.parseInt(webPort));

        try {
            //Get a reference to the service's jar file.
            File jar = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

            //Create a destination directory to run the jar file from, inside of the WEB-INF folder.
            File targetFolder = new File(SERVICE_NAME + File.separator + "WEB-INF" + File.separator +"lib");
            File destination = new File(targetFolder.getPath() + File.separator + SERVICE_NAME + ".jar");
            targetFolder.mkdirs();
    
            //Copy the service's jar file to the WEB-INF directory.
            FileChannel sourceChannel = new FileInputStream(jar).getChannel();
            FileChannel destChannel = new FileOutputStream(destination).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            sourceChannel.close();
            destChannel.close();

            //Add the webapp context to the embedded Tomcat instance.
            tomcat.addWebapp("/", new File(SERVICE_NAME).getAbsolutePath());

            //Start the server.
            tomcat.start();
            tomcat.getServer().await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
