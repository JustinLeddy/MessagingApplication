import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Message Client
 * <p>
 * Deals with most GUI parts of social messaging app.
 * Takes input from user within application and sends to the MessageServer
 * USE PORT 8888 on LOCALHOST
 *
 * @author Alex Frey, Justin Leddy, Maeve Tra, Yifie Mao, Naveena Erranki
 * @version November 30th, 2020
 */
public class MessageClient {
    //Global Fields
    private static String clientMessage;
    private static final String title = "Social Messaging App";
    private static JFrame frame;
    private static String clientUsername;
    private static AtomicBoolean messageSent;

    //Runs actions for login or button based on true (login) or false (register) param
    public static void setClientMessage(boolean loginOrRegister, String username, char[] passwordArray) {
        //Grab username and password
        String password = "";

        //Turns password into string representation
        for (char character : passwordArray) {
            password += character;
        }

        //Checks if fields are empty and displays message if so
        if (username.isEmpty() || password.isEmpty()) {
            message("Please Fill All Fields", JOptionPane.ERROR_MESSAGE);
        } else if (loginOrRegister) {
            //Send info for login
            clientMessage = String.format("L:%s:%s", username, password);
        } else {
            //Send info for register
            clientMessage = String.format("R:%s:%s", username, password);
        }
    }


    //sets client message for sending an 1->1 message
    public static void setClientMessage(String message, String recipient) {
        clientMessage = String.format("M|%s|%s|%s", clientUsername, recipient, message);
    }

    /**
     * Message App Screen for messaging.
     * Will implement screens for seeing messages and reading single/group chats.
     * Will have many functions in side:
     * Adding, Editing, Deleting Messages (Single or Group)
     * Adding, Editing, Deleting Entire Conversations (Single or Group)
     * Edit Own Account
     * Main Screen with all conversations
     * Optional (Recommended) Functions:
     * Notifications
     * Change Group Names and Background
     * Possible (Stretch) Functions:
     * Moderator Role
     */
    public static void messageApp(JPanel panel) {
        //TODO Implement message screen and individual message screen
        // Communications with server to receive conversations, messages, and their functionality

        //Example for Justin

        String[] test = {"T", "E", "S", "T", "M", "E", "S", "S", "A", "G", "E", "T", "E", "S", "T"};

        JList<String> jList = new JList<>(test);

        JScrollPane jScrollPane = new JScrollPane(jList);
        panel.setLayout(new FlowLayout());
        panel.add(jScrollPane);

        //Example for Justin
        //Justin: ty
    }


    //Method to run message application GUI,
    //TODO: Create the actual GUI for this
    public static void runMessageApp() {
        //wipe and repaint
        frame.getContentPane().removeAll();
        frame.repaint();

        //components for messaging
        JButton sendButton = new JButton("Send");
        JTextField messageField = new JTextField(10);
        JTextField userToSendTo = new JTextField(10);
        JLabel recipientLabel = new JLabel("Recipient");

        JPanel panel = new JPanel();

        //action listener
        sendButton.addActionListener(event -> {

            messageSent.set(true);

            if (messageField.getText().isEmpty()) {
                message("Fill All Fields", JOptionPane.ERROR_MESSAGE);
            } else {
                setClientMessage(messageField.getText(), userToSendTo.getText());
            }
        });

        panel.add(messageField);
        panel.add(sendButton);
        panel.add(recipientLabel);
        panel.add(userToSendTo);
        frame.add(panel);


        //Set Frame Size, Settings, and Visibility
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    //Simplifies JOptionPane process
    public static void message(String message, int type) {
        JOptionPane.showMessageDialog(null, message, title, type);
    }

    //Main method to run all screens: Login, Register, messageApp
    public static void main(String[] args) {

        //Declare component fields for login
        JLabel userLbl = new JLabel("Username");
        JLabel passLbl = new JLabel("Password");
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        JTextField userText = new JTextField(5);
        JPasswordField passText = new JPasswordField(5);


        //frame
        frame = new JFrame(title);

        //Other Fields
        AtomicBoolean loginOrRegister = new AtomicBoolean(true);
        AtomicBoolean buttonClicked = new AtomicBoolean(false);
        messageSent = new AtomicBoolean(false);

        //Add Button Functionality, can adapt this to a better action listener method.
        //TODO: Adapt action listeners to a method above to make organization better
        loginBtn.addActionListener(event -> {
            String username = userText.getText();
            char[] password = passText.getPassword();

            loginOrRegister.set(true);

            if (username.isEmpty() || password.length == 0) {
                message("Fill All Fields", JOptionPane.ERROR_MESSAGE);
                clientMessage = "";
            } else {
                setClientMessage(loginOrRegister.get(), username, password);
                buttonClicked.set(true);
            }
        });

        registerBtn.addActionListener(event -> {
            String username = userText.getText();
            char[] password = passText.getPassword();

            loginOrRegister.set(false);

            if (username.isEmpty() || password.length == 0) {
                message("Fill All Fields", JOptionPane.ERROR_MESSAGE);
                clientMessage = "";
            } else {
                setClientMessage(loginOrRegister.get(), username, password);
                buttonClicked.set(true);
            }
        });


        //layout for login screen
        //Creating Login Screen
        //Wipe frame and set new login and register screen
        frame.getContentPane().removeAll();
        frame.repaint();

        JPanel panel = new JPanel(null);

        //Add components to Login Screen
        //Sets location of components on Login Screen
        userLbl.setBounds(10, 10, 80, 25);
        passLbl.setBounds(10, 40, 160, 25);
        loginBtn.setBounds(10, 80, 80, 25);
        registerBtn.setBounds(180, 80, 80, 25);
        userText.setBounds(100, 10, 160, 25);
        passText.setBounds(100, 40, 160, 25);

        //Adds components to panel
        panel.add(userLbl);
        panel.add(passLbl);
        panel.add(loginBtn);
        panel.add(registerBtn);
        panel.add(userText);
        panel.add(passText);

        //Add Panel to Frame
        frame.add(panel);

        //Set Frame Size, Settings, and Visibility
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        while (true) { //infinite loop for server communication
            //only sends the message if a button has been clicked for login screen
            //or if a button has been clicked for messaging
            if (buttonClicked.get() || messageSent.get()) {
                try (var socket = new Socket("localhost", 8888); //change "Localhost" to the server IP when connecting from another device
                     var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     var writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {


                    //Sends message from Client to server
                    if (clientMessage != null && clientMessage.length() > 0) {
                        writer.write(clientMessage);
                        writer.newLine();
                        writer.flush();
                    }

                    if (messageSent.get()) {
                        messageSent.set(false); //reset so it sends once

                        //receive verification from server
                        System.out.println(reader.readLine());
                    }


                    if (buttonClicked.get()) { //we want to communicate about login
                        buttonClicked.set(false);
                        System.out.println(loginOrRegister);
                        //Boolean represents if login credentials exist
                        boolean userExists = Boolean.parseBoolean(reader.readLine());

                        if (userExists) {
                            //If login does exist, check if user is logging in or registering
                            if (loginOrRegister.get()) { //user successfully logged in
                                //Changes window to full message app
                                message("You've successfully logged in!",
                                        JOptionPane.INFORMATION_MESSAGE);
                                clientUsername = userText.getText();
                                runMessageApp();
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
                                //Changes window to full message app
                                message("You've successfully registered!",
                                        JOptionPane.INFORMATION_MESSAGE);
                                clientUsername = userText.getText();
                                runMessageApp();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }

        }
    }
}
