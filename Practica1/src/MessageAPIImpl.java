import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
public class MessageAPIImpl extends UnicastRemoteObject implements MessageAPI, Runnable {
    private Queue<String> messageQueue;
    protected MessageAPIImpl() throws RemoteException {
        super();
    }

    @Override
    public void MsqQ_Init(String ServerAddress) {

    }

    @Override
    public void sendMessage(String message) throws RemoteException {
        messageQueue.add(message);
    }
    @Override
    public String receiveMessage(String recipient) throws RemoteException {
        return messageQueue.toString();
    }

    @Override
    public void run() {

    }
    /*
    @Override
    public Object MsgQ_CreateQueue(String msgqname) {
        return null;
    }

    @Override
    public Object MsgQ_CloseQueue(String msgqname) {
        return null;
    }

    @Override
    public Object MsgQ_SendMessage(String msgqname, String message, int type) {
        return null;
    }

    @Override
    public String MsgQ_ReceiveMessage(String msgqname, int type) {
        return null;
    }

    @Override
    public Object MsgQ_CreateTopic(String topicname, EPublishMode mode) {
        return null;
    }

    @Override
    public Object MsgQ_CloseTopic(String topicname) {
        return null;
    }

    @Override
    public Object MsgQ_Publish(String topic, String message, int type) {
        return null;
    }

    @Override
    public Object MsgQ_Subscribe(String topic, TopicListenerInterface listener) {
        return null;
    }*/
}
