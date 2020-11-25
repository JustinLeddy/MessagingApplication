import java.io.*;
import java.net.Socket;

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
 * @author Alex Frey, Justin Leddy, Maeve Tra, Yifie Mao, Naveena Erranki
 * @version November 30th, 2020
 */
public class MessageHandler implements Runnable {
    //Socket to interact with MessageClient and Synchronized field
    private final Socket clientSocket;
    private final Object gateKeeper = new Object();

    //fields
    private File accountList;

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
                 var clientReader = new BufferedReader(new InputStreamReader(inputStream));
                 var clientWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                 var fileReader = new BufferedReader(new FileReader("Accounts.txt"));
                 var fileWriter = new PrintWriter(new FileOutputStream("Accounts.txt", true))) {

                //read in acct info from client
                String clientMessage = clientReader.readLine();
                System.out.println(clientMessage);
                if (clientMessage.charAt(0) == 'M') { //incoming message is a message to the server
                    //format for incoming messages M|SendingUserName|ReceivingUserName|Message
                    String[] messageSplit = clientMessage.split("|");
                    String userReceived = messageSplit[1];
                    String userToSend = messageSplit[2];
                    String message = messageSplit[3];


                } else { //Login/Register processing
                    String credentials = clientMessage;
                    String[] info = credentials.split(":");
                    String username = info[1];
                    String password = info[2];

                    //login
                    if (credentials.charAt(0) == 'L') {
                        String s = fileReader.readLine();
                        while ((s != null)) {
                            String currentUser = s.substring(0, s.indexOf(","));
                            String currentPass = s.substring(s.indexOf(",") + 1);
                            //once username and password is found, is true and break
                            if ((currentUser.equals(username)) && (currentPass.equals(password))) {
                                clientWriter.write("true");
                                clientWriter.newLine();
                                clientWriter.flush();
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
                        //if username is unique and now added to list of accts
                        fileWriter.println(username + "," + password);
                        System.out.println("Wrottoten: " + username + ", " + password + " to file Accounts.txt");
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
    //Account Format: username,password


}
