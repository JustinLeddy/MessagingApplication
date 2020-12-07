import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * LoginGUI
 * <p>
 * The login/register window to take in client's username and password
 * Send client's credentials to MessageClient to process and verify authentication
 * Prompt error message if credentials are invalid and welcoming message if the user is verfied
 *
 * @author Alex Frey, Justin Leddy, Maeve Tra, Yifei Mao, Naveena Erranki
 * @version December 7th, 2020
 */
public class LoginGUI extends JFrame {
    private final MessageClient CLIENT;
    private static final String TITLE = "Social Messaging App";
    private static JFrame frame;

    //Declare component fields for login
    private JLabel userLbl;
    private JLabel passLbl;
    private JButton loginBtn;
    private JButton registerBtn;
    private JTextField userText;
    private JPasswordField passText;

    /**
     * Constructor for LoginGUI
     * Initialize the login frame with all functional buttons and text fields
     *
     * @param client the specific client is logging in
     */
    public LoginGUI(MessageClient client) {
        this.CLIENT = client;
        showLogin();
    }

    /**
     * Simplifies JOptionPane message dialog
     *
     * @param message the message to display
     * @param type    the type of JOptionPane message
     */

    public static void message(String message, int type) {
        JOptionPane.showMessageDialog(null, message, TITLE, type);
    }

    /**
     * Initializes all the components in the login frame
     * Add action listener to the buttons
     */

    private void showLogin() {
        //Declare component fields for login
        userLbl = new JLabel("Username");
        passLbl = new JLabel("Password");
        loginBtn = new JButton("Login");
        registerBtn = new JButton("Register");
        userText = new JTextField(5);
        passText = new JPasswordField(5);
        //Add Button Functionality
        loginBtn.addActionListener(actionListener);
        registerBtn.addActionListener(actionListener);

        //frame
        frame = new JFrame(TITLE);
        frame.getContentPane().removeAll();
        frame.repaint();


        //layout for login screen
        //Creating Login Screen
        //Wipe frame and set new login and register screen


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
    }

    /**
     * Close the login frame once the user is logged in
     */

    public void close() {
        frame.dispose();
    }


    /**
     * Add functional method for the action listerner called by the buttons
     * Once a button is clicked, check if all fields are filled and send the information to MessageClient
     * to validate.
     * <p>
     * Testing (more info in README)
     * - When trying to logged in with non-existed credentials, an error message should pop up
     * - When trying to register with an existed credentials, an error message should pop up
     */
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == loginBtn) {
                String username = userText.getText().strip();
                char[] password = passText.getPassword();

                CLIENT.setLoginOrRegister(true); // true = loginButton clicked

                if (username.isEmpty() || password.length == 0) {
                    message("Fill All Fields", JOptionPane.ERROR_MESSAGE);
                } else {
                    CLIENT.setClientUsername(username);
                    MessageClient.setClientMessageLoginRegister(true, username, password);
                    CLIENT.setLoginRegisterClicked(); //set to true to notify button click
                }
            } else if (e.getSource() == registerBtn) {
                String username = userText.getText();
                char[] password = passText.getPassword();

                CLIENT.setLoginOrRegister(false); // false = registerButton clicked

                if (username.isEmpty() || password.length == 0) {
                    message("Fill All Fields", JOptionPane.ERROR_MESSAGE);
                } else {
                    CLIENT.setClientUsername(username);
                    MessageClient.setClientMessageLoginRegister(false, username, password);
                    CLIENT.setLoginRegisterClicked(); //set to true to notify button click
                }
            }
        }
    };

}