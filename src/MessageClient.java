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
 * Format message from the client and sends to the MessageServer
 * Receive message from MessageServer and allocate changes on GUIs
 * USE PORT 8888 on LOCALHOST
 *
 * @author Alex Frey, Justin Leddy, Maeve Tra, Yifei Mao, Naveena Erranki
 * @version December 7th, 2020
 */
public class MessageClient {

    //Global Fields
    private static String clientMessage;
    private static String clientUsername;
    private static final String TITLE = "Social Messaging App";
    private static ChatGUI chatGUI;
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
     * sets the field clientMessage to the correct format to be sent to
     * the server.
     * Format for logging in is L|username|password
     * and for registering is R|username|password
     *
     * @param loginRegister boolean which determines whether to set the message in the login or register format
     * @param username        username of the client
     * @param passwordArray   character array of the password
     */
    public static void setClientMessageLoginRegister(boolean loginRegister, String username, char[] passwordArray) {
        //Grab username and password
        String password = "";

        //Turns password into string representation
        for (char character : passwordArray) {
            password += character;
        }

        //Already check for empty in LoginGUI
        if (loginRegister) {
            //Send info for login
            clientMessage = String.format("L |%s|%s", username, password);
        } else {
            //Send info for register
            clientMessage = String.format("R|%s|%s", username, password);
        }
    }

    //sets client message for sending a group message

    /**
     * Sets the field clientMessage to the correct format for sending a message
     * to a group in the format
     * <p>
     * M|clientUsername|arrayOfMembers|message
     *
     * @param message the message to send to the group
     * @param members the members of the group who are receiving this message
     */
    public static void setClientMessageMessaging(String message, ArrayList<String> members) {
        String recipient = Arrays.toString(members.toArray())
                .replaceAll(", ", ",")
                .replaceAll("[\\[\\]]", "");
        //makes sure that newlines dont cause issues
        clientMessage = String.format("M|%s|%s|%s", clientUsername,
                recipient, message.replaceAll("\n", " "));
    }


    /**
     * sets client message for deleting account
     * in the format D|username
     * README: MAKE SURE TO RUN A LEAVE CONVERSATION ON EVERY CONVERSATION THE USER IS IN BEFORE SENDING THIS MESSAGE
     */
    public static void setClientMessageDeleteAccount() {
        clientMessage = "D|" + clientUsername;
    }

    /**
     * sets client message for changing account password
     * format P|username|newPassword
     *
     * @param newPassword new password for the current client to change their password to
     */
    public static void setClientMessageChangePassword(String newPassword) {
        clientMessage = "P|" + clientUsername + "|" + newPassword;
    }


    /**
     * sets the clientMessage to the correct format for checking if users
     * have a registered account
     * Format: C|Recipient1,Recipient2,Recipient3
     *
     * @param usersToSend a string ArrayList of usernames to check for accounts
     */
    public static void setClientMessageNewChat(ArrayList<String> usersToSend) {
        Collections.sort(usersToSend);
        clientMessage = "C|" + Arrays.toString(usersToSend.toArray())
                .replaceAll("[\\[\\]]", "")
                .replaceAll(", ", ",");
    }

    //setClientMessage update conversation

    /**
     * Method to set the client message to the correct command
     * so the server deletes this user from that conversation
     * Method automatically uses currentClient as the one to delete
     * Format U<*>currentMember1|currentMember2|currentMember3<*>memberToDelete<*>allMessages
     *
     * @param conversation the conversation without the user
     */
    public static void setClientMessageDeleteUser(Conversation conversation) {
        //Format U<*>currentMember1|currentMember2|currentMember3<*>memberToDelete<*>allMessages
        String newMessage = "U<*>";
        newMessage += Arrays.toString(conversation.getMembers().toArray())
                .replaceAll(", ", "|")
                .replaceAll("[\\[\\]]", "");
        newMessage += "<*>" + clientUsername + "<*>";
        for (String s : conversation.getMessages()) {
            newMessage += s + "%&";
        }
        clientMessage = newMessage.substring(0, newMessage.length() - 2);
    }

    /**
     * Formats the updated conversation to send to the server and update there
     * Format U<*>currentMember1|currentMember2|currentMember3<*>allMessages
     *
     * @param conversation the conversation which needs updated
     */
    public static void setClientMessageUpdateChat(Conversation conversation) {

        String newMessage = "U<*>";
        //System.out.println(Arrays.toString(conversation.getMembers().toArray()));
        newMessage += Arrays.toString(conversation.getMembers().toArray())
                .replaceAll(", ", "|")
                .replaceAll("[\\[\\]]", "") + "|" + clientUsername;
        newMessage += "<*>";
        for (String s : conversation.getMessages()) {
            newMessage += s + "%&";
        }
        clientMessage = newMessage.substring(0, newMessage.length() - 2);
    }

    //Simplifies JOptionPane process

    /**
     * method to show a JOptionPane message dialog
     * with a given message and type
     *
     * @param message String message to display in the optionPane
     * @param type    int type which is the type of optionPane to display
     */
    public static void message(String message, int type) {
        JOptionPane.showMessageDialog(null, message, TITLE, type);
    }

    /**
     * method to connect to the server hosted locally.
     * at port 8888.
     * to connect to a remote server change "localhost" to the server's IP address
     */
    public void connect() {
        try {
            socket = new Socket("localhost", 8888);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("Connected!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Main method to run all screens: Login, Register, messageApp

    /**
     * the main method which initializes the client GUI and handles
     * all server communication.
     *
     * @param args String array of arguments
     */
    public static void main(String[] args) {
        MessageClient messageClient = new MessageClient();
        messageClient.connect(); // use Socket to initialize reader and writer
        LoginGUI login = new LoginGUI(messageClient); //display login page
        /*
         * infinite loop for server communication
         * only sends the message if a button has been clicked for login screen
         * or if a button has been clicked for messaging. It sends the message
         * clientMessage which has been initialized by the user through the
         * chatGUI and other methods
         */
        while (true) {
            if (loginRegisterClicked.get() || sendMessageClicked.get()) {
                try {
                    if (sendMessageClicked.get()) {
                        sendMessageClicked.set(false);
                    }

                    //Sends message from Client to server
                    if (clientMessage != null && clientMessage.length() > 0) {
                        //System.out.println("Sent to server: " + clientMessage);
                        writer.write(clientMessage);
                        writer.newLine();
                        writer.flush();
                    }

                    if (checkUserAccountsExisting.get()) {
                        boolean newUserExist = Boolean.parseBoolean(reader.readLine());
                        System.out.println(clientMessage);
                        System.out.println(newUserExist);
                        userAccountsExist.set(newUserExist);
                        checkUserAccountsExisting.set(false);
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
                    e.printStackTrace();
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
                            membersList.remove(clientUsername);
                            Collections.sort(membersList); //sort membersList

                            for (Conversation conversation : conversations) {
                                ArrayList<String> members = conversation.getMembers();
                                members.remove(clientUsername); //remove the client username
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

                    if (fromServer.startsWith("U<*>")) {
                        updateConversation(fromServer);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Method to update conversations when there has been a message change or user deleted
     *
     * @param fromServer the message received from the server
     */
    private static void updateConversation(String fromServer) {

        if (fromServer.split("<\\*>").length == 4) { //if its a member deletion
            //System.out.println("Members have changed");
            String[] splitServerMessage = fromServer.split("<\\*>");
            String removedUser = splitServerMessage[2];
            String members = splitServerMessage[1] + "|" + removedUser;
            ArrayList<String> memberArray = new ArrayList<>(Arrays.asList(members.split("\\|")));
            memberArray.remove(clientUsername); //conversation memberList never includes the current client
            Collections.sort(memberArray); //sort for matching

            for (Conversation conversation : conversations) {
                if (conversation.getMembers().equals(memberArray)) {
                    memberArray.remove(removedUser); //remove the removed user
                    conversation.setMembers(memberArray); //reset the members
                    chatGUI.userLeft(conversation, removedUser); //updates the conversation
                    break;
                }
            }

        } else if (fromServer.split("<\\*>").length == 3) {
            //System.out.println("Message has changed");
            String[] splitServerMessage = fromServer.split("<\\*>");
            String members = splitServerMessage[1];
            String messages = splitServerMessage[2];
            ArrayList<String> memberArray = new ArrayList<>(Arrays.asList(members.split("\\|")));
            memberArray.remove(clientUsername); //conversation memberList never includes the current client
            Collections.sort(memberArray);
            ArrayList<String> messageArray = new ArrayList<>(Arrays.asList(messages.split("%&")));

            //search for chat to update
            for (Conversation conversation : conversations) {
                //System.out.println(Arrays.toString(new ArrayList[]{memberArray}));
                //System.out.println(Arrays.toString(new ArrayList[]{conversation.getMembers()}));
                if (memberArray.equals(conversation.getMembers())) {
                    conversation.setMessages(messageArray);
                    chatGUI.editChat(conversation); //update the list
                    break;
                }
            }
        }
    }


    /**
     * method which takes a message received from the server in the format
     * Member1|Member2|Member3<*>Username|Message&%Username|Message<&*>conversation2<&*>conversation3
     * and initializes the array Conversation[] conversations
     * the method splits the message into an array of conversations by the delimiter <&*>
     * and then splits each conversation by the delimiter <*>
     * and then takes the first entry and splits it into a list of members by the delimiter "|"
     * and adds it to a new conversation. Then splits the second entry into a String array of messages by
     * the delimiter "%&" and adds that array to the new conversation.
     * it then adds all of the conversations to an array of conversations
     * and sets it as the clients Conversation array "conversations"
     *
     * @param readLine the line sent by the server
     */
    private static void initializeConversations(String readLine) {
        //Member1,Member2,Member<*>Username|Message,Username|Message
        if (readLine != null && readLine.length() > 2) {
            String[] newConversations = readLine.split("<&\\*>");
            ArrayList<Conversation> updatedConversations = new ArrayList<>();
            //Member1|Member2|Member3<*>Username|Message%&Username|Message%&Username|Message
            for (String conversation : newConversations) {
                String[] membersAndMessages = conversation.split("<\\*>");
                if (membersAndMessages.length == 3) {
                    ArrayList<String> members = new ArrayList<>(Arrays.asList(membersAndMessages[0].split("\\|")));
                    boolean displayOrNot = Boolean.parseBoolean(membersAndMessages[2]);

                    //displayOrNot true if the first user left,
                    // false if the second, this adds the conversation if they're not the user that left
                    if (clientUsername.equals(members.get(0)) != displayOrNot) {
                        members.remove(clientUsername);
                        ArrayList<String> messages = new ArrayList<>(Arrays.asList(membersAndMessages[1].split("%&")));
                        updatedConversations.add(new Conversation(members, messages));
                    }

                } else {
                    ArrayList<String> members = new ArrayList<>(Arrays.asList(membersAndMessages[0].split("\\|")));
                    members.remove(clientUsername);
                    ArrayList<String> messages = new ArrayList<>(Arrays.asList(membersAndMessages[1].split("%&")));
                    updatedConversations.add(new Conversation(members, messages));
                }
            }
            conversations = updatedConversations;
        } else {
            System.out.println("User has no conversations on record.");
        }
    }
    //Special Characters Message: | &*
    //Special Characters Login: , :  (leading and trailing spaces stripped)

    /**
     * returns the array of conversations
     *
     * @return the field ArrayList<Conversation> conversations
     */
    public ArrayList<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(ArrayList<Conversation> conversations) {
        this.conversations = conversations;
    }

    /**
     * Method to access clientUsername field
     *
     * @return the field clientUsername
     */
    public String getClientUsername() {
        return clientUsername;
    }

    /**
     * method to set the clientUsername field
     *
     * @param username username to set the clientUsername
     */
    public void setClientUsername(String username) {
        clientUsername = username;
    }

    /**
     * method to set the AtomicBoolean loginRegisterClicked to true
     */
    public void setLoginRegisterClicked() {
        loginRegisterClicked.set(true);
    }

    /**
     * method to set the AtomicBoolean sendMessageClicked to a boolean
     * value
     *
     * @param value the boolean value to set AtomicBoolean sendMessageClicked to
     */
    public void setSendMessageClicked(boolean value) {
        sendMessageClicked.set(value);
    }

    /**
     * method to set the AtomicBoolean loginOrRegister to a boolean
     * value
     *
     * @param value the boolean value to set AtomicBoolean loginOrRegister to
     */
    public void setLoginOrRegister(boolean value) {
        MessageClient.loginOrRegister.set(value);
    }

    /**
     * method to set the AtomicBoolean checkUserAccountsExisting to a boolean
     * value
     *
     * @param value the boolean value to set AtomicBoolean checkUserAccountsExisting to
     */
    public void setCheckUserAccountsExisting(boolean value) {
        MessageClient.checkUserAccountsExisting.set(value);
    }

    /**
     * a method to access the AtomicBoolean userAccountsExist
     *
     * @return the boolean value of userAccountsExist
     */
    public static boolean getUserAccountsExist() {
        return userAccountsExist.get();
    }

    /**
     * method to set the AtomicBoolean userAccountsExist to a boolean
     * value
     *
     * @param value the boolean value to set AtomicBoolean userAccountsExist to
     */
    public static void setUserAccountsExists(boolean value) {
        MessageClient.userAccountsExist.set(value);
    }

    /**
     * method to access the String clientMessage
     *
     * @return the string value of clientMessage
     */
    public String getClientMessage() {
        return clientMessage;
    }
}


