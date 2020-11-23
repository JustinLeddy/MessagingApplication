import java.io.*;
import java.net.Socket;

/**
 * Message Handler
 *
 * Deals with interpretation of information from the client.
 * A Runnable (Threading) class used in the MessageServer to respond to MessageClient
 * with multiple threads.
 * Purpose/Functions (To be implemented in MessageServer):
 *      Check Username and Password Format, if they exist, and then add/reject them to accounts.txt
 *      Check if there are conversations and messages within conversations
 *      Have add/edit/delete functionality of conversations and messages
 *
 * @author Alex Frey, Justin Leddy, Maeve Tra, Yifie Mao, Naveena Erranki
 * @version November 30th, 2020
 */
public class MessageHandler implements Runnable {
    //Socket to interact with MessageClient and Synchronized field
    private final Socket clientSocket;
    private final Object gateKeeper = new Object();

    //Constructor to initialize clientSocket
    public MessageHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    //Run method which uses clientSocket to interact with MessageClient
    @Override
    public void run() {
        synchronized (gateKeeper) {
            try (var inputStream = this.clientSocket.getInputStream();
                 var outputStream = this.clientSocket.getOutputStream();
                 var reader = new BufferedReader(new InputStreamReader(inputStream));
                 var writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
                //TODO Implement receiving info from MessageClient in order

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //Account Format: username,password


}
