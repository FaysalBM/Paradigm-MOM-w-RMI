import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class MessagingClient {
    private MessageAPI messagingAPI;

    public MessagingClient() throws RemoteException, NotBoundException, MalformedURLException {
        messagingAPI = (MessageAPI) Naming.lookup("MessagingAPI");
    }

    public void sendMessage(Message message) throws RemoteException {
        messagingAPI.sendMessage("message");
    }

    public String receiveMessage(String recipient) throws RemoteException {
        return messagingAPI.receiveMessage(recipient);
    }
    public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException {
        String registry = "localhost";
        if (args.length >=1)
            registry = args[0];

        // Formatear la url del registro
        String registro ="rmi://" + registry + "/MessageAPI";

        // Buscar el servicio en el registro.
        Remote servicioRemoto = Naming.lookup(registro);

        // Convertir a un interfaz
        MessageAPI servicioMensaje = (MessageAPI) servicioRemoto;

        // Encender la bombilla
        System.out.println("Invocando sendMessage");
        servicioMensaje.sendMessage("hola");

        System.out.println(servicioMensaje.receiveMessage("x"));

    }
}