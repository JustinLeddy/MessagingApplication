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

                //read in acct info from client
                String credentials = reader.readLine();
                String[] info = credentials.split(":");
                String username = info[1];
                String password = info[2];

                //to write to a file
                try {
                    File f = new File("Accounts.txt");
                    BufferedReader bfr = new BufferedReader(new FileReader(f));
                    PrintWriter pw = new PrintWriter(new FileOutputStream(f, true));

                    //login
                    if (credentials.charAt(0) == 'L')
                    {
                        String s = bfr.readLine();
                        while ((s != null)) {
                            String currentUser = s.substring(0, s.indexOf(","));
                            String currentPass = s.substring(s.indexOf(",") + 1);
                            //once username and password is found, is true and break
                            if ((currentUser.equals(username)) && (currentPass.equals(password)))
                            {
                                writer.write("true");
                                writer.newLine();
                                writer.flush();
                                break;
                            }
                            s = bfr.readLine();
                        }
                        //if username and password aren't found, is false
                        writer.write("false");
                        writer.newLine();
                        writer.flush();
                    }
                    //register
                    else
                    {
                        String s = bfr.readLine();
                        while ((s != null)) {
                            String currentUser = s.substring(0, s.indexOf(","));
                            //if username is and password is taken, is true and break
                            if (currentUser.equals(username))
                            {
                                writer.write("true");
                                writer.newLine();
                                writer.flush();
                                break;
                            }
                            s = bfr.readLine();
                        }
                        //if username is unique and now added to list of accts
                        pw.println(username + "," + password);
                        writer.write("false");
                        writer.newLine();
                        writer.flush();
                    }

                } catch (IOException e)
                {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //Account Format: username,password


}
