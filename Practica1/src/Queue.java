import java.rmi.Remote;

public interface Queue<S> extends Remote {
    void add(S message);
}
