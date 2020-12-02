import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
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
    private static JFrame frame;
    private static String clientUsername;
    private static AtomicBoolean sendMessageClicked;
    private static Socket socket = null;
    private static String ipAddress;
    private static String userName;
    private static BufferedReader reader;
    private static BufferedWriter writer;

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

    //Method to run message application GUI,
    //TODO: Create the actual GUI for this
    public static void runMessageApp() {
        //wipe and repaint
        frame.getContentPane().removeAll();
        frame.repaint();

        //components for messaging
        JButton sendBtn = new JButton("Send");
        JTextField messageText = new JTextField(10);
        JTextField recipientText = new JTextField(10);
        JLabel recipientLbl = new JLabel("Recipient");

        JPanel panel = new JPanel();

        //action listener
        sendBtn.addActionListener(event -> {

            if (messageText.getText().isEmpty()) {
                message("Fill All Fields", JOptionPane.ERROR_MESSAGE);
            } else {
                setClientMessage(messageText.getText(), recipientText.getText());
                sendMessageClicked.set(true);
            }
        });

        panel.add(messageText);
        panel.add(sendBtn);
        panel.add(recipientLbl);
        panel.add(recipientText);
        frame.add(panel);


        //Set Frame Size, Settings, and Visibility
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
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
            e.printStackTrace();
        }
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
        frame = new JFrame(TITLE);

        //Other Fields
        AtomicBoolean loginOrRegister = new AtomicBoolean(true);
        AtomicBoolean loginRegisterClicked = new AtomicBoolean(false);
        sendMessageClicked = new AtomicBoolean(false);

        //Add Button Functionality, can adapt this to a better action listener method.
        loginBtn.addActionListener(event -> {
            String username = userText.getText();
            char[] password = passText.getPassword();

            loginOrRegister.set(true);

            if (username.isEmpty() || password.length == 0) {
                message("Fill All Fields", JOptionPane.ERROR_MESSAGE);
                clientMessage = "";
            } else {
                setClientMessage(loginOrRegister.get(), username, password);
                loginRegisterClicked.set(true);
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
                loginRegisterClicked.set(true);
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
        new MessageClient().connect();


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

            //read messages from server broadcast
            try {
                if (reader.ready()) {
                    System.out.println("Testing");
                    String fromServer = reader.readLine();
                    if (fromServer != null) {
                        System.out.println("Received this from the server: " + fromServer);

                        //TODO: message sorting into chats

                    } else{
                        reader.readLine();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
