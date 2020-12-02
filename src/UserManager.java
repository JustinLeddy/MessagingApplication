import java.util.HashMap;
import java.util.Map;

//Map to store User IPs with their usernames
public class UserManager {
    private static Map<String, String> storeInfo;

    private UserManager() {
    }

    static {
        storeInfo = new HashMap<String, String>();
    }

    public static void addTrace(String key, String value) {
        storeInfo.put(key, value);
    }

    public static String getTrace(String key) {
        return storeInfo.get(key);
    }

    public static void clearTrace() {
        storeInfo.clear();
    }

    public static void removeTrace(String key) {
        storeInfo.remove(key);
    }
}
