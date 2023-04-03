import javax.management.ValueExp;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Vector;

public interface MessageAPI extends java.rmi.Remote {

    void MsqQ_Init(String ServerAddress) throws RemoteException, MalformedURLException;
    EMomError MsgQ_CreateQueue(String msgqname) throws RemoteException, MalformedURLException;
    EMomError MsgQ_CloseQueue(String msgqname) throws RemoteException, MalformedURLException;
    EMomError MsgQ_SendMessage(String msgqname, String message, int type) throws RemoteException, MalformedURLException;
    String MsgQ_ReceiveMessage(String msgqname, int type) throws RemoteException, MalformedURLException;
    EMomError MsgQ_CreateTopic(String topicname, EPublishMode mode) throws RemoteException, MalformedURLException;
    EMomError MsgQ_CloseTopic(String topicname) throws RemoteException, MalformedURLException;
    EMomError MsgQ_Publish(String topic, String message, int type) throws RemoteException, MalformedURLException;
    EMomError MsgQ_Subscribe(String topic, TopicListenerInterface listener) throws RemoteException, MalformedURLException;
}
