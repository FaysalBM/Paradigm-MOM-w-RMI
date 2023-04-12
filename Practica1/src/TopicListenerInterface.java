import java.net.MalformedURLException;
import java.rmi.RemoteException;

public interface TopicListenerInterface extends java.rmi.Remote{
    void onTopicMessage(String message) throws RemoteException, MalformedURLException;
    void onTopicClose(String topic) throws RemoteException, MalformedURLException, InterruptedException;
}
