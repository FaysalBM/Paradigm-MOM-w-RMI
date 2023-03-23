import java.util.HashMap;
import java.util.Vector;

public class TopicQueue {
    public HashMap<String, Vector<ClientListener>> clientsSuscribed;
    public Vector<String> messages;
    public EPublishMode modeP;
    public TopicQueue(EPublishMode mode){
        clientsSuscribed = new HashMap<>();
        messages = new Vector<>();
        modeP = mode;
    }

}
