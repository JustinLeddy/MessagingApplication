import java.util.ArrayList;

public class DisplayMessageGUI {
    private ArrayList<String> messages;
    private final MessageClient client;
    private final String userName;

    public DisplayMessageGUI(MessageClient client, String userName) {
        this.client = client;
        this.userName = userName;
    }
}
