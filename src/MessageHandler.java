import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
                 var clientWriter = new BufferedWriter(new OutputStreamWriter(outputStream))) {

                //read info from client
                clientMessage = clientReader.readLine();

                while (clientMessage != null) {
                    System.out.println(clientMessage); //print it for processing purposes

                    if (clientMessage.charAt(0) == 'M') { //incoming message is

                        HashMap<String, MessageHandler> allClients = ClientManager.getDeliverTo(); //HashMap of all the clients in the client manager

                        for (Map.Entry<String, MessageHandler> client : allClients.entrySet()) { //loops through all message handlers
                            MessageHandler clientMessageHandler = client.getValue(); //sets socket and message handler for this iteration
                            Socket socket = clientMessageHandler.getClientSocket();

                            if (socket.isConnected()) { //if this user is connected

                                if (!clientMessageHandler.getClientMessage().equals(clientMessage)) { //check if the client does not have the same message as this messageHandler
                                    clientMessageHandler.send(clientMessage); //if its different then send our message to that client
                                }
                            }
                        }

                        //File handling for conversations

                        List<String> lines = Files.readAllLines(Path.of("Conversations.txt"), StandardCharsets.UTF_8);
                        //Format of clientMessage: M|Sender|Recipient|Message
                        //Format Of Lines: Member1|Member2|Member3<*>Username|Message%&Username|Message%&Username|Message

                        //Separates clientMessage
                        String[] receivedMessage = clientMessage.split("\\|");
                        String sender = receivedMessage[1];
                        String recipients = receivedMessage[2];
                        String message = sender + "|" + receivedMessage[3];
                        ArrayList<String> membersList = new ArrayList<>();
                        boolean writtenToFile = false;

                        //Sets the membersList and sorts it
                        membersList = new ArrayList<>(Arrays.asList(recipients.split(",")));
                        membersList.add(sender);
                        Collections.sort(membersList);

                        for (int i = 0; i < lines.size(); i++) {
                            String conversationLine = lines.get(i);
                            conversationLine.replaceAll("\n", "");
                            ArrayList<String> line = new ArrayList<>(Arrays.asList(conversationLine.split("<*>")));
                            ArrayList<String> members = new ArrayList<>(Arrays.asList(line.get(0).split("\\|")));

                            //Sort Members
                            Collections.sort(members);

                            //displays members and memberslist for testing purposes
                            System.out.println("Members: " + Arrays.toString(members.toArray()));
                            System.out.println("MembersList: " + Arrays.toString(membersList.toArray()));

                            if (members.equals(membersList)) {
                                writtenToFile = true;
                                String newLine = String.format("%s%%&%s\n", conversationLine, message);
                                lines.set(i, newLine);
                                Files.write(Path.of("Conversations.txt"), lines, StandardCharsets.UTF_8);
                                break;
                            }
                        }

                        if (!writtenToFile) {
                            try (var conversationWriter = new PrintWriter(new FileOutputStream("Conversations.txt", true))) {
                                String newLine = Arrays.toString(membersList.toArray())
                                        .replaceAll(", ", "|")
                                        .replaceAll("\\[|\\]", "")
                                        + "<*>" + message;
                                conversationWriter.write(newLine + "\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    } else if (clientMessage.charAt(0) == 'I') {
                        //Read Conversations from File
                        //look for every conversation with the username in it
                        // write them all in one massive line with format explained in Conversations.txt
                        //Format: Member1|Member2|Member3<*>Username|Message&%Username|Message<&*>conversation2<&*>conversation3

                        //TODO: Write to Client Conversations
                        List<String> lines = Files.readAllLines(Path.of("Conversations.txt"), StandardCharsets.UTF_8);
                        //clientMessage split
                        String clientUsername = clientMessage.substring(clientMessage.indexOf("|") + 1);
                        String allConversations = "";
                        System.out.println(clientUsername);
                        for (int i = 0; i < lines.size(); i++) {
                            String conversationLine = lines.get(i);
                            ArrayList<String> line = new ArrayList<>(Arrays.asList(conversationLine.split("<*>")));
                            ArrayList<String> members = new ArrayList<>(Arrays.asList(line.get(0).split("\\|")));

                            if (members.contains(clientUsername)) {
                                allConversations += line + "<&*>";
                            }
                        }

                        if (!allConversations.isEmpty()) {
                            allConversations = allConversations.substring(0, allConversations.length() - 4);
                        }

                        clientWriter.write(allConversations);
                        clientWriter.newLine();
                        clientWriter.flush();

                    } else { //Login/Register processing
                        String[] info = clientMessage.split(":");
                        String username = info[1].strip(); //strip removes leading and trailing spaces
                        String password = info[2].strip();
                        boolean userExists = false;

                        //login
                        if (clientMessage.charAt(0) == 'L') {
                            try (var fileReader = new BufferedReader(new FileReader("Accounts.txt"))) {
                                String line;
                                while ((line = fileReader.readLine()) != null) {
                                    if (line.strip().isEmpty()) {
                                        continue;
                                    }

                                    String currentUser = line.substring(0, line.indexOf(","));
                                    String currentPass = line.substring(line.indexOf(",") + 1);

                                    //once username and password is found, is true and break
                                    if ((currentUser.equals(username)) && (currentPass.equals(password))) {
                                        userExists = true;
                                        clientWriter.write("true\n");
                                        clientWriter.flush();
                                        break;
                                    }
                                }

                                if (!userExists) {
                                    //if username and password aren't found, is false
                                    clientWriter.write("false\n");
                                    clientWriter.flush();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //register
                        else if (clientMessage.charAt(0) == 'R') {
                            try (var fileReader = new BufferedReader(new FileReader("Accounts.txt"));
                                 var fileWriter = new PrintWriter(new FileOutputStream("Accounts.txt", true))) {
                                String line;
                                while ((line = fileReader.readLine()) != null) {
                                    if (line.strip().isEmpty()) {
                                        continue;
                                    }

                                    String currentUser = line.substring(0, line.indexOf(","));

                                    //if username is and password is taken, is true and break
                                    if (currentUser.equals(username)) {
                                        userExists = true;
                                        clientWriter.write("true\n");
                                        clientWriter.flush();
                                        break;
                                    }
                                }

                                if (!userExists) {
                                    //if username is unique and now added to list of accounts
                                    fileWriter.println(username + "," + password);
                                    fileWriter.flush();
                                    clientWriter.write("false\n");
                                    clientWriter.flush();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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

}
