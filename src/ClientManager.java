import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

//Map to store user ips with their Message Handler
public class ClientManager {
    private static Map<String, MessageHandler> deliverTo;
    private ClientManager() {}
    static {
        deliverTo = new HashMap<String, MessageHandler>();
    }
    public static void addTrace(String key, MessageHandler value) {
        deliverTo.put(key, value);
    }
    public static MessageHandler getTrace(String key) {
        return deliverTo.get(key);
    }

    public static void clearTrace() {
        deliverTo.clear();
    }

    public static void removeTrace(String key) {
        deliverTo.remove(key);
    }
}
