public class Message {
    private String sender;
    private String recipient;
    private String content;

    public Message(String sender, String recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    }

    public String getMessage(){
        return this.content;
    }
    public String getSender(){
        return this.sender;
    }
    public String getRecipient(){
        return this.recipient;
    }
}