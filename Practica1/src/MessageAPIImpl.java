import javax.swing.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class MessageAPIImpl implements MessageAPI {

    public Semaphore semaphore = new Semaphore(0); // Initialize semaphore with permits = 1
    public HashMap<String,TopicQueue> topicQueues = new HashMap<String, TopicQueue>();
    public HashMap<String, Vector<Message>> topicMessages = new HashMap<>();
    private int expectedClients = 0;

    protected MessageAPIImpl() throws RemoteException {
        super();
    }

    @Override
    public void MsqQ_Init(String ServerAddress) throws RemoteException, MalformedURLException {

        MessageAPI apiMess = (MessageAPI) UnicastRemoteObject.exportObject(this, 0);
        Registry registry = LocateRegistry.getRegistry(ServerAddress, 0);
        // Crear la URL del registro.
        String registro ="rmi://" + ServerAddress + "/MessengerRMI";
        // Registrar el servicio
        Naming.rebind(registro, apiMess);
        System.err.println("API ready");
    }


    public EMomError MsgQ_CreateQueue(String msgqname) {
        Vector<Message> cola = new Vector<>();
        if (topicMessages.containsKey(msgqname)){
            return null;
        }else{
            topicMessages.put(msgqname, cola);
        }
        return null;
    }

    @Override
    public EMomError MsgQ_CloseQueue(String msgqname) {
        if (!topicMessages.containsKey(msgqname)){
            return new EMomError("P2P Chat doesn't exist");
        }else{
            topicMessages.remove(msgqname);
        }
        return new EMomError("Done");
    }
    //Controlar condiciones de carrera al publicar
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
        if(topicQueues.containsKey(topicname)){
            return null;
        }else{
            topicQueues.put(topicname, new TopicQueue(mode));
        }
        return null;
    }

    @Override
    public EMomError MsgQ_CloseTopic(String topicname) {
        if(topicQueues.get(topicname).messages.size() == 0){
            topicQueues.remove(topicname);
            return new EMomError("Topic Queue deleted");
        }
        return new EMomError("Topc Queue still has messages");
    }

    //Controlar condiciones de carrera al publicar
    @Override
    public EMomError MsgQ_Publish(String topic, String message, int type) throws MalformedURLException, RemoteException {
        if(topicQueues.containsKey(topic)){
            topicQueues.get(topic).addMessage(new Message(message, type));
            int x = topicQueues.get(topic).clientsSuscribed.size();
            System.out.println("Clients subscribed: "+x);
            //Recorrer los usuarios de ese topic i invocar el metodo de onTopicMessage del listener de los suscritores
            if(topicQueues.get(topic).messages.size() == expectedClients){
                for(int i = 0; i < x; i++){
                    topicQueues.get(topic).clientsSuscribed.get(i).onTopicMessage(topicQueues.get(topic).messages.get(i).getMessage());
                }
            }
            System.out.println("Messsage added");
            return null;
        }
        return null;
    }

    @Override
    public EMomError MsgQ_Subscribe(String topic, TopicListenerInterface listener) {
        if(topicQueues.containsKey(topic)){
            if(topicQueues.get(topic).clientsSuscribed.size() == expectedClients){
                semaphore.release();
            }
            topicQueues.get(topic).subClient(listener);
            return null;
        }
        return null;
    }

    @Override
    public Semaphore getSemaphore(int subs) {
        this.expectedClients = subs;
        return semaphore;
    }
}
