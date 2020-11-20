import java.io.IOException;
import java.net.ServerSocket;

/**
 * Message Server
 *
 * The server for the social messaging application which uses the MessageHandler
 * class to respond to the MessageClient on multiple threads
 * Purpose/Functions (To be implemented from MessageHandler):
 *      Check Username and Password Format, if they exist, and then add/reject them to accounts.txt
 *      Check if there are conversations and messages within conversations
 *      Have add/edit/delete functionality of conversations and messages
 *
 *
 * @author Alex Frey, Justin Leddy, Maeve Tra, Yifie Mao, Naveena Erranki
 * @version November 30th, 2020
 */
public class MessageServer {
    // ServerSocket for server
    private final ServerSocket serverSocket;


    //Constructor to construct server off passed port
    public MessageServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public void serveClient() {
        //TODO Serve Client using MessageHandler threading
    }






}
