import java.io.Serializable;

public class EMomError implements Serializable {
    private final String error;
    public EMomError(String error){
        this.error = error;
    }
    public void main(String[] args) {
        System.out.println("Error! +" + this.error);
    }
}
