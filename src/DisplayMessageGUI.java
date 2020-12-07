import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;

/**
 * DisplayMessageGUI
 * <p>
 * A unique message display for each conversation.
 * Handle real-time message updating
 *
 * @author Alex Frey, Justin Leddy, Maeve Tra, Yifei Mao, Naveena Erranki
 * @version December 7th, 2020
 */

public class DisplayMessageGUI extends JPanel {
    private final MessageClient CLIENT;
    private final String CLIENT_USERNAME;
    private final String MESSAGE_LABEL;
    private Conversation conversation;
    private DefaultListModel<String> list;
    private JList<String> messages;


    /**
     * Constructor for DisplayMessageGUI object. Initialize all the GUI components
     * preload the list with old messages
     *
     * @param conversation conversation to display
     * @param client       the current sender of this conversation
     */
    public DisplayMessageGUI(Conversation conversation, MessageClient client) {
        this.conversation = conversation;
        this.CLIENT = client;
        this.CLIENT_USERNAME = client.getClientUsername();
        this.list = new DefaultListModel<>();
        initializeList(); //fill list with old messages
        this.messages = new JList<>(list);
        messages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        messages.setFont(new Font("Sans Serif", Font.PLAIN, 12));
        messages.addMouseListener(mouseListener);
        this.MESSAGE_LABEL = setMessageLabel();
        setLayout(new BorderLayout());
        add(new JScrollPane(messages), "Center");
    }

    /**
     * Iterate through the message array and preload list with old messages
     */
    private void initializeList() {
        ArrayList<String> allMessages = conversation.getMessages();
        for (String message : allMessages) {
            list.addElement(message);
        }
    }

    /**
     * sort the member list and create an appropriate label for this conversation
     *
     * @return label that is created
     */
    public String setMessageLabel() {
        String sendTo = "";
        ArrayList<String> members = conversation.getMembers();
        Collections.sort(members);
        for (String m : members) {
            sendTo += " " + m;
        }
        sendTo = sendTo.replaceFirst(" ", "");
        sendTo = sendTo.replaceAll(" ", " \\| ");
        return sendTo;
    }

    /**
     * Getter for MESSAGE_LABEL
     *
     * @return current label of the conversation
     */
    public String getMessageLabel() {
        return this.MESSAGE_LABEL;
    }

    /**
     * Getter for conversation
     *
     * @return conversation in this panel
     */
    public Conversation getConversation() {
        return this.conversation;
    }

    /**
     * Thread-safe implementation of message updating. Add the new message to the list
     * and auto-scroll to the latest message
     *
     * @param c new conversation that has the new message
     */
    public void updateMessage(Conversation c) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                conversation = c;
                ArrayList<String> allMessages = conversation.getMessages();
                list.addElement(allMessages.get(allMessages.size() - 1)); //get the latest message and print out
                messages.ensureIndexIsVisible(list.size() - 1); //auto-scroll to the latest message
            }
        });
    }

    /**
     * Temporarily display a system message to notify user leaves
     *
     * @param removedUser user that leaves
     */
    public void notifyUserLeft(String removedUser) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                list.addElement(String.format("System|%s has left the chat.", removedUser));
            }
        });
    }

    /**
     * MouseListener to implement edit/delete message.
     * Get the message and prompt the user to edit/delete.
     * Send message to MessageClient to record on file
     * <p>
     * Testing (more info in README)
     * - double click on any messages should prompt a pop up window. Tested with single click
     * - edit/delete message should immediately update conversation on all sides
     * - when trying to edit/delete messages that are not yours, an error message will pop up
     */
    private MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) { //double click
                String message = messages.getSelectedValue();
                if (!checkUser(message)) { //check if user is the sender
                    JOptionPane.showMessageDialog(null,
                            "You can only edit or delete your message!",
                            "Invalid choice", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int index = messages.getSelectedIndex();
                String[] options = {"Edit", "Delete", "Cancel"};
                int answer = JOptionPane.showOptionDialog(null,
                        String.format("Do you want to edit or delete this message? \n%s", message),
                        "Edit/Delete message", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, options, options[0]);
                switch (answer) {
                    case 0 -> { //Edit
                        String edit = JOptionPane.showInputDialog("Make your changes here:", message.substring(message.indexOf("|") + 1));
                        if (edit == null) { // cancel
                            return;
                        } else if (edit.contains("<*>") || edit.contains("|") || edit.contains("%&") || edit.contains("<&*>")) { //<*> or | or %& or <&*>
                            JOptionPane.showMessageDialog(null, "Please make sure your message doesnt contain <*> or | or %& or <&*>.",
                                    "Social Messaging App", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        message = message.substring(0, message.indexOf("|") + 1) + edit;
                        conversation.editMessageAtIndex(index, message);
                        list.set(index, message);
                        notifyChange();
                    }
                    case 1 -> { //delete
                        int choice = JOptionPane.showConfirmDialog(null,
                                String.format("Are you sure you want to permanently delete this message?\n%s", message),
                                "Delete Message", JOptionPane.YES_NO_OPTION);
                        if (choice == JOptionPane.YES_OPTION) {
                            conversation.removeMessageAtIndex(index);
                            list.remove(index);
                            notifyChange();
                        }
                    }
                    default -> {
                        return;
                    }
                }
            }
        }
    };

    /**
     * notify MessageClient that there is a change in the conversation
     */
    private void notifyChange() { //send this conversation back to MessageClient
        MessageClient.setClientMessageUpdateChat(this.conversation);
        CLIENT.setSendMessageClicked(true); //to enter the loop

    }

    /**
     * local check to see if user is the sender
     *
     * @param message
     * @return
     */
    private boolean checkUser(String message) { //check if the message is sent by this user or not
        String[] info = message.split("\\|");
        return info[0].equals(CLIENT_USERNAME);
    }


}
