import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoginGUI extends JFrame {
    private final MessageClient client;
    private static final String TITLE = "Social Messaging App";
    private static JFrame frame;

    //Declare component fields for login
    JLabel userLbl;
    JLabel passLbl;
    JButton loginBtn;
    JButton registerBtn;
    JTextField userText;
    JPasswordField passText;

    public LoginGUI(MessageClient client) {
        this.client = client;
        showLogin();
    }

    public static void message(String message, int type) {
        JOptionPane.showMessageDialog(null, message, TITLE, type);
    }

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

    public void close() {
        frame.dispose();
    }




    // Declare actionListener for specific functionality
    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == loginBtn) {
                String username = userText.getText().strip();
                char[] password = passText.getPassword();

                client.setLoginOrRegister(true); // true = loginButton clicked

                if (username.isEmpty() || password.length == 0) {
                    message("Fill All Fields", JOptionPane.ERROR_MESSAGE);
                } else {
                    client.setClientUsername(username);
                    MessageClient.setClientMessage(true, username, password);
                    client.setLoginRegisterClicked(); //set to true to notify button click
                }
            } else if (e.getSource() == registerBtn) {
                String username = userText.getText();
                char[] password = passText.getPassword();

                client.setLoginOrRegister(false); // false = registerButton clicked

                if (username.isEmpty() || password.length == 0) {
                    message("Fill All Fields", JOptionPane.ERROR_MESSAGE);
                } else {
                    client.setClientUsername(username);
                    MessageClient.setClientMessage(false, username, password);
                    client.setLoginRegisterClicked(); //set to true to notify button click
                }
            }
        }
    };

}