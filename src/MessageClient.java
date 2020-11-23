import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Message Client
 * <p>
 * Deals with most GUI parts of social messaging app.
 * Takes input from user within application and sends to the MessageServer
 * USE PORT 5555 on LOCALHOST
 *
 * @author Alex Frey, Justin Leddy, Maeve Tra, Yifie Mao, Naveena Erranki
 * @version November 30th, 2020
 */
public class MessageClient {
    //Declare component fields
    private static JLabel userLbl;
    private static JLabel passLbl;
    private static Button loginBtn;
    private static Button registerBtn;
    private static JTextField userText;
    private static JPasswordField passText;
    private static JFrame frame;

    //Declare Network IO fields
    private static BufferedWriter writer;
    private static BufferedReader reader;

    //Other Fields
    private static final String title = "Social Messaging App";

    //ActionListener for all components in program
    private static final ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
            //Format L:username:password
            if (event.getSource() == loginBtn) {
                loginRegisterActions(true);
            }
            //Format R:username:password
            if (event.getSource() == registerBtn) {
                loginRegisterActions(false);
            }
        }
    };

    //Constructor to initialize component and Network IO fields
    public MessageClient() {
        //Component
        userLbl = new JLabel("Username");
        passLbl = new JLabel("Password");
        loginBtn = new Button("Login");
        registerBtn = new Button("Register");
        userText = new JTextField(5);
        passText = new JPasswordField(5);
        frame = new JFrame(title);

        //Network IO
        try {
            Socket socket = new Socket("localhost", 8888);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Login Screen for login or register. Will redirect to message app or to register.
     */
    public static void login(JPanel panel) {
        //Sets location of components on Login Screen
        userLbl.setBounds(10, 10, 80, 25);
        passLbl.setBounds(10, 40, 160, 25);
        loginBtn.setBounds(10, 80, 80, 25);
        registerBtn.setBounds(180, 80, 80, 25);
        userText.setBounds(100, 10, 160, 25);
        passText.setBounds(100, 40, 160, 25);

        //Add actionlisteners
        loginBtn.addActionListener(actionListener);
        registerBtn.addActionListener(actionListener);
        userText.addActionListener(actionListener);
        passText.addActionListener(actionListener);

        //Adds components to panel
        panel.add(userLbl);
        panel.add(passLbl);
        panel.add(loginBtn);
        panel.add(registerBtn);
        panel.add(userText);
        panel.add(passText);
    }

    //Runs actions for login or button based on true (login) or false (register) param
    public static void loginRegisterActions(boolean loginOrRegister) {
        //Grab username and password
        String username = userText.getText();
        char[] passwordArray = passText.getPassword();
        String password = "";
        String userpass;

        //Turns password into string representation
        for (char character : passwordArray) {
            password += character;
        }

        //Checks if fields are empty and displays message if so
        if (username.isEmpty() || password.isEmpty()) {
            message("Please Fill All Fields", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (loginOrRegister) {
            //Send info for login
            userpass = String.format("L:%s:%s", username, password);
        } else {
            //Send info for register
            userpass = String.format("R:%s:%s", username, password);
        }

        try {
            //Writes to server to take username and password
            writer.write(userpass);
            writer.newLine();
            writer.flush();

            //Boolean represents if login credentials exist
            boolean userExists = Boolean.parseBoolean(reader.readLine());

            if (userExists) {
                //If login does exist, check if user is logging in or registering
                if (loginOrRegister) {
                    //Changes window to full message app
                    runMessageApp();
                } else {
                    //Prompts user that entered username is already taken
                    message("Username is already taken, enter a different username",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                //If login does not exist, check if user is logging in or registering
                if (loginOrRegister) {
                    //Prompts user that login credentials do not exist
                    message("Account with entered username and password does not exist.",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    //Changes window to full message app
                    runMessageApp();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Method to run Login and Register screen
    public static void runLoginRegister() {
        //Wipe frame and set new login and register screen
        frame.getContentPane().removeAll();
        frame.repaint();
        JPanel panel = new JPanel(null);
        login(panel);
        frame.add(panel);

        //Set Frame Size, Settings, and Visibility
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
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

        String[] test = {"T", "E", "S", "T","M", "E", "S", "S","A", "G", "E","T", "E", "S", "T"};

        JList<String> jList = new JList<>(test);

        JScrollPane jScrollPane = new JScrollPane(jList);
        panel.setLayout(new FlowLayout());
        panel.add(jScrollPane);

        //Example for Justin
    }

    //Method to run message application screen
    public static void runMessageApp() {
        //Wipe frame and set new message app screen
        frame.getContentPane().removeAll();
        frame.repaint();
        JPanel panel = new JPanel(null);
        messageApp(panel);
        frame.add(panel);

        //Set Frame Size, Settings, and Visibility
        frame.setSize(300, 150); //Up to Change
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

        MessageClient client = new MessageClient();

        runLoginRegister();
    }
}
