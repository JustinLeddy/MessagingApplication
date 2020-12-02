import java.util.ArrayList;

public class Conversation {
    //Fields
    private ArrayList<String> messages;
    private ArrayList<String> members;


    public Conversation(ArrayList<String> members) {
        this.members = members;
        this.messages = new ArrayList<>();
    }

    public Conversation(ArrayList<String> members, ArrayList<String> messages) {
        this(members);
        this.messages = messages;
    }

    //functional methods

    public String membersString() {
        if (members.size() == 1) {
            return members.get(0);
        }
        String output = "";
        for (String member : members) {
            output += member + ",";
        }
        return output.substring(0, output.length() - 1);
    }

    //Username|message
    public void addMessage(String message) {
        messages.add(message);
    }

    //accessors
    public ArrayList<String> getMessages() {
        return messages;
    }

    public ArrayList<String> getMembers() {
        return members;
    }


}
