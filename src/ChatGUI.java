import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ChatGUI extends JFrame {
    private ArrayList<Conversation> conversations = new ArrayList<>();
    private String clientUsername;
    ChatGUI chatGUI;
    MessageClient messageClient;

    JButton sendButton;
    JButton newChatButton;
    JTextArea messageText;
    JTextArea sentText;
    JList<String> inboxList;
    DefaultListModel<String> inboxes;
    JFrame messageFrame;


    public ChatGUI(MessageClient client) {
        this.messageClient = client;
        this.clientUsername = client.getClientUsername();
        this.conversations = client.getConversations();
        showMessagePanel();
    }

    public void showMessagePanel() {
        //initialize variables
        messageFrame = new JFrame(String.format("%s's Message", clientUsername));
        messageFrame.getContentPane().removeAll();
        messageFrame.repaint();
        messageText = new JTextArea("Type your message here...",5,60);
        messageText.addFocusListener(focusListener);
        sentText = new JTextArea(20, 40);
        sentText.setEditable(false); // display messages here
        inboxes = new DefaultListModel<>();
        inboxList = new JList<>(inboxes);
        inboxList.setVisibleRowCount(10);
        // set function for JList
        inboxList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String userToSend = inboxList.getSelectedValue();
            }
        });

        sendButton = new JButton("Send");
        sendButton.addActionListener(actionListener);
        newChatButton = new JButton(("New Chat"));
        newChatButton.addActionListener(actionListener);

        //set up panels

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BorderLayout());
        middlePanel.add(new JScrollPane((sentText)), "Center");

        JPanel botPanel = new JPanel();
        botPanel.setLayout(new FlowLayout());
        botPanel.add(new JScrollPane(messageText));
        botPanel.add(sendButton);

        JPanel userListPanel = new JPanel();
        userListPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        //Setting constraints for New Chat Button
        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.weighty = 0.5;
        constraints.anchor = GridBagConstraints.PAGE_END;
        userListPanel.add(newChatButton, constraints);
        //Setting constraints for "Inboxes" label
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weighty = 0.0; //reset weighty
        constraints.weightx = 0.0;
        userListPanel.add(new JLabel("Inboxes", JLabel.CENTER), constraints);
        //Setting constraints for inboxList
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.ipady = 250;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 3;
        userListPanel.add(new JScrollPane(inboxList), constraints);

        userListPanel.setPreferredSize(new Dimension(150, 5));

        //function for userList
        inboxList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {


                }
            }
        });


        messageFrame.add(middlePanel, "Center");
        messageFrame.add(botPanel, "South");
        messageFrame.add(userListPanel, "East");
        messageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        messageFrame.pack();
        messageFrame.setLocationRelativeTo(null);
        messageFrame.setVisible(true);
    }
    public void run() {

    }

    /**
     * GUI Methods
     * All method related to FocusListener, MouseListener, ActionListener
     */
    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e){
            if (e.getSource() == sendButton) {
                String message = (String) messageText.getText();
                System.out.println(message); //print out message for debugging
                if (message.isEmpty()) {
                    JOptionPane.showMessageDialog(null,"There is no message to send!",
                            "Social Messaging App", JOptionPane.ERROR_MESSAGE);
                } else {
                    MessageClient.setClientMessage(message, new ArrayList<String>(Collections.singletonList("Username")));
                    //TODO: Change "new ArrayList<String>(Collections.singletonList(""))" to arraylist of chat members
                    messageClient.setSendMessageClicked(true);
                    messageText.setText("Type your message here..."); //add the default text again after clicking send
                    messageText.addFocusListener(focusListener);

                }
            } else if (e.getSource() == newChatButton) {
                String userNames = (String) JOptionPane.showInputDialog("Recipient's username in the format [username,username]",
                        JOptionPane.DEFAULT_OPTION);
                setUsersToSend(userNames);
            }
        }
    };


    FocusListener focusListener = new FocusListener() {
        @Override
        public void focusGained(FocusEvent e) {
            messageText.setText("");
            messageText.removeFocusListener(this);
        }

        @Override
        public void focusLost(FocusEvent e) {
            messageText.setText("Type your message here...");
        }
    };

    public void setUsersToSend(String userNames) {
        String[] splitName = userNames.split(",");
        for (String user : splitName) {
            //usersToSend.add(user);
        }
    }
    public static void main(String[] args) {
        new ChatGUI(new MessageClient());
    }
}
