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
    //Other Fields
    private static String clientMessage;
    private static final String title = "Social Messaging App";

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
    }

    /*
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
    */
    //Simplifies JOptionPane process
    public static void message(String message, int type) {
        JOptionPane.showMessageDialog(null, message, title, type);
    }

    //Main method to run all screens: Login, Register, messageApp
    public static void main(String[] args) {

        //Declare component fields
        JLabel userLbl = new JLabel("Username");
        JLabel passLbl = new JLabel("Password");
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        JTextField userText = new JTextField(5);
        JPasswordField passText = new JPasswordField(5);
        JFrame frame = new JFrame(title);

        //Other Field
        AtomicBoolean loginOrRegister = new AtomicBoolean(true);
        AtomicBoolean buttonClicked = new AtomicBoolean(false);

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

        //Add Futton Bunctionality
        loginBtn.addActionListener(event -> {
            String username = userText.getText();
            char[] password = passText.getPassword();

            loginOrRegister.set(true);
            buttonClicked.set(true);

            if (username.isEmpty() || password.length == 0) {
                message("Fill All Fields", JOptionPane.ERROR_MESSAGE);
            } else {
                setClientMessage(loginOrRegister.get(), username, password);
            }
        });
        registerBtn.addActionListener(event -> {
            String username = userText.getText();
            char[] password = passText.getPassword();

            loginOrRegister.set(false);
            buttonClicked.set(true);

            if (username.isEmpty() || password.length == 0) {
                message("Fill All Fields", JOptionPane.ERROR_MESSAGE);
            } else {
                setClientMessage(loginOrRegister.get(), username, password);
            }
        });

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

        while (true) {
            if (buttonClicked.get()) {
                buttonClicked.set(false);
                try (var socket = new Socket("localhost", 8888);
                     var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     var writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                    //write / read from server


                    //Writes to server to take username and password
                    writer.write(clientMessage);
                    writer.newLine();
                    writer.flush();

                    //Boolean represents if login credentials exist
                    boolean userExists = Boolean.parseBoolean(reader.readLine());

                    if (userExists) {
                        //If login does exist, check if user is logging in or registering
                        if (loginOrRegister.get()) {
                            //Changes window to full message app
                            message("You've successfully logged in!",
                                    JOptionPane.INFORMATION_MESSAGE);
                            break;
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
                        } else {
                            //Changes window to full message app
                            message("You've successfully registered!",
                                    JOptionPane.INFORMATION_MESSAGE);
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        //TODO Message App
    }
}
