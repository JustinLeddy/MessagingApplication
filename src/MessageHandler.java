import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
 * @version November 30th, 2020
 */
public class MessageHandler implements Runnable {
    //Socket to interact with MessageClient and Synchronized field
    private final Socket clientSocket;
    private final Object gateKeeper = new Object();
    private String userName;
    private String identity;
    private BufferedWriter clientWriter;
    private String clientMessage;
    private String currentClientUsername;

    //fields
    private File accountList;

    //Constructor to initialize clientSocket
    public MessageHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            InputStream inputStream = this.clientSocket.getInputStream();
            OutputStream outputStream = this.clientSocket.getOutputStream();
            BufferedReader clientReader = new BufferedReader(new InputStreamReader(inputStream));
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


                while ((clientMessage = clientReader.readLine()) != null) {
                    System.out.println(clientMessage); //print it for processing purposes


                    if (clientMessage.charAt(0) == 'M') { //incoming message is

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


                        for (int i = 0; i < lines.size(); i++) {
                            String conversationLine = lines.get(i);
                            conversationLine = conversationLine.replaceAll("\n", "");
                            ArrayList<String> line = new ArrayList<>(Arrays.asList(conversationLine.split("<\\*>")));
                            ArrayList<String> members = new ArrayList<>(Arrays.asList(line.get(0).split("\\|")));

                            //Sort Members
                            Collections.sort(members);


                            if (members.equals(membersList)) {
                                writtenToFile = true;
                                String newLine = String.format("%s%%&%s", conversationLine, message);
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

                    } else if (clientMessage.charAt(0) == 'U') {
                        //edit conversation in file
                        //look for correct conversation,
                        String[] clientMessageSplit = clientMessage.split("<\\*>");

                        if (clientMessageSplit.length == 4) { //delete request
                            //Format U<*>currentMember1|currentMember2|currentMember3<*>memberToDelete<*>allMessages
                            //get correct fields
                            String userToRemove = clientMessageSplit[2];
                            String clientConversationMembers = clientMessageSplit[1] + "|" + userToRemove;
                            String allMessages = clientMessageSplit[3];
                            ArrayList<String> membersArray = (ArrayList<String>) Arrays.asList(clientConversationMembers.split("\\|"));
                            //loop through file and find correct row
                            List<String> lines = Files.readAllLines(Path.of("Conversations.txt"), StandardCharsets.UTF_8);
                            String allConversations = "";

                            for (int i = 0; i < lines.size(); i++) {
                                String conversationLine = lines.get(i);
                                ArrayList<String> line = new ArrayList<>(Arrays.asList(conversationLine.split("<\\*>")));
                                ArrayList<String> members = new ArrayList<>(Arrays.asList(line.get(0).split("\\|")));

                                //if the conversation members matches
                                if (members.containsAll(membersArray) && members.size() == membersArray.size()) {
                                    membersArray.remove(userToRemove); //remove the user
                                    if (membersArray.size() > 1) { //if there is still more that two users update the chat, otherwise, remove it.
                                        String updatedConversation = Arrays.toString(new ArrayList[]{membersArray}) //format the
                                                .replaceAll(", ", "|")
                                                .replaceAll("\\[|\\]", "") + "<*>" + allMessages + "\n";
                                        allConversations = "U<*>" + userToRemove + "<*>" + allMessages; //format U<*>userRemoved<*>message<*>
                                        lines.set(i, updatedConversation); //set the correct line
                                        break;
                                    }
                                    lines.remove(i); //if there is only one user remove the array
                                    break;
                                }
                            }

                            //now update the conversations file.
                            Files.write(Path.of("Conversations.txt"), lines, StandardCharsets.UTF_8);

                            //send the conversation update to the other members
                            HashMap<String, MessageHandler> allClients = ClientManager.getDeliverTo();
                            for (Map.Entry<String, MessageHandler> client : allClients.entrySet()) { //loops through all message handlers
                                MessageHandler clientMessageHandler = client.getValue(); //sets socket and message handler for this iteration
                                Socket socket = clientMessageHandler.getClientSocket();

                                if (!socket.isClosed()
                                        && clientMessageHandler.getCurrentClientUsername() != null
                                        && membersArray.contains(clientMessageHandler.getCurrentClientUsername())
                                        && !(currentClientUsername.equals(clientMessageHandler.getCurrentClientUsername()))) { //if this user is connected, and is an intended recipient, and is not the sender
                                    if (allConversations.length() > 1) { //if it still has multiple members
                                        clientMessageHandler.send(clientMessage); //format U<*>memberArray<*>userRemoved<*>messageArray
                                    } else { //send the total initialization to the other client

                                        //clientMessage split
                                        String clientUsername = clientMessageHandler.getCurrentClientUsername();
                                        allConversations = "";
                                        for (int i = 0; i < lines.size(); i++) {
                                            String conversationLine = lines.get(i);
                                            ArrayList<String> line = new ArrayList<>(Arrays.asList(conversationLine.split("<\\*>")));
                                            ArrayList<String> members = new ArrayList<>(Arrays.asList(line.get(0).split("\\|")));

                                            if (members.contains(clientUsername) && membersArray.contains(clientUsername)) {
                                                allConversations += lines.get(i) + "<&*>";
                                            }
                                        }

                                        if (!allConversations.isEmpty()) {
                                            allConversations = allConversations.substring(0, allConversations.length() - 4);
                                        }

                                        clientWriter.write("U<**>" + allConversations);
                                        clientWriter.newLine();
                                        clientWriter.flush();
                                        break;
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
                    } else if (clientMessage.charAt(0) == 'I') {
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
                        if (firstLetter == 'L') {
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
                        else if (firstLetter == 'R') {
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
                        } else if (firstLetter == 'C') {
                            partTwo = partTwo.substring(1, partTwo.length() - 1);
                            try (var fileReader = new BufferedReader(new FileReader("Accounts.txt"))) {
                                List<String> allUsernames = fileReader.lines()
                                        .map(String::strip)
                                        .filter(line -> !line.isEmpty())
                                        .map(line -> line.substring(0, line.indexOf(",")))
                                        .collect(Collectors.toList());
                                boolean usersExist = allUsernames.containsAll(Arrays.asList(partTwo.split(", ")));

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

    public String getCurrentClientUsername() {
        return currentClientUsername;
    }

    public void setCurrentClientUsername(String currentClientUsername) {
        this.currentClientUsername = currentClientUsername;
    }
}
