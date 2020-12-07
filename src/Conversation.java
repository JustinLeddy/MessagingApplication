import java.util.ArrayList;
import java.util.Collections;

/**
 * Conversation
 * <p>
 * The class to handle information on a single conversation
 * it hold an array of messages in this conversation
 * and an array of members
 *
 * @author Alex Frey, Justin Leddy, Maeve Tra, Yifei Mao, Naveena Erranki
 * @version December 7th, 2020
 */
public class Conversation {
    //Fields
    private ArrayList<String> messages;
    private ArrayList<String> members;

    /**
     * a constructor which creates a new Conversation
     * object with the messages empty and sets
     * the list of members with the given
     * parameter. this does not include the user, who is
     * already added
     *
     * @param members ArrayList<String> of all members to be added to this conversation
     */
    public Conversation(ArrayList<String> members) {
        if (members != null) {
            Collections.sort(members);
        }
        this.members = members;
        this.messages = new ArrayList<>();
    }

    /**
     * a secondary constructor to also initialize
     * the messages with a given array
     *
     * @param members  ArrayList<String> of all members to be added to this conversation
     * @param messages ArrayList<String> of all messages to be added to this conversation
     */
    public Conversation(ArrayList<String> members, ArrayList<String> messages) {
        this(members);
        this.messages = messages;
    }

    //functional methods

    /**
     * a method to remove a message at an index
     *
     * @param index int index to remove the message from
     */
    public void removeMessageAtIndex(int index) {
        messages.remove(index);
    }

    /**
     * a method to remove a member with a given nam
     *
     * @param name String name of member to remove
     */
    public void removeMemberWithName(String name) {
        members.remove(name);
    }

    /**
     * a method to set a message at an index to a given message
     *
     * @param index   int index of the message to change
     * @param message String new message to change the message to
     */
    public void editMessageAtIndex(int index, String message) {
        this.messages.set(index, message);
    }

    /**
     * method to append a message to this conversations message array
     *
     * @param message String message to add
     */
    public void addMessage(String message) {
        this.messages.add(message);
    }

    //accessors

    /**
     * method to access the messages array of this conversation
     *
     * @return ArrayList<String> messages field
     */
    public ArrayList<String> getMessages() {
        return messages;
    }

    /**
     * method to access the members array of this conversation
     *
     * @return ArrayList<String> members field
     */
    public ArrayList<String> getMembers() {
        return members;
    }

    /**
     * method to set the field members as the given ArrayList<String>
     *
     * @param members ArrayList<String> to set the field members to
     */
    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    /**
     * method to set the field messages as the given ArrayList<String> messages
     *
     * @param messages ArrayList<String> to set the field messages to.
     */
    public void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }
}
