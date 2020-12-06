import java.util.ArrayList;
import java.util.Collections;

public class Conversation {
    //Fields
    private ArrayList<String> messages;
    private ArrayList<String> members;


    public Conversation(ArrayList<String> members) {
        Collections.sort(members);
        this.members = members;
        this.messages = new ArrayList<>();
    }

    public Conversation(ArrayList<String> members, ArrayList<String> messages) {
        this(members);
        this.messages = messages;
    }

    //functional methods
    public void removeMessageAtIndex(int index) {
        messages.remove(index);
    }

    public void removeMemberWithName(String name) {
        members.remove(name);
    }

    public void editMessageAtIndex(int index, String message) {
        this.messages.set(index, message);
    }

    //Username|message
    public void addMessage(String message) {
        this.messages.add(message);
    }

    //accessors
    public ArrayList<String> getMessages() {
        return messages;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }
}
