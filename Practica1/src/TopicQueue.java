import java.util.HashMap;
import java.util.Vector;

public class TopicQueue {
    Vector<TopicListenerInterface> clientsSuscribed;
    Vector<Message> messages;
    final EPublishMode modeP = new EPublishMode("");
    public TopicQueue(EPublishMode mode){
        clientsSuscribed = new Vector<>();
        messages = new Vector<>();
    }
    public void addMessage(Message mess){
        messages.add(mess);
    }
    public void subClient(TopicListenerInterface cl){
        clientsSuscribed.add(cl);
    }
    public void unsubClient(TopicListenerInterface cl){
        clientsSuscribed.remove(cl);
    }
}
