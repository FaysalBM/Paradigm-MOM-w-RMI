import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class MessageAPIImpl implements MessageAPI {

    public Semaphore semaphore = new Semaphore(0); // Initialize semaphore with permits = 1
    public final HashMap<String,TopicQueue> topicQueues = new HashMap<String, TopicQueue>();
    public final HashMap<String, Vector<Message>> topicMessages = new HashMap<>();
    private int expectedClients = 0;
    public Boolean canPublish = false;
    public int counterClient = 0;
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
            return new EMomError("The queue with topic " + msgqname + " already exists!");
        }else{
            synchronized (topicMessages){
                topicMessages.put(msgqname, cola);
            }
        }
        return null;
    }

    @Override
    public EMomError MsgQ_CloseQueue(String msgqname) {
        synchronized (topicMessages){
            if (!topicMessages.containsKey(msgqname)){
                return null;
            }else{
                topicMessages.remove(msgqname);
                System.out.println("Closed Queue " + msgqname);
            }
            return null;
        }
    }
    //Controlar condiciones de carrera al publicar
    @Override
    public EMomError MsgQ_SendMessage(String msgqname, String message, int type) {
        if(!topicMessages.containsKey(msgqname)) return null;
        synchronized (topicMessages.get(msgqname)){
            Message temp = new Message(message, type);
            topicMessages.get(msgqname).add(temp);
        }
        System.out.println("Message added to the queue " + msgqname + " -> " + message);
        return null;
    }

    @Override
    public String MsgQ_ReceiveMessage(String msgqname, int type) {
        synchronized (topicMessages.get(msgqname)){
            if(topicMessages.get(msgqname).size() > 0){
                String result = topicMessages.get(msgqname).get(0).getMessage();
                if (type == 0) {
                    System.out.println("Message received -> " + result);
                    topicMessages.get(msgqname).remove(0);
                    return result;
                }
                Vector<Message> temp = topicMessages.get(msgqname);
                for (int i = 0; i < temp.size(); i++){
                    if (temp.get(i).getType() == type) {
                        String t = temp.get(i).getMessage();
                        temp.remove(i);
                        return t;
                    }

                }
            }
        }
        return null;
    }

    @Override
    public EMomError MsgQ_CreateTopic(String topicname, String mode) {
        if(topicQueues.containsKey(topicname)){
            System.out.println("This topic already Exists!");
            return new EMomError("This topic already exists!");
        }else{
            synchronized (topicQueues){
                topicQueues.put(topicname, new TopicQueue(mode));
            }
        }
        return null;
    }

    @Override
    public EMomError MsgQ_CloseTopic(String topicname) throws MalformedURLException, RemoteException, InterruptedException {
        int x = topicQueues.get(topicname).clientsSuscribed.size();
        for(int i = 0; i < x; i++){
            topicQueues.get(topicname).clientsSuscribed.get(i).onTopicClose(topicname);
        }
        topicQueues.remove(topicname);
        System.out.println("Topic Queue " + topicname + "closed!");
        return null;
    }

    //Controlar condiciones de carrera al publicar
    @Override
    public EMomError MsgQ_Publish(String topic, String message, int type) throws MalformedURLException, RemoteException {
        if(topicQueues.containsKey(topic)){
            synchronized (topicQueues.get(topic)){
                topicQueues.get(topic).addMessage(new Message(message, type));
                System.out.println("Messsage added to the respective history");
                int x = topicQueues.get(topic).clientsSuscribed.size();
                System.out.println("Clients subscribed into " + topic +": " + x);
                //Recorrer los usuarios de ese topic i invocar el metodo de onTopicMessage del listener de los suscritores
                System.out.println(topicQueues.get(topic).getMode());
                if(Objects.equals(topicQueues.get(topic).getMode(), "B")){
                    System.out.println("Publishing in Broadcast");
                    for(int i = 0; i < x; i++){
                        topicQueues.get(topic).clientsSuscribed.get(i).onTopicMessage(message);
                    }
                    topicQueues.get(topic).messages.remove(topicMessages.get(topic).size() - 1);
                }else if(Objects.equals(topicQueues.get(topic).getMode(), "RR")){
                    System.out.println("Publishing in Round Robin");
                    topicQueues.get(topic).clientsSuscribed.get(counterClient).onTopicMessage(message);
                    counterClient++;
                    if(counterClient == expectedClients){
                        counterClient = 0;
                    }
                }
            }
            System.out.println("Messsage sended");
        }
        return null;
    }

    @Override
    public EMomError MsgQ_Subscribe(String topic, TopicListenerInterface listener) {
        if(topicQueues.containsKey(topic)){
            topicQueues.get(topic).subClient(listener);
            System.out.println("Object substribed into "+topic);
            System.out.println(topicQueues.get(topic).clientsSuscribed.size());
            System.out.println(expectedClients);
            if(Objects.equals(topic, "Work")){
                if(topicQueues.get(topic).clientsSuscribed.size() >= expectedClients){
                    semaphore.release();
                    System.out.println("Semaphore released");
                }
            }
            return null;
        }
        return null;
    }

    @Override
    public Semaphore getSemaphore(int subs) {
        this.expectedClients = subs;
        return semaphore;
    }

    @Override
    public void catchSem() throws RemoteException, MalformedURLException, InterruptedException {
        semaphore.acquire();
    }

    @Override
    public void releaseSem() throws RemoteException, MalformedURLException {
        semaphore.release();
    }
}
