import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ChatGUI extends JFrame {
    private ArrayList<Conversation> conversations = new ArrayList<>();
    private String clientUsername;
    private ArrayList<String> usersToSend;
    ChatGUI chatGUI;
    MessageClient messageClient;

    JPanel middlePanel;
    JButton sendButton;
    JButton newChatButton;
    JTextArea messageText;
    JTextArea sentText;
    JList<String> inboxList;
    DefaultListModel<String> inboxes;
    JFrame messageFrame;
    DisplayMessageGUI messageField;


    public ChatGUI(MessageClient client) {
        this.messageClient = client;
        this.clientUsername = client.getClientUsername();
        this.conversations = client.getConversations();
        this.usersToSend = new ArrayList<>();
        showMessagePanel();
    }

    private void showMessagePanel() {
        //initialize variables
        messageFrame = new JFrame(String.format("%s's Messages", clientUsername));
        messageFrame.getContentPane().removeAll();
        messageFrame.repaint();
        messageText = new JTextArea("Type your message here...", 5, 60);
        messageText.setEditable(false); //only enabled in a chat
        messageText.addFocusListener(focusListener);
        sentText = new JTextArea(20, 40);
        sentText.setEditable(false);
        //fill up inboxes with past conversations
        inboxes = new DefaultListModel<>();
        inboxList = new JList<>();
        if (!conversations.isEmpty()) {
            for (Conversation c : this.conversations) {
                setConversationLabel(c);
            }
        }

        inboxList.setVisibleRowCount(10);
        inboxList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inboxList.setLayoutOrientation(JList.VERTICAL);
        // set function for JList
        inboxList.addMouseListener(mouseListener);


        sendButton = new JButton("Send");
        sendButton.addActionListener(actionListener);
        newChatButton = new JButton(("New Chat"));
        newChatButton.addActionListener(actionListener);

        //set up panels

        middlePanel = new JPanel();
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


        //add panel to frame
        messageFrame.add(middlePanel, "Center");
        messageFrame.add(botPanel, "South");
        messageFrame.add(userListPanel, "East");
        messageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        messageFrame.pack();
        messageFrame.setLocationRelativeTo(null);
        messageFrame.setVisible(true);
    }

    public void startNewChat(Conversation c) {
        setConversationLabel(c);
        inboxList.setSelectedIndex(inboxes.getSize());
        addPanel(c);
    }

    private void setConversationLabel(Conversation c) {
        String sendTo = "";
        ArrayList<String> members = c.getMembers();
        for (String m : members) {
            if (!m.equals(clientUsername)) {
                sendTo += " " + m;
            }
        }
        sendTo = sendTo.replaceFirst(" ", "");
        sendTo = sendTo.replaceAll(" ", " \\| ");
        inboxes.addElement(sendTo);
        inboxList.setModel(inboxes);

    }

    //change panel when a chat is selected or create a new chat from the newChatButton
    private void addPanel(Conversation c) {
        messageField = new DisplayMessageGUI(c);
        middlePanel.removeAll();
        middlePanel.add(messageField);
        middlePanel.revalidate();
        middlePanel.repaint();
    }

    public void updateCurrentChat() { //for recipients
        messageField.updateMessage();
    }

    public void updateCurrentChat(String message) { //display to sender
        if (messageField == null) {
            Conversation temp = new Conversation(usersToSend);
            temp.addMessage(message);
            startNewChat(temp);
        } else {
            System.out.println("Message field not null");
            messageField.updateMessage(message);
        }
    }

    // find conversation with the same members
    private void matchConversation(String selectedValue) {
        String[] members = selectedValue.split(" \\| ");
        ArrayList<String> allMembers = new ArrayList<>(Arrays.asList(members));
        //update userToSend
        usersToSend.clear();
        usersToSend.addAll(allMembers);
        allMembers.add(clientUsername); //add sender to the group
        for (Conversation c : conversations) {
            if (c.getMembers().containsAll(allMembers)
                    && allMembers.containsAll(c.getMembers())) { //find the matched conversation
                addPanel(c); //create new panel with that conversation
            }
        }
    }
    public void setUsersToSend(String userNames, String message) {
        usersToSend = new ArrayList<>();
        userNames = userNames.strip();
        if (userNames.isEmpty() || message.isEmpty()) {
            JOptionPane.showMessageDialog(null, "You did not enter a valid input",
                    "Social Messaging App", JOptionPane.ERROR_MESSAGE);
        }
        String[] splitName = userNames.split(",");
        for (String user : splitName) {
            usersToSend.add(user.strip());
        }
        Collections.sort(usersToSend);

        messageClient.setCheckUserAccountsExisting(true);
        MessageClient.setClientMessage(usersToSend);

        if (!messageClient.getUserAccountsExist()) {
            JOptionPane.showMessageDialog(null, "One or More of the account usernames entered does not exist",
                    "Social Messaging App", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MessageClient.setClientMessage(message, usersToSend);
        messageClient.setSendMessageClicked(true);//send ArrayList to MessageClient for processing
    }

    /**
     * GUI Methods
     * All method related to FocusListener, MouseListener, ActionListener
     */
    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == sendButton) {
                String message = messageText.getText();
                System.out.println(message); //print out message for debugging
                if (message.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "There is no message to send!",
                            "Social Messaging App", JOptionPane.ERROR_MESSAGE);
                } else {
                    messageClient.setClientMessage(message, usersToSend);
                    messageClient.setSendMessageClicked(true); //set to TRUE to notify button click
                    messageText.setText("Type your message here..."); //add the default text again after clicking send
                    messageText.addFocusListener(focusListener);

                }
            } else if (e.getSource() == newChatButton) {
                String single = "For Single Conversations type the username";
                String group = "For Group Conversations type the usernames separated by a comma like so: username,username,username";
                messageField = null; //set back to null
                String userNames = JOptionPane
                        .showInputDialog(single +"\n" + group);
                String initialMessage = JOptionPane.showInputDialog("Say something first!");
                setUsersToSend(userNames, initialMessage);
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

    MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            String selectedConversation = inboxList.getSelectedValue();
            messageText.setEditable(true); //enable text field
            matchConversation(selectedConversation);
        }
    };

    //for debugging
    private void printList() {
        for (int i = 0; i < inboxes.getSize(); i++) {
            System.out.println(inboxes.get(i));
        }
    }

    public static void main(String[] args) {
        new ChatGUI(new MessageClient());
    }
}
