import java.rmi.RemoteException;
import java.util.*;
import java.util.Queue;

public class MessageAPIImpl implements MessageAPI {


    public HashMap<String, Vector<TopicQueue>> topicQueues;
    public HashMap<String, Vector<Message>> topicMessages;
    protected MessageAPIImpl() throws RemoteException {
        super();
    }

    @Override
    public void MsqQ_Init(String ServerAddress) {
        topicQueues = new HashMap<>();
        topicMessages = new HashMap<>();
    }

    @Override
    public EMomError MsgQ_CreateQueue(String msgqname) {
        Vector<Message> cola = new Vector<>();
        if (topicMessages.containsKey(msgqname)){
            return new EMomError("P2P chat already exist");
        }else{
            topicMessages.put(msgqname, cola);
        }
        return new EMomError("Done");
    }

    @Override
    public EMomError MsgQ_CloseQueue(String msgqname) {
        if (!topicMessages.containsKey(msgqname)){
            return new EMomError("P2P Chat doesn't exist");
        }else{
            topicMessages.get(msgqname).clear();
        }
        return new EMomError("Done");
    }

    @Override
    public EMomError MsgQ_SendMessage(String msgqname, String message, int type) {
        if(!topicMessages.containsKey(msgqname)) return new EMomError("Error, cola closed");
        Message temp = new Message(message, type);
        topicMessages.get(msgqname).add(temp);
        return new EMomError("MEssage added to the queue for" + msgqname);
    }

    @Override
    public String MsgQ_ReceiveMessage(String msgqname, int type) {
        String result = topicMessages.get(msgqname).get(0).getMessage();
        if (type == 0) return result;
        Vector<Message> temp = topicMessages.get(msgqname);
        for (int i = 0; i < temp.capacity(); i++){
            if (temp.get(0).getType() == type) return temp.get(0).getMessage();
        }
        return "No Message available";
    }

    @Override
    public EMomError MsgQ_CreateTopic(String topicname, EPublishMode mode) {
        return null;
    }

    @Override
    public EMomError MsgQ_CloseTopic(String topicname) {
        return null;
    }

    @Override
    public EMomError MsgQ_Publish(String topic, String message, int type) {
        return null;
    }

    @Override
    public EMomError MsgQ_Subscribe(String topic, TopicListenerInterface listener) {
        return null;
    }
}
