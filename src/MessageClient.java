import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Message Client
 * <p>
 * Deals with most GUI parts of social messaging app.
 * Takes input from user within application and sends to the MessageServer
 * USE PORT 8888 on LOCALHOST
 *
 * @author Alex Frey, Justin Leddy, Maeve Tra, Yifei Mao, Naveena Erranki
 * @version November 30th, 2020
 */
public class MessageClient {
    //Global Fields
    private static String clientMessage;
    private static final String TITLE = "Social Messaging App";
    private static ChatGUI chatGUI;
    private static String clientUsername;
    private static Socket socket = null;
    private static BufferedReader reader;
    private static BufferedWriter writer;
    private static ArrayList<Conversation> conversations = new ArrayList<>();
    private static AtomicBoolean loginOrRegister = new AtomicBoolean(true);
    private static AtomicBoolean loginRegisterClicked = new AtomicBoolean(false);
    private static AtomicBoolean sendMessageClicked = new AtomicBoolean(false);
    private static AtomicBoolean checkUserAccountsExisting = new AtomicBoolean(false);
    private static AtomicBoolean userAccountsExist = new AtomicBoolean(true);


    //Runs actions for login or button based on true (login) or false (register) param
    public static void setClientMessageLoginRegister(boolean loginOrRegister, String username, char[] passwordArray) {
        //Grab username and password
        String password = "";

        //Turns password into string representation
        for (char character : passwordArray) {
            password += character;
        }

        //Already check for empty in LoginGUI
        if (loginOrRegister) {
            //Send info for login
            clientMessage = String.format("L|%s|%s", username, password);
        } else {
            //Send info for register
            clientMessage = String.format("R|%s|%s", username, password);
        }
    }

    //sets client message for sending a group message
    public static void setClientMessageMessaging(String message, ArrayList<String> members) {
        String recipient = Arrays.toString(members.toArray())
                .replaceAll(", ", ",")
                .replaceAll("\\[|\\]", "");
        clientMessage = String.format("M|%s|%s|%s", clientUsername, recipient, message);
    }

    //Format: C|Recipient1,Recipient2,Recipient3
    public static void setClientMessageNewChat(ArrayList<String> usersToSend) {
        Collections.sort(usersToSend);
        clientMessage = "C|" + Arrays.toString(usersToSend.toArray());
    }

    //setClientMessage update conversation
    /**
     * Method to set the client message to the correct command
     * so the server deletes this user from that conversation
     * Method automatically uses currentClient as the one to delete
     *
     * @param conversation the conversation without the user
     */
    public static void setClientMessageDeleteUser(Conversation conversation) {
        //Format U<*>currentMember1|currentMember2|currentMember3<*>memberToDelete<*>allMessages
        String newMessage = "U<*>";
        newMessage += Arrays.toString(conversation.getMembers().toArray())
                .replaceAll(", ", "|")
                .replaceAll("\\[|\\]", "");
        newMessage += "<*>" + clientUsername + "<*>";
        newMessage += Arrays.toString(conversation.getMessages().toArray())
                .replaceAll(", ", "%&")
                .replaceAll("\\[|\\]", "");
        clientMessage = newMessage;
    }

    /**
     * Formats the updated conversation to send to the server and update there
     *
     * @param conversation the conversation
     */
    public static void setClientMessageUpdateChat(Conversation conversation) {
        //Format U<*>currentMember1|currentMember2|currentMember3<*>allMessages
        String newMessage = "U<*>";
        System.out.println(Arrays.toString(conversation.getMembers().toArray()));
        newMessage += Arrays.toString(conversation.getMembers().toArray())
                .replaceAll(", ", "|")
                .replaceAll("\\[|\\]", "") + "|" + clientUsername;
        newMessage += "<*>" + Arrays.toString(conversation.getMessages().toArray())
                .replaceAll(", ", "%&")
                .replaceAll("\\[|\\]", "");
        clientMessage = newMessage;
    }

    //Simplifies JOptionPane process
    public static void message(String message, int type) {
        JOptionPane.showMessageDialog(null, message, TITLE, type);
    }

    public void connect() {
        try {
            socket = new Socket("localhost", 8888);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("Connected!");
        } catch (IOException e) {
            e.printStackTrace(); //TODO: Replace with detailed error popup
        }
    }

    //Main method to run all screens: Login, Register, messageApp
    public static void main(String[] args) {
        MessageClient messageClient = new MessageClient();
        messageClient.connect(); // use Socket to initialize reader and writer
        LoginGUI login = new LoginGUI(messageClient); //display login page
        /*
         * infinite loop for server communication
         * only sends the message if a button has been clicked for login screen
         * or if a button has been clicked for messaging
         */
        while (true) {
            if (loginRegisterClicked.get() || sendMessageClicked.get()) {
                try {
                    if (sendMessageClicked.get()) {
                        sendMessageClicked.set(false);
                    }

                    //Sends message from Client to server
                    if (clientMessage != null && clientMessage.length() > 0) {
                        writer.write(clientMessage);
                        writer.newLine();
                        writer.flush();
                        System.out.println("Sent to server: " + clientMessage);
                    }

                    if (checkUserAccountsExisting.get()) {
                        checkUserAccountsExisting.set(false);

                        userAccountsExist.set(Boolean.parseBoolean(reader.readLine()));
                    }

                    // Login
                    if (loginRegisterClicked.get()) {
                        loginRegisterClicked.set(false);
                        //Boolean represents if login credentials exist
                        boolean userExists = Boolean.parseBoolean(reader.readLine());
                        if (userExists) {
                            //If login does exist, check if user is logging in or registering
                            if (loginOrRegister.get()) { //user successfully logged in
                                //Changes window to full message app
                                message("You've successfully logged in!",
                                        JOptionPane.INFORMATION_MESSAGE);
                                login.close(); //close the login page
                                //needs to initialize conversations
                                writer.write("I|" + clientUsername + "\n");
                                writer.flush();
                                initializeConversations(reader.readLine());
                                chatGUI = new ChatGUI(messageClient); //open chatGUI
                            } else {
                                //Prompts user that entered username is already taken
                                message("Username is already taken, enter a different username",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }


                        } else {
                            //If login does not exist, check if user is logging in or registering
                            if (loginOrRegister.get()) {
                                //Prompts user that login credentials do not exist
                                message("Account with entered username and password does not exist.",
                                        JOptionPane.INFORMATION_MESSAGE);
                            } else { //user successfully registered
                                message("You've successfully registered!",
                                        JOptionPane.INFORMATION_MESSAGE);
                                login.close(); //close login page
                                chatGUI = new ChatGUI(messageClient); //open chatGUI
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace(); //TODO: Replace with detailed error popup
                    break;
                }
            }


            /* read messages from server broadcast
             * if the message contains the username of this client in the recipient,
             * then look for a conversation to file this message into
             * it looks by gathering and sorting the list of recipients,
             * then it compares it with every member list in all conversations
             * the user is in.
             * If it cant find a conversation with matching members then that means
             * that its a conversation another user created
             * then it creates a new conversation in the current clients list.
             */
            try {
                if (reader.ready()) { //if there is a message from the server
                    String fromServer = reader.readLine(); //read message
                    System.out.println("Received this from the server: {" + fromServer + "}"); //print the message it received from server
                    if (fromServer.startsWith("M|")) {//if it is in the message format
                        //M|Sender|Recipient|Message
                        //M|Sender|Recipient1,Recipient2,Recipient3|Message
                        String[] receivedMessage = fromServer.split("\\|");
                        String sender = receivedMessage[1];
                        String recipients = receivedMessage[2];
                        String message = receivedMessage[3];
                        ArrayList<String> membersList;
                        if (recipients.contains(clientUsername) || sender.equals(clientUsername)) {
                            boolean conversationExists = false;

                            membersList = new ArrayList<>(Arrays.asList(recipients.split(",")));

                            membersList.add(sender);
                            Collections.sort(membersList); //sort membersList

                            for (Conversation conversation : conversations) {
                                ArrayList<String> members = conversation.getMembers();
                                Collections.sort(members);
                                if (members.equals(membersList)) {
                                    conversation.addMessage(String.format("%s|%s", sender, message));
                                    conversationExists = true;
                                    chatGUI.updateChat(conversation);
                                    break;
                                }
                            }

                            if (!conversationExists) { //conversation doesn't exist
                                Conversation conversationToAdd = new Conversation(membersList);
                                conversationToAdd.addMessage(String.format("%s|%s", sender, message));
                                conversations.add(conversationToAdd);
                                chatGUI.updateChat(conversationToAdd);
                            }

                        }
                    }

                    if (fromServer.startsWith("U<*")) {
                        updateConversation(fromServer);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace(); //TODO: Replace with detailed error popup
            }

        }
    }

    private static void updateConversation(String fromServer) {
        if (fromServer.startsWith("U<*>")) {
            if (fromServer.split("<\\*>").length == 4) {
                System.out.println("Members have changed");
                String[] splitServerMessage = fromServer.split("<\\*>");
                String removedUser = splitServerMessage[2];
                String members = splitServerMessage[1] + "|" + removedUser;
                String messages = splitServerMessage[3];
                ArrayList<String> memberArray = (ArrayList<String>) Arrays.asList(members.split("\\|"));
                Collections.sort(memberArray);
                ArrayList<String> messageArray = (ArrayList<String>) Arrays.asList(messages.split("%&"));

                for (Conversation conversation : conversations) {
                    if (conversation.getMembers().equals(memberArray)) {
                        memberArray.remove(removedUser);
                        conversation.setMembers(memberArray);
                        conversation.setMessages(messageArray);
                        break;
                    }
                }

            } else if (fromServer.split("<\\*>").length == 3) {
                System.out.println("Message has changed");
                String[] splitServerMessage = fromServer.split("<\\*>");
                String members = splitServerMessage[1];
                String messages = splitServerMessage[2];
                ArrayList<String> memberArray = new ArrayList<>(Arrays.asList(members.split("\\|")));
                Collections.sort(memberArray);
                ArrayList<String> messageArray = new ArrayList<>(Arrays.asList(messages.split("%&")));

                //search for chat to update
                for (Conversation conversation : conversations) {
                    if (memberArray.containsAll(conversation.getMembers()) && conversation.getMembers().size() == memberArray.size() - 1) {
                        conversation.setMessages(messageArray);
                        chatGUI.editChat(conversation); //supposedly update the list?
                        break;
                    }
                }
            }
        } else {
            System.out.println("Initializing conversations");
            initializeConversations(fromServer.substring(4));
        }
    }


    private static void initializeConversations(String readLine) {
        //Member1,Member2,Member<*>Username|Message,Username|Message
        if (readLine != null && readLine.length() > 2) {
            String[] newConversations = readLine.split("<&\\*>");
            ArrayList<Conversation> updatedConversations = new ArrayList<>();
            for (String conversation : newConversations) { //Member1,Member2,Member3<*>Username|Message%&Username|Message%&Username|Message
                String[] membersAndMessages = conversation.split("<\\*>");
                ArrayList<String> members = new ArrayList<>(Arrays.asList(membersAndMessages[0].split("\\|")));
                ArrayList<String> messages = new ArrayList<>(Arrays.asList(membersAndMessages[1].split("%&")));
                updatedConversations.add(new Conversation(members, messages));
            }
            conversations = updatedConversations;
        } else {
            System.out.println("User has no conversations on record.");
        }
    }
    //Special Characters Message: | &*
    //Special Characters Login: , :  (leading and trailing spaces stripped)


    /*
     * Getters and setters for the fields
     */
    public ArrayList<Conversation> getConversations() {
        return conversations;
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public void setClientUsername(String username) {
        clientUsername = username;
    }

    public String getClientMessage() {
        return clientMessage;
    }

    public void setLoginRegisterClicked() {
        loginRegisterClicked.set(true);
    }

    public void setSendMessageClicked(boolean value) {
        sendMessageClicked.set(value);
    }

    public void setLoginOrRegister(boolean value) {
        MessageClient.loginOrRegister.set(value);
    }

    public void setCheckUserAccountsExisting(boolean value) {
        MessageClient.checkUserAccountsExisting.set(value);
    }

    public boolean getUserAccountsExist() {
        return MessageClient.userAccountsExist.get();
    }

    public void setUserAccountsExists(boolean value) {
        MessageClient.userAccountsExist.set(value);
    }

}


