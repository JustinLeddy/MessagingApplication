import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public class DisplayMessageGUI extends JPanel {
    //private final MessageClient client;
    private Conversation conversation;
    //private DefaultListModel<String> list = new DefaultListModel<>();
    //private JList<String> messages = new JList<>(list);
    private final JTextArea messages;

    public DisplayMessageGUI() {
        messages = new JTextArea(20, 40);
        messages.setEditable(false);
    }

    public DisplayMessageGUI(Conversation conversation) {
        this.conversation = conversation;
        messages = new JTextArea(20, 40);
        messages.setLineWrap(true);
        messages.setWrapStyleWord(true);
        messages.setEditable(false);
        setLayout(new BorderLayout());
        displayMessages();
        add(new JScrollPane(messages), "Center");
    }

    public void displayMessages() {

        ArrayList<String> allMessages = conversation.getMessages();
        for (String message : allMessages) {
            messages.append(message + "\n");
        }


    }

    public void updateMessage() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                    ArrayList<String> allMessages = conversation.getMessages();
                    messages.append(allMessages.get(allMessages.size() - 1) + "\n");
                    messages.setCaretPosition(messages.getDocument().getLength());
            }
        });
    }

    public void updateMessage(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                messages.append(message + "\n");
                messages.setCaretPosition(messages.getDocument().getLength());
            }
        });
    }


    //public void updateMessage(String message) {
    //list.addElement(message);
    //}

    private void formatMessage() {


    }

}
