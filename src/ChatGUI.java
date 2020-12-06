import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ChatGUI extends JFrame {
    private final MessageClient MESSAGE_CLIENT;
    private ArrayList<Conversation> conversations;
    private final String CLIENT_USERNAME;
    private ArrayList<String> usersToSend;
    private Map<String, DisplayMessageGUI> allMessages;


    //All GUI fields
    private JPanel middlePanel;
    private JButton sendButton;
    private JButton newChatButton;
    private JTextArea messageText;
    private JTextArea sentText;
    private JList<String> inboxList;
    private DefaultListModel<String> inboxes;
    private JFrame messageFrame;
    private DisplayMessageGUI messageField;
    private final JLabel DELETE_INSTRUCTION = new JLabel(
            "*Double click on any chat or message to edit/delete");
    private final JLabel EDIT_ACCOUNT = new JLabel("[Edit your account]");

    //Dark mode maybe
    private JToggleButton switchButton;


    public ChatGUI(MessageClient client) {
        this.MESSAGE_CLIENT = client;
        this.CLIENT_USERNAME = client.getClientUsername();
        this.conversations = client.getConversations();
        this.usersToSend = new ArrayList<>();
        this.allMessages = new HashMap<>();
        showMessagePanel();
    }

    //initial setup thingy
    private void showMessagePanel() {
        //initialize variables
        messageFrame = new JFrame(String.format("%s's Messages", CLIENT_USERNAME));
        messageFrame.getContentPane().removeAll();
        messageFrame.repaint();

        messageText = new JTextArea("Type your message here...", 5, 50);
        messageText.setEditable(false); //only enabled in a chat
        messageText.addFocusListener(focusListener);
        sentText = new JTextArea(18, 40); //temp blank area before user choose a chat
        sentText.setEditable(false);

        //fill up inboxes with past conversations
        inboxes = new DefaultListModel<>();
        if (!conversations.isEmpty()) {
            for (Conversation c : this.conversations) {
                createPanel(c); //create a panel with this conversation
            }
        }
        inboxList = new JList<>(inboxes);
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
        DELETE_INSTRUCTION.setFont(new Font("Sans Serif", Font.ITALIC, 10));
        EDIT_ACCOUNT.setFont(new Font("Sans Serif", Font.PLAIN, 10));
        //set underline for label
        Font font = EDIT_ACCOUNT.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        EDIT_ACCOUNT.setFont(font.deriveFont(attributes));
        //set clickable
        EDIT_ACCOUNT.setCursor(new Cursor(Cursor.HAND_CURSOR));
        EDIT_ACCOUNT.addMouseListener(mouseListener);
        middlePanel.add(EDIT_ACCOUNT, "North");
        middlePanel.add(DELETE_INSTRUCTION, "South");
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
        constraints.ipady = 235;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 3;
        userListPanel.add(new JScrollPane(inboxList), constraints);


        userListPanel.setPreferredSize(new Dimension(150, 6));


        //add panel to frame
        messageFrame.add(middlePanel, "Center");
        messageFrame.add(botPanel, "South");
        messageFrame.add(userListPanel, "East");
        messageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        messageFrame.pack();
        messageFrame.setLocationRelativeTo(null);
        messageFrame.setVisible(true);
    }


    private void createPanel(Conversation c) {
        DisplayMessageGUI panelToAdd = new DisplayMessageGUI(c, MESSAGE_CLIENT);
        String label = panelToAdd.getMessageLabel(); //set label (already sort member)
        allMessages.put(label, panelToAdd); //put the panel to map
        inboxes.addElement(label); //add Label to the inboxList
    }

    private void displayMessage(String messageLabel) {
        middlePanel.removeAll();
        messageField = allMessages.get(messageLabel); //find the panel in the map
        usersToSend = messageField.getConversation().getMembers(); //set usersToSend to current member
        usersToSend.remove(CLIENT_USERNAME); //remove sender out of usersToSend
        middlePanel.add(DELETE_INSTRUCTION, "South");
        middlePanel.add(EDIT_ACCOUNT, "North");
        middlePanel.add(messageField, "Center");
        middlePanel.revalidate();
        middlePanel.repaint();
        repaint();
    }


    //delete conversation for recipient
    public void userLeft(Conversation c, String removedUser) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (!removedUser.equals(CLIENT_USERNAME)) { //make sure this is not the sender
                    ArrayList<String> tempMem = c.getMembers();
                    tempMem.add(removedUser); //add it back in to find the panel
                    String label = new DisplayMessageGUI(new Conversation(tempMem), MESSAGE_CLIENT).setMessageLabel();
                    tempMem.remove(removedUser);
                    DisplayMessageGUI oldPanel = allMessages.get(label);
                    if (c.getMembers().size() == 0) { //only one user left
                        oldPanel.notifyUserLeft(removedUser); //just notify, don't need to change label
                    } else {
                        DisplayMessageGUI newPanel = new DisplayMessageGUI(c, MESSAGE_CLIENT);
                        newPanel.notifyUserLeft(removedUser); //display notification
                        String newLabel = newPanel.getMessageLabel();
                        int index = inboxes.indexOf(label);
                        inboxes.setElementAt(newLabel, index); //switch out the label
                        allMessages.remove(label); //remove old panel from map
                        allMessages.put(newLabel, newPanel); //add new panel to map with a new label
                        if (newPanel.getConversation().getMembers().containsAll(messageField.getConversation().getMembers())
                                && messageField.getConversation().getMembers().containsAll(newPanel.getConversation().getMembers())) {
                            //user currently open the chat
                            middlePanel.remove(messageField);
                            messageField = newPanel;
                            middlePanel.add(messageField);
                            middlePanel.revalidate();
                            middlePanel.repaint();
                        }

                    }
                }
            }
        });

    }

    public void updateChat(Conversation c) {
        if (messageField != null
                && messageField.getConversation().getMembers().containsAll(c.getMembers())
                && c.getMembers().containsAll(messageField.getConversation().getMembers())) {
            // if the user is currently in that chat
            messageField.updateMessage(c);
        } else {
            DisplayMessageGUI tempPanel = new DisplayMessageGUI(c, MESSAGE_CLIENT);
            String label = tempPanel.setMessageLabel(); // create a temp label for this conversation
            if (allMessages.containsKey(label)) { //use the label to see if conversation already exist
                allMessages.get(label).updateMessage(c); //update the message, do not need to display
            } else { // if panel for this conversation does not exist (aka create new message)
                createPanel(c); //create a new panel for it
                displayMessage(label);
                //display that new panel (label should match already because memberList is sorted in DisplayMessageGUI)
                inboxList.setSelectedIndex(inboxes.size() - 1); //new chat is selected/highlighted in the list
            }

        }
    }

    public void editChat(Conversation c) {
        DisplayMessageGUI newPanel = new DisplayMessageGUI(c, MESSAGE_CLIENT);
        String label = newPanel.setMessageLabel();
        DisplayMessageGUI oldPanel = allMessages.get(label);
        allMessages.replace(label, oldPanel, newPanel); //replace the old panel with new panel
        if (messageField.getMessageLabel().equals(label)) { //if user currently open this chat
            //System.out.println("Label match");
            middlePanel.remove(messageField);
            messageField = newPanel;
            middlePanel.add(messageField);
            middlePanel.revalidate();
            middlePanel.repaint();
        }
    }

    //ALL PRIVATE METHODS BELOW
    private void setUsersToSend(String userNames, String message) {
        usersToSend = new ArrayList<>();
        userNames = userNames.strip(); //strip leading white spaces
        userNames = userNames.replaceAll(", ", ",");
        if (userNames.isEmpty() || message.isEmpty() || userNames.equals(CLIENT_USERNAME)) { //if the user type in their name only
            JOptionPane.showMessageDialog(null, "You did not enter a valid input",
                    "Social Messaging App", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String[] splitName = userNames.split(",");
        for (String user : splitName) {
            usersToSend.add(user.strip());
        }
        Collections.sort(usersToSend);

        //check for local duplicates
        DisplayMessageGUI tempPanel = new DisplayMessageGUI(new Conversation(usersToSend), MESSAGE_CLIENT);
        String label = tempPanel.setMessageLabel(); // create a temp label for this conversation
        if (allMessages.containsKey(label)) { //use the label to see if conversation already exist
            int answer = JOptionPane.showConfirmDialog(null,
                    "This conversation already exists. Do you want to send this chat to this conversation?",
                    "Social Messaging App", JOptionPane.YES_NO_OPTION); //ask if the user want to add this message to the conversation or not
            if (answer == JOptionPane.YES_OPTION) { // if yes
                MessageClient.setClientMessageMessaging(message, usersToSend); //send message as usual
                MESSAGE_CLIENT.setSendMessageClicked(true); //notify sendButton clicked, this calls the updateCurrentChat
                displayMessage(label); //(for future debug) SEE IF IT UPDATE IN TIME, if not, use Thread.sleep maybe
                return;
            }
        }

        MessageClient.setClientMessageNewChat(usersToSend); // start a new chat
        MESSAGE_CLIENT.setCheckUserAccountsExisting(true); // get the server to check if the recipients are in the system
        MESSAGE_CLIENT.setSendMessageClicked(true);//notify button click

        try { //wait for server to respond
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!MessageClient.getUserAccountsExist()) {
            JOptionPane.showMessageDialog(null, "One or More of the account usernames entered does not exist",
                    "Social Messaging App", JOptionPane.ERROR_MESSAGE);
            MessageClient.setUserAccountsExists(true);
        } else {
            MessageClient.setClientMessageMessaging(message, usersToSend);
            MESSAGE_CLIENT.setSendMessageClicked(true);
        }
    }

    //[GUI method] temp remove conversation for sender and send to user
    private void removeConversation(String label, int index) {
        inboxes.remove(index); //remove from inboxes -> not display anymore
        MessageClient.setClientMessageDeleteUser(allMessages.get(label).getConversation()); //send to client
        MESSAGE_CLIENT.setSendMessageClicked(true); //enter the loop
        DisplayMessageGUI temp = allMessages.get(label);
        //if user is currently open the chat
        if (temp.getConversation().getMembers().containsAll(messageField.getConversation().getMembers())
                && messageField.getConversation().getMembers().containsAll(temp.getConversation().getMembers())) {
            middlePanel.removeAll(); //wipe the chat panel
            messageField = null; //set back to null
            messageText.setEditable(false);//so they can't type any more
            middlePanel.revalidate();
            middlePanel.repaint();
        }
        allMessages.remove(label); //remove from map

    }


    /**
     * GUI Methods
     * All methods related to FocusListener, MouseListener, ActionListener
     */
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == sendButton) { //sendButton is clicked = new message
                String message = messageText.getText();
                //System.out.println(message); //print out message for debugging
                if (message.isEmpty()) { //check if there is message to send
                    JOptionPane.showMessageDialog(null, "There is no message to send!",
                            "Social Messaging App", JOptionPane.ERROR_MESSAGE);
                } else {
                    MessageClient.setClientMessageMessaging(message, usersToSend); //send the message to messageClient
                    MESSAGE_CLIENT.setSendMessageClicked(true); //set to TRUE to notify button click
                    messageText.setText("Type your message here..."); //add the default text again after clicking send
                    messageText.addFocusListener(focusListener); // just for fancy displaying purpose :)

                }
            } else if (e.getSource() == newChatButton) {
                String single = "For Single Conversations type the username";
                String group = "For Group Conversations type the usernames separated by a comma like so: username,username,username";
                messageField = null; //set back to null
                String userNames = JOptionPane
                        .showInputDialog(single + "\n" + group);
                if (userNames == null) { // user choose cancel
                    return;
                }
                String initialMessage = JOptionPane.showInputDialog("Say something first!");
                if (initialMessage == null) { // user choose cancel
                    return;
                }
                setUsersToSend(userNames, initialMessage);

            }
        }
    };


    private FocusListener focusListener = new FocusListener() {
        @Override
        public void focusGained(FocusEvent e) {
            if (messageField == null) {
                return;
            }
            messageText.setText("");
            messageText.removeFocusListener(this);
        }

        @Override
        public void focusLost(FocusEvent e) {
            messageText.setText("Type your message here...");
        }
    };

    private MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() == inboxList) {
                if (e.getClickCount() == 2) { //double click = delete conversation
                    String chatLabel = inboxList.getSelectedValue();
                    int index = inboxList.getSelectedIndex();
                    int choice = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to permanently delete this conversation?",
                            "Delete Conversation", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        removeConversation(chatLabel, index);
                    }
                } else { //just click to open the chat
                    String selectedConversation = inboxList.getSelectedValue();
                    messageText.setEditable(true); //enable text field to start typing
                    displayMessage(selectedConversation);
                }
            } else if (e.getSource() == EDIT_ACCOUNT) {
                String[] options = {"Edit", "Delete", "Cancel"};
                int answer = JOptionPane.showOptionDialog(null,
                        "Do you want to edit or delete your account?",
                        "Edit Account", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, options, options[0]);
                switch (answer) {
                    case 0 -> { //Edit
                        String newPassword = JOptionPane.showInputDialog(null, "Type in your new password:");
                        if (newPassword == null) { //chose cancel
                            return;
                        }
                        MESSAGE_CLIENT.setClientMessageChangePassword(newPassword);
                        MESSAGE_CLIENT.setSendMessageClicked(true);
                        JOptionPane.showMessageDialog(null, "Your changes have been recorded.");

                    }
                    case 1 -> { //delete
                        int choice = JOptionPane.showConfirmDialog(null,
                                "Are you sure you want to permanently delete this account?",
                                "Delete Account", JOptionPane.YES_NO_OPTION);
                        if (choice == JOptionPane.YES_OPTION) {
                            for (Conversation c : conversations) {
                                MESSAGE_CLIENT.setClientMessageDeleteUser(c);
                                MESSAGE_CLIENT.setSendMessageClicked(true);
                            }
                            MESSAGE_CLIENT.setClientMessageDeleteAccount();
                            MESSAGE_CLIENT.setSendMessageClicked(true);
                            messageFrame.dispose(); //close the chat
                        }
                    }
                    default -> {
                        return;
                    }
                }
            }
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
