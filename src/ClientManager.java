import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
/**
 * Client Manager
 * <p>
 * A hashmap that stores user identity and thread connection.
 * Assigns user IP/port pair as a key to the value of a MessageHandler instance.
 *
 * @author Alex Frey, Justin Leddy, Maeve Tra, Yifei Mao, Naveena Erranki
 * @version December 7th, 2020
 */
public class ClientManager {
    private static HashMap<String, MessageHandler> deliverTo = new HashMap<String, MessageHandler>();

    /**
     * add a client to the map
     * @param key is their ip address
     * @param value a unique MessageHandler
     */
    public static void addTrace(String key, MessageHandler value) {
        deliverTo.put(key, value);
    }

    /**
     * Getter for deliverTo
     * @return the map consists of all clients in the system
     */
    public static HashMap<String, MessageHandler> getDeliverTo() {
        return deliverTo;
    }
}
