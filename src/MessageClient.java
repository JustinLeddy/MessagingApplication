import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

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
    //Declare components and Network IO fields
    private static JLabel userLbl;
    private static JLabel passLbl;
    private static Button loginBtn;
    private static Button registerBtn;
    private static JTextField userText;
    private static JTextField passText;
    private static JFrame frame;
    private static Socket socket;
    private static OutputStream outputStream;
    private static InputStream inputStream;
    private static BufferedWriter socketWriter;
    private static BufferedReader socketReader;

    //ActionListener for all components in program
    private static ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
            //TODO Implement component ActionListeners
        }
    };

    //Constructor to initialize component and Network IO fields
    public MessageClient() throws IOException {
        //Component
        userLbl = new JLabel("Username");
        passLbl = new JLabel("Password");
        loginBtn = new Button("Login");
        registerBtn = new Button("Sign Up");
        userText = new JTextField(5);
        passText = new JTextField(5);
        frame = new JFrame("Social Messaging App");

        //Network IO
        socket = new Socket("localhost", 5555);
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
        socketWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        socketReader = new BufferedReader(new InputStreamReader(inputStream));
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

        //TODO Implement checking for button click and then connection to server
    }

    /**
     * Register screen for registering. Will redirect back to Login Screen
     */
    public static void register(JPanel panel) {
        //TODO Implement registering panel and communicate
        // to server to check requirements
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
    }

    //Main method to run all screens: Login, Register, messageApp
    public static void main(String[] args) {
        //Login or SignUp
        JPanel panel = new JPanel(null);
        login(panel);
        frame.add(panel);

        //Set Frame Size, Settings, and Visibility
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}
