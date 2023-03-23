import java.rmi.RemoteException;
import java.util.*;
import java.util.Queue;

public class MessageAPIImpl implements MessageAPI {


    public HashMap<String, TopicQueue> topicQueues;
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
            return new EMomError("Topic Queue already created");
        }else{
            topicQueues.put(topicname, new TopicQueue(mode));
        }
        return new EMomError("Topic Queue created");
    }

    @Override
    public EMomError MsgQ_CloseTopic(String topicname) {
        if(topicQueues.get(topicname).messages.capacity() == 0){
            topicQueues.remove(topicname);
            return new EMomError("Topic Queue deleted");
        }return new EMomError("Topc Queue still has messages");
    }

    //Controlar condiciones de carrera al publicar
    @Override
    public EMomError MsgQ_Publish(String topic, String message, int type) {
        if(topicQueues.containsKey(topic)){
            topicQueues.get(topic).addMessage(new Message(message, type));
            //Recorrer los usuarios de ese topic i invocar el metodo de onTopicMessage del listener de los suscritores
            for(int i = 0; i < topicQueues.get(topic).clientsSuscribed.capacity(); i++){
                topicQueues.get(topic).clientsSuscribed.get(i).onTopicMessage(new Message(message, type));
            }
            return new EMomError("Message Published");
        }
        return new EMomError("No Queue with that topic");
    }

    @Override
    public EMomError MsgQ_Subscribe(String topic, TopicListenerInterface listener) {
        if(topicQueues.containsKey(topic)){
            topicQueues.get(topic).subClient(listener);
            return new EMomError("Client Subscribed");
        }
        return new EMomError("No Queue with that topic");
    }
}
