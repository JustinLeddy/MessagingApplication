import javax.swing.*;
import java.awt.*;

public class ChatGUI extends JFrame implements Runnable {
    ChatGUI chatGUI;
    MessageClient messageClient;
    //Login panel
    JButton sendButton;
    JTextArea messageText;
    JTextArea sentText;
    JList userList;
    JFrame messageFrame;
    //Chat panel

    public ChatGUI() {
        showMessagePanel();
    }

    public void showMessagePanel() {
        messageFrame = new JFrame();
        messageText = new JTextArea(5,60);
        sentText = new JTextArea(5, 60);
        userList = new JList();
        userList.setVisibleRowCount(10);

        sendButton = new JButton("Send");

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BorderLayout());
        middlePanel.add(new JScrollPane((sentText)), "Center");

        JPanel botPanel = new JPanel();
        botPanel.setLayout(new FlowLayout());
        botPanel.add(new JScrollPane(messageText));
        botPanel.add(sendButton);

        JPanel userListPanel = new JPanel();
        userListPanel.setLayout(new BorderLayout());
        userListPanel.add(new JLabel("Inboxes", JLabel.CENTER), "North");
        userListPanel.add(new JScrollPane(userList), "Center");

        messageFrame.add(middlePanel, "Center");
        messageFrame.add(botPanel, "South");
        messageFrame.add(userListPanel, "East");
        messageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        messageFrame.setVisible(true);
    }
    public void run() {

    }

    public static void main(String[] args) {
        new ChatGUI();
    }
}
