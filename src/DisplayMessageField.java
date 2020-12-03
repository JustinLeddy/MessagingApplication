import java.util.ArrayList;

public class DisplayMessageField {
    private ArrayList<String> messages;
    private final MessageClient client;
    private final String userName;

    public DisplayMessageField(MessageClient client, String userName) {
        this.client = client;
        this.userName = userName;
    }
}