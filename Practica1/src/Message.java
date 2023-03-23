public class Message {
    private int type;
    private String content;

    public Message(String content, int type) {
        this.content = content;
        this.type = type;
    }

    public String getMessage(){
        return this.content;
    }
    public int getType(){
        return this.type;
    }

}