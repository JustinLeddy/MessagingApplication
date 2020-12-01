import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
    private String userFrom;
    private String message;
    private String userToSend;
    private String identity;
    private BufferedWriter clientWriter;
    private BufferedReader clientReader;
    private InputStream inputStream;
    private OutputStream outputStream;


    //fields
    private File accountList;

    //Constructor to initialize clientSocket
    public MessageHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            inputStream = this.clientSocket.getInputStream();
            outputStream = this.clientSocket.getOutputStream();
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
                String clientMessage = clientReader.readLine();
                System.out.println(clientMessage); //print it for processing purposes





                if (clientMessage.charAt(0) == 'M') { //incoming message is a message to the server
                    //format for incoming messages M|SendingUserName|ReceivingUserName|Message
                    String[] messageSplit = clientMessage.split("\\|");
                    //splits message into components to use

                    userFrom = messageSplit[1];
                    userToSend = messageSplit[2];
                    message = messageSplit[3];
                    //storeInfo.put(userName, identity);//stores user info with their ip address & port number pair
                    //deliverTo.put(identity, clientWriter);//stores user identity with their specific clientWriter

                    //sendToSomeone(userToSend, message);
                    //echoMessage(userToSend, message);
                    //sendToAll(message);

                    MessageHandler mh = ClientManager.getTrace(UserManager.getTrace(userToSend));
                    mh.send(message);


                    //for now im just going to have it ping back the message edited
                    clientWriter.write("Returned:" + clientMessage);
                    clientWriter.newLine();
                    clientWriter.flush();






                } else { //Login/Register processing
                    String[] info = clientMessage.split(":");
                    String username = info[1].trim(); //trim removes leading and trailing spaces " "
                    String password = info[2].trim();

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

                        String s = fileReader.readLine();
                        while ((s != null)) {
                            String currentUser = s.substring(0, s.indexOf(","));

                            //if username is and password is taken, is true and break
                            if (currentUser.equals(username)) {
                                clientWriter.write("true");
                                clientWriter.newLine();
                                clientWriter.flush();
                                break;
                            }
                            s = fileReader.readLine();
                        }

                        //if username is unique and now added to list of accounts
                        fileWriter.println(username + "," + password);
                        clientWriter.write("false");
                        clientWriter.newLine();
                        clientWriter.flush();
                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
//Version one for messaging
    /*
    //a method that send to private message to the specific user
    private synchronized void sendToSomeone(String name,String message) {
        String ipAddress = storeInfo.get(name);
        BufferedWriter abc = deliverTo.get(ipAddress);
        if (abc != null) {
            try {
                abc.write(message);
                abc.newLine();
                abc.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    //Echos user message back to the user who sent it
    private synchronized void echoMessage(String name,String message) {
        String ipAddress = storeInfo.get(name);
        BufferedWriter abc = deliverTo.get(ipAddress);
        if (abc != null) {
            try {
                abc.write("From: " + message);
                abc.newLine();
                abc.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //Right now I'm just gonna let the server send message to all users
    private synchronized void sendToAll(String message) {
        for(BufferedWriter out: deliverTo.values()) {
            try {
                out.write(message);
                out.newLine();
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    */

    //Version 2 of sending message
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


}
