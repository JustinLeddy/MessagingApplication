import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;

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
 * @version December 7th, 2020
 */

public class MessageHandler implements Runnable {
    //Socket to interact with MessageClient and Synchronized field
    private final Socket CLIENT_SOCKET;
    //other fields
    private final Object GATE_KEEPER = new Object();
    private BufferedWriter clientWriter;
    private String clientMessage;
    private String currentClientUsername;
    private File accountList;

    /**
     * Constructor to initialize clientSocket, this initializes
     * the inputStream, outputStream, clientReader, and clientWriter
     * using the given clientSocket
     *
     * @param clientSocket socket of the connected client
     */
    public MessageHandler(Socket clientSocket) {
        this.CLIENT_SOCKET = clientSocket;
        try {
            InputStream inputStream = this.CLIENT_SOCKET.getInputStream();
            OutputStream outputStream = this.CLIENT_SOCKET.getOutputStream();
            BufferedReader clientReader = new BufferedReader(new InputStreamReader(inputStream));
            clientWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* List of all client -> messageHandler formats:
     * loginRegister: L|username|password, R|username|password
     * sendMessage: M|clientUsername|arrayOfMembers|message
     * Update message history in chat: U<*>currentMember1|currentMember2|currentMember3<*>allMessages
     * Delete user from chat: U<*>currentMember1|currentMember2|currentMember3<*>memberToDelete<*>allMessages
     * delete account: D|username
     * change password: P|username|newPassword
     * check if users exist: C|user1,user2,user3
     *
     * List of all messageHandler -> client formats:
     * initialize conversation array:
     * Member1|Member2|Member3<*>Username|Message&%Username|Message<&*>conversation2<&*>conversation3
     * loginOrRegister:
     * login: true if logged in, false if not, Register: true if the account exists, false if register success
     * Check if user exists: true if they all exists, false if there is one thats not.
     */

    /**
     * Run method which uses clientSocket to interact with MessageClient
     *
     * The run method for message handler which is run whenever a new thread is started.
     * This method handles all communication with the client it is initialized with
     * at the socket clientSocket. The functions of this method include
     * - sending messages to other message handlers
     * - sending messages to this client
     * - determine if a username or password is valid
     * - add a new account to the account array
     * - send all conversations that this client is a member of to this client
     * - delete this clients account
     * - change this clients password
     * - edit and delete messages in conversations this client is a member of
     * The way it does this is through the clientMessage that the MessageClient sends
     * to the MessageHandler
     * Depending on the first character and contents of this message, it will do
     * one of the actions listed above.
     *
     * Testing:
     * This method was tested using the debugging window and console printing to verify that inputs
     * and its various outputs sent the correct Strings. For example, to test if the message was being
     * sent to each user correctly I added breakpoints to the array and watched the message match each client
     * and call their send method. I then had the client print the received message to the console so I could see
     * that it was indeed being sent to the correct clients. I then had a breakpoint at the line where it updates
     * the conversation array to verify that the message was added to the correct conversation
     * in the correct format and order.
     */
    @Override
    public void run() {

        synchronized (GATE_KEEPER) {

            //try with resources, being the input and output streams, readers, and writers.
            try (var inputStream = this.CLIENT_SOCKET.getInputStream();
                 var outputStream = this.CLIENT_SOCKET.getOutputStream();
                 var clientReader = new BufferedReader(new InputStreamReader(inputStream));
                 var clientWriter = new BufferedWriter(new OutputStreamWriter(outputStream))) {


                while ((clientMessage = clientReader.readLine()) != null) {
                    System.out.println("Received from client: " + clientMessage); //print it for processing purposes


                    if (clientMessage.charAt(0) == 'M') { //its a message to send to other users

                        //Separates clientMessage
                        String[] receivedMessage = clientMessage.split("\\|");
                        String sender = receivedMessage[1];
                        String recipients = receivedMessage[2];
                        String message = sender + "|" + receivedMessage[3];
                        ArrayList<String> membersList;
                        boolean writtenToFile = false;

                        //Sets the membersList and sorts it
                        membersList = new ArrayList<>(Arrays.asList(recipients.split(",")));
                        membersList.add(sender);
                        Collections.sort(membersList);
                        HashMap<String, MessageHandler> allClients = ClientManager.getDeliverTo(); //HashMap of all the clients in the client manager

                        //sends message to appropriate users
                        for (Map.Entry<String, MessageHandler> client : allClients.entrySet()) { //loops through all message handlers
                            MessageHandler clientMessageHandler = client.getValue(); //sets socket and message handler for this iteration
                            Socket socket = clientMessageHandler.getClientSocket();

                            if (!socket.isClosed()
                                    && clientMessageHandler.getCurrentClientUsername() != null
                                    && membersList.contains(clientMessageHandler.getCurrentClientUsername())) { //if this user is connected, and is an intended recipient
                                clientMessageHandler.send(clientMessage);

                            }
                        }

                        //File handling for conversations

                        List<String> lines = Files.readAllLines(Path.of("Conversations.txt"), StandardCharsets.UTF_8);
                        //Format of clientMessage: M|Sender|Recipient|Message
                        //Format Of Lines: Member1|Member2|Member3<*>Username|Message%&Username|Message%&Username|Message

                        //loops through every conversation
                        for (int i = 0; i < lines.size(); i++) {
                            String conversationLine = lines.get(i);
                            conversationLine = conversationLine.replaceAll("\n", "");
                            ArrayList<String> line = new ArrayList<>(Arrays.asList(conversationLine.split("<\\*>")));
                            ArrayList<String> members = new ArrayList<>(Arrays.asList(line.get(0).split("\\|")));

                            //Sort Members
                            Collections.sort(members);

                            //if its a conversation where a member left but wants to join again,
                            //remove the last entry and add their message
                            if (line.size() == 3) {
                                //remove that ending, so it will start displaying to both, hopefully
                                conversationLine = conversationLine.substring(0, conversationLine.lastIndexOf("<*>"));
                            }

                            if (members.equals(membersList)) {
                                writtenToFile = true;
                                String newLine = String.format("%s%%&%s", conversationLine, message);
                                lines.set(i, newLine);
                                Files.write(Path.of("Conversations.txt"), lines, StandardCharsets.UTF_8);
                                break;
                            }
                        }

                        if (!writtenToFile) { //if the conversation doesn't exist
                            try (var conversationWriter = new PrintWriter(new FileOutputStream("Conversations.txt", true))) {
                                String newLine = Arrays.toString(membersList.toArray())
                                        .replaceAll(", ", "|")
                                        .replaceAll("[\\[\\]]", "")
                                        + "<*>" + message;
                                conversationWriter.write(newLine + "\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    } else if (clientMessage.charAt(0) == 'D') { //delete account
                        List<String> accountLines = Files.readAllLines(Path.of("Accounts.txt"), StandardCharsets.UTF_8);
                        //Format of clientMessage: M|Sender|Recipient|Message
                        //Format Of Lines: Member1|Member2|Member3<*>Username|Message%&Username|Message%&Username|Message

                        //loops through every conversation
                        for (int i = 0; i < accountLines.size(); i++) {
                            String[] lineSplit = accountLines.get(i).split(",");
                            String username = lineSplit[0];
                            if (username.equalsIgnoreCase(currentClientUsername)) {
                                accountLines.set(i, currentClientUsername + ","); //sets the password blank so the account cant be accessed, essentially "deleted"
                                break;
                            }
                        }

                        //update accounts
                        Files.write(Path.of("Accounts.txt"), accountLines, StandardCharsets.UTF_8);


                    } else if (clientMessage.charAt(0) == 'P') { //change password

                        String newPassword = clientMessage.split("\\|")[2];
                        List<String> lines = Files.readAllLines(Path.of("Accounts.txt"), StandardCharsets.UTF_8);
                        //Format of clientMessage: M|Sender|Recipient|Message
                        //Format Of Lines: Member1|Member2|Member3<*>Username|Message%&Username|Message%&Username|Message

                        //loops through every conversation
                        for (int i = 0; i < lines.size(); i++) {
                            String[] lineSplit = lines.get(i).split(",");
                            String username = lineSplit[0];
                            if (username.equalsIgnoreCase(currentClientUsername)) {
                                lines.set(i, username + "," + newPassword);
                                break;
                            }
                        }

                        //update accounts
                        Files.write(Path.of("Accounts.txt"), lines, StandardCharsets.UTF_8);

                    } else if (clientMessage.charAt(0) == 'U') { //update conversation members or messages
                        //edit conversation in file
                        //look for correct conversation,
                        String[] clientMessageSplit = clientMessage.split("<\\*>");

                        if (clientMessageSplit.length > 3) { //delete request
                            //Format U<*>currentMember1|currentMember2|currentMember3<*>memberToDelete<*>allMessages and the optional <*>trueOrFalse
                            //get correct fields
                            String userToRemove = clientMessageSplit[2];
                            String clientConversationMembers = clientMessageSplit[1] + "|" + userToRemove;
                            String allMessages = clientMessageSplit[3];
                            ArrayList<String> membersArray = new ArrayList<>(Arrays.asList(clientConversationMembers.split("\\|")));
                            //loop through file and find correct row
                            List<String> lines = Files.readAllLines(Path.of("Conversations.txt"), StandardCharsets.UTF_8);
                            String allConversations = "";
                            boolean conversationToUpdate = true;
                            for (int i = 0; i < lines.size(); i++) {
                                String conversationLine = lines.get(i);
                                ArrayList<String> line = new ArrayList<>(Arrays.asList(conversationLine.split("<\\*>")));
                                ArrayList<String> members = new ArrayList<>(Arrays.asList(line.get(0).split("\\|")));

                                if (clientMessageSplit.length == 5) { //if there is only one user left and they tossed a delete, then remove the array
                                    lines.remove(i);
                                    conversationToUpdate = false;
                                    break;
                                }

                                //if the conversation members matches
                                if (members.containsAll(membersArray) && members.size() == membersArray.size()) {
                                    membersArray.remove(userToRemove); //remove the user
                                    if (membersArray.size() > 1) { //if there is still more than two
                                        String updatedConversation = Arrays.toString(new ArrayList[]{membersArray}) //format the
                                                .replaceAll(", ", "|")
                                                .replaceAll("[\\[\\]]", "") + "<*>" + allMessages + "<*>System|" + userToRemove + " has left the chat.";
                                        //keep record of user leaving chat to display later
                                        allConversations = "U<*>" + userToRemove + "<*>" + allMessages; //format U<*>userRemoved<*>message<*>
                                        lines.set(i, updatedConversation); //set the correct line
                                        break;
                                    } else {
                                        //theres two members in this chat, then append true if the first user is removed, false if the second user is removed
                                        String updatedConversation = lines.get(i) + "<*>"; //format U<*>userRemoved<*>message<*>
                                        if (members.get(0).equals(userToRemove)) {
                                            updatedConversation += "true";
                                        } else {
                                            updatedConversation += "false";
                                        }
                                        lines.set(i, updatedConversation); //set the correct line
                                        break;
                                    }
                                }
                            }

                            //now update the conversations file.
                            Files.write(Path.of("Conversations.txt"), lines, StandardCharsets.UTF_8);

                            //send the conversation update to the other members
                            HashMap<String, MessageHandler> allClients = ClientManager.getDeliverTo();
                            clientConversationMembers = clientConversationMembers.substring(0, clientConversationMembers.lastIndexOf("|"));
                            for (Map.Entry<String, MessageHandler> client : allClients.entrySet()) { //loops through all message handlers
                                MessageHandler clientMessageHandler = client.getValue(); //sets socket and message handler for this iteration
                                Socket socket = clientMessageHandler.getClientSocket();

                                if (!socket.isClosed()
                                        && clientMessageHandler.getCurrentClientUsername() != null
                                        && membersArray.contains(clientMessageHandler.getCurrentClientUsername())
                                        && !(currentClientUsername.equals(clientMessageHandler.getCurrentClientUsername()))) { //if this user is connected, and is an intended recipient, and is not the sender

                                    if (conversationToUpdate) {
                                        clientMessageHandler.send(clientMessage);
                                    }
                                }
                            }


                        } else if (clientMessageSplit.length == 3) { //change messages request
                            //Format U<*>currentMember1|currentMember2|currentMember3<*>allMessages
                            String updateChatMembers = clientMessageSplit[1];
                            String messages = clientMessageSplit[2];
                            List<String> membersArray = Arrays.asList(updateChatMembers.split("\\|"));
                            List<String> lines = Files.readAllLines(Path.of("Conversations.txt"), StandardCharsets.UTF_8);

                            for (int i = 0; i < lines.size(); i++) {
                                String conversationLine = lines.get(i);
                                ArrayList<String> line = new ArrayList<>(Arrays.asList(conversationLine.split("<\\*>")));
                                ArrayList<String> members = new ArrayList<>(Arrays.asList(line.get(0).split("\\|")));

                                //if the conversation members matches
                                if (members.containsAll(membersArray) && members.size() == membersArray.size()) {
                                    lines.set(i, line.get(0) + "<*>" + messages);
                                    break;
                                }
                            }

                            //rewrite conversations file
                            Files.write(Path.of("Conversations.txt"), lines, StandardCharsets.UTF_8);

                            //send update conversation to
                            HashMap<String, MessageHandler> allClients = ClientManager.getDeliverTo();

                            for (Map.Entry<String, MessageHandler> client : allClients.entrySet()) { //loops through all message handlers
                                MessageHandler clientMessageHandler = client.getValue(); //sets socket and message handler for this iteration
                                Socket socket = clientMessageHandler.getClientSocket();

                                if (!socket.isClosed()
                                        && clientMessageHandler.getCurrentClientUsername() != null
                                        && membersArray.contains(clientMessageHandler.getCurrentClientUsername())
                                        && !(currentClientUsername.equals(clientMessageHandler.getCurrentClientUsername()))) { //if this user is connected, and is an intended recipient, and is not the sender

                                    clientMessageHandler.send(clientMessage); //format U<*>memberArray<*>newMessageArray

                                }
                            }
                        }
                    } else if (clientMessage.charAt(0) == 'I') { //initialize conversations this client is in
                        //Read Conversations from File
                        //look for every conversation with the username in it
                        // write them all in one massive line with format explained in Conversations.txt
                        //Format: Member1|Member2|Member3<*>Username|Message&%Username|Message<&*>conversation2<&*>conversation3

                        List<String> lines = Files.readAllLines(Path.of("Conversations.txt"), StandardCharsets.UTF_8);

                        //clientMessage split
                        String clientUsername = clientMessage.substring(clientMessage.indexOf("|") + 1);
                        String allConversations = "";

                        for (int i = 0; i < lines.size(); i++) {
                            String conversationLine = lines.get(i);
                            ArrayList<String> line = new ArrayList<>(Arrays.asList(conversationLine.split("<\\*>")));
                            ArrayList<String> members = new ArrayList<>(Arrays.asList(line.get(0).split("\\|")));

                            if (members.contains(clientUsername)) {
                                allConversations += lines.get(i) + "<&*>";
                            }
                        }

                        if (!allConversations.isEmpty()) {
                            allConversations = allConversations.substring(0, allConversations.length() - 4);
                        }

                        clientWriter.write(allConversations);
                        clientWriter.newLine();
                        clientWriter.flush();

                    } else { //Login/Register processing

                        char firstLetter = clientMessage.charAt(0);
                        String[] info = clientMessage.split("\\|");
                        String partTwo = info[1].strip();
                        String username;
                        String password;

                        boolean userExists = false;

                        //login
                        if (firstLetter == 'L') { //login to a new account
                            username = partTwo; //strip removes leading and trailing spaces
                            password = info[2].strip();

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
                                        currentClientUsername = username;
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
                        else if (firstLetter == 'R') { //register a new account
                            username = partTwo; //strip removes leading and trailing spaces
                            password = info[2].strip();

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
                                    currentClientUsername = username;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (firstLetter == 'C') { //check user exist
                            try (var fileReader = new BufferedReader(new FileReader("Accounts.txt"))) {
                                List<String> allUsernames = fileReader.lines()
                                        .map(String::strip)
                                        .filter(line -> !line.isEmpty())
                                        .map(line -> line.substring(0, line.indexOf(",")))
                                        .collect(Collectors.toList());
                                boolean usersExist = allUsernames.containsAll(Arrays.asList(partTwo.split(",")));
                                System.out.println(userExists);
                                clientWriter.write(usersExist + "\n");
                                clientWriter.flush();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            } catch (IOException e) {
                System.out.println("Client " + currentClientUsername + " disconnected");
            }
        }
    }

    //Sending message

    /**
     * sends a message to the client
     * is useful for sending messages from other MessageHandlers
     *
     * @param str String message to send
     */
    public void send(String str) {
        try {
            clientWriter.write(str);
            clientWriter.newLine();
            clientWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //get socket

    /**
     * method to access this clients socket
     *
     * @return this clients socket
     */
    public Socket getClientSocket() {
        return CLIENT_SOCKET;
    }

    /**
     * method to access the current username of the connected client
     *
     * @return String currentClientUsername
     */
    public String getCurrentClientUsername() {
        return currentClientUsername;
    }

    /**
     * method to set the field currentClientUsername
     *
     * @param currentClientUsername the username to set currentClientUsername to
     */
    public void setCurrentClientUsername(String currentClientUsername) {
        this.currentClientUsername = currentClientUsername;
    }
}
