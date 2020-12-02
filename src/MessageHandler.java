import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Message Handler
 * <p>
 * Deals with interpretation of information from the client.
 * A Runnable (Threading) class used in the MessageServer to respond to MessageClient
 * with multiple threads.
 * Purpose/Functions (To be implemented in MessageServer):
 * Check Username and Password Format, if they exist, and then add/reject them to accounts.txt
 * Check if there are conversations and messages within conversations
 * Have add/edit/delete functionality of conversations and messages
 *
 * @author Alex Frey, Justin Leddy, Maeve Tra, Yifei Mao, Naveena Erranki
 * @version November 30th, 2020
 */
public class MessageHandler implements Runnable {
    //Socket to interact with MessageClient and Synchronized field
    private final Socket clientSocket;
    private final Object gateKeeper = new Object();
    private String userName;
    private String identity;
    private BufferedWriter clientWriter;
    private BufferedReader clientReader;
    private String clientMessage;
    private AtomicBoolean broadcastMessage;


    //fields
    private File accountList;

    //Constructor to initialize clientSocket
    public MessageHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            InputStream inputStream = this.clientSocket.getInputStream();
            OutputStream outputStream = this.clientSocket.getOutputStream();
            clientReader = new BufferedReader(new InputStreamReader(inputStream));
            clientWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Run method which uses clientSocket to interact with MessageClient
    @Override
    public void run() {

        synchronized (gateKeeper) {

            //try with resources, being the input and output streams, readers, and writers.
            try (var inputStream = this.clientSocket.getInputStream();
                 var outputStream = this.clientSocket.getOutputStream();
                 var clientReader = new BufferedReader(new InputStreamReader(inputStream));
                 var clientWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                 var fileReader = new BufferedReader(new FileReader("Accounts.txt"));
                 var fileWriter = new PrintWriter(new FileOutputStream("Accounts.txt", true))) {

                //read info from client
                clientMessage = clientReader.readLine();

                while (clientMessage != null) {
                    System.out.println(clientMessage); //print it for processing purposes

                    if (clientMessage.charAt(0) == 'M') { //incoming message is
                        broadcastMessage.set(true);
                    } else { //Login/Register processing
                        String[] info = clientMessage.split(":");
                        String username = info[1].strip(); //strip removes leading and trailing spaces
                        String password = info[2].strip();

                        //login
                        if (clientMessage.charAt(0) == 'L') {
                            String s = fileReader.readLine();
                            while ((s != null)) {
                                String currentUser = s.substring(0, s.indexOf(","));
                                String currentPass = s.substring(s.indexOf(",") + 1);

                                //once username and password is found, is true and break
                                if ((currentUser.equals(username)) && (currentPass.equals(password))) {
                                    clientWriter.write("true");
                                    clientWriter.newLine();
                                    clientWriter.flush();
                                    //Add the username to the map
                                    System.out.println(username);
                                    System.out.println(identity);
                                    UserManager.addTrace(username, identity);
                                    break;
                                }
                                s = fileReader.readLine();
                            }
                            //if username and password aren't found, is false
                            clientWriter.write("false");
                            clientWriter.newLine();
                            clientWriter.flush();

                        }
                        //register
                        else {
                            System.out.println("Enter registration method");
                            String s = fileReader.readLine();
                            while ((s != null)) {
                                String currentUser = s.substring(0, s.indexOf(","));

                                //if username is and password is taken, is true and break
                                if (currentUser.equals(username)) {
                                    clientWriter.write("true");
                                    clientWriter.newLine();
                                    clientWriter.flush();

                                    UserManager.addTrace(username, identity);
                                    break;
                                }
                                s = fileReader.readLine();
                            }

                            //if username is unique and now added to list of accounts
                            fileWriter.println(username + "," + password);
                            fileWriter.flush();
                            clientWriter.write("false");
                            clientWriter.newLine();
                            clientWriter.flush();
                        }
                    }
                    clientMessage = clientReader.readLine(); // to read a new line from client
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Sending message
    public void send(String str) {
        try {
            clientWriter.write(str);
            clientWriter.newLine();
            clientWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Account Format: username,password
    //get socket
    public Socket getClientSocket() {
        return clientSocket;
    }

    public String getClientMessage() {
        return clientMessage;
    }

    public AtomicBoolean getBroadcastMessage() {
        return broadcastMessage;
    }
}
