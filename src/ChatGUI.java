import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ChatGUI extends JFrame {
    private final MessageClient messageClient;
    private ArrayList<Conversation> conversations;
    private final String clientUsername;
    private ArrayList<String> usersToSend;
    private Map<String, DisplayMessageGUI> allMessages;


    //All GUI fields
    JPanel middlePanel;
    JButton sendButton;
    JButton newChatButton;
    JTextArea messageText;
    JList<String> inboxList;
    DefaultListModel<String> inboxes;
    JFrame messageFrame;
    DisplayMessageGUI messageField;
    private final JLabel deleteInstruction = new JLabel(
            "*Double click on any chat or message to edit/delete");


    public ChatGUI(MessageClient client) {
        this.messageClient = client;
        this.clientUsername = client.getClientUsername();
        this.conversations = client.getConversations();
        this.usersToSend = new ArrayList<>();
        this.allMessages = new HashMap<>();
        showMessagePanel();
    }

    private void showMessagePanel() {
        //initialize variables
        messageFrame = new JFrame(String.format("%s's Messages", clientUsername));
        messageFrame.getContentPane().removeAll();
        messageFrame.repaint();

        messageText = new JTextArea("Type your message here...", 5, 50);
        messageText.setEditable(false); //only enabled in a chat
        messageText.addFocusListener(focusListener);
        JTextArea sentText = new JTextArea(18, 40); //temp blank area before user choose a chat
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
        deleteInstruction.setFont(new Font("Sans Serif", Font.ITALIC, 10));
        middlePanel.add(deleteInstruction, "South");
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
        DisplayMessageGUI panelToAdd = new DisplayMessageGUI(c, messageClient);
        String label = panelToAdd.getMessageLabel();
        allMessages.put(label, panelToAdd); //put the panel to map
        inboxes.addElement(label); //add Label to the inboxList
    }

    private void displayMessage(String messageLabel) {
        middlePanel.removeAll();
        messageField = allMessages.get(messageLabel); //find the panel in the map
        usersToSend = messageField.getConversation().getMembers(); //set usersToSend to current member
        usersToSend.remove(clientUsername);
        middlePanel.add(deleteInstruction, "South");
        middlePanel.add(messageField, "Center");
        middlePanel.revalidate();
        middlePanel.repaint();
        repaint();
    }




    /*private void setConversationLabel(Conversation c) {
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

     */

    //change panel when a chat is selected or create a new chat from the newChatButton
    /*
    private void addPanel(Conversation c) {
        messageField = new DisplayMessageGUI(c);
        middlePanel.removeAll();
        middlePanel.add(messageField);
        middlePanel.revalidate();
        middlePanel.repaint();
    }

     */

    public void updateChat(Conversation c) {
        if (messageField != null
                && messageField.getConversation().getMembers().containsAll(c.getMembers())
                && c.getMembers().containsAll(messageField.getConversation().getMembers())) {
            // if the user is currently in that chat
            messageField.updateMessage(c);
        } else {
            DisplayMessageGUI tempPanel = new DisplayMessageGUI(c, messageClient);
            String label = tempPanel.setMessageLabel(); // create a temp label for this conversation
            if (allMessages.containsKey(label)) { //use the label to see if conversation already exist
                allMessages.get(label).updateMessage(c); //update the message, do not need to display
            } else { // if panel for this conversation does not exist (aka create new message)
                createPanel(c); //create a new panel for it
                displayMessage(label);
                //display that new panel (label should match already because memberList is sorted in DisplayMessageGUI)
                inboxList.setSelectedIndex(inboxes.size() - 1);
            }

        }
    }


    /*
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
                //addPanel(c); //create new panel with that conversation
            }
        }
    }

     */

    private void setUsersToSend(String userNames, String message) {
        usersToSend = new ArrayList<>();
        userNames = userNames.strip(); //strip leading white spaces
        if (userNames.isEmpty() || message.isEmpty() || userNames.equals(clientUsername)) { //if the user type in their name only
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
        DisplayMessageGUI tempPanel = new DisplayMessageGUI(new Conversation(usersToSend), messageClient);
        String label = tempPanel.setMessageLabel(); // create a temp label for this conversation
        if (allMessages.containsKey(label)) { //use the label to see if conversation already exist
                int answer = JOptionPane.showConfirmDialog(null,
                        "This conversation already exists. Do you want to send this chat to this conversation?",
                        "Social Messaging App", JOptionPane.YES_NO_OPTION); //ask if the user want to add this message to the conversation or not
                if (answer == JOptionPane.YES_OPTION) { // if yes
                    messageClient.setClientMessageMessaging(message, usersToSend); //send message as usual
                    messageClient.setSendMessageClicked(true); //notify sendButton clicked, this calls the updateCurrentChat
                    displayMessage(label); //SEE IF IT UPDATE IN TIME, if not, use Thread.sleep maybe
                    return;
                }
        }
        messageClient.setSendMessageClicked(true);//notify button click
        messageClient.setCheckUserAccountsExisting(true); // get the server to check if the recipients are in the system
        messageClient.setClientMessageNewChat(usersToSend); // start a new chat

        if (!messageClient.getUserAccountsExist()) {
            JOptionPane.showMessageDialog(null, "One or More of the account usernames entered does not exist",
                    "Social Messaging App", JOptionPane.ERROR_MESSAGE);
            messageClient.setUserAccountsExists(true);
            return;
        }

        MessageClient.setClientMessageMessaging(message, usersToSend);
        messageClient.setSendMessageClicked(true);
    }

    private void removeConversation(String label, int index) {
        inboxes.remove(index);
        messageClient.setClientMessageDeleteUser(allMessages.get(label).getConversation());
        allMessages.remove(label);
    }

    public void editChat(Conversation c) {
        DisplayMessageGUI newPanel = new DisplayMessageGUI(c, messageClient);
        String label = newPanel.setMessageLabel();
        DisplayMessageGUI oldPanel = allMessages.get(label);
        allMessages.replace(label, oldPanel, newPanel);

    }


    /**
     * GUI Methods
     * All methods related to FocusListener, MouseListener, ActionListener
     */
    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == sendButton) { //sendButton is clicked = new message
                String message = messageText.getText();
                //System.out.println(message); //print out message for debugging
                if (message.isEmpty()) { //check if there is message to send
                    JOptionPane.showMessageDialog(null, "There is no message to send!",
                            "Social Messaging App", JOptionPane.ERROR_MESSAGE);
                } else {
                    messageClient.setClientMessageMessaging(message, usersToSend); //send the message to messageClient
                    messageClient.setSendMessageClicked(true); //set to TRUE to notify button click
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
            if (e.getClickCount() == 2) { //double click
                String chatLabel = inboxList.getSelectedValue();
                int index = inboxList.getSelectedIndex();
                int choice = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to permanently delete this conversation?",
                        "Delete Conversation", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    removeConversation(chatLabel, index);
                }
            } else {
                String selectedConversation = inboxList.getSelectedValue();
                System.out.println(selectedConversation);
                messageText.setEditable(true); //enable text field to start typing
                displayMessage(selectedConversation);
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
