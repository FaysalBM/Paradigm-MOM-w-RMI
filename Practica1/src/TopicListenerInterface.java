public interface TopicListenerInterface extends java.rmi.Remote{
    void onTopicMessage(Message message);
    void onTopicClose(String topic);
}
