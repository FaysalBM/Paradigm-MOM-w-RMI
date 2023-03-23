import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MessagingClient implements TopicListenerInterface{
    private MessageAPI messagingAPI;

    public MessagingClient() {

    }

    public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 0);
        String tempR = "127.0.0.1";
        if (args.length >= 1){
            registry = LocateRegistry.getRegistry(args[0], 0);
            tempR = args[0];
        }

        // Formatear la url del registro
        String registro ="rmi://" + tempR + "/MessengerRMI";

        // Buscar el servicio en el registro.
        Remote servicioRemoto = Naming.lookup(registro);

        // Convertir a un interfaz
        MessageAPI servicioMensaje = (MessageAPI) servicioRemoto;
        // Encender la bombilla
        while(true){

        }
    }

    @Override
    public void onTopicMessage(Message message) {
        System.out.println("Recibed a message from a topic!");
    }

    @Override
    public void onTopicClose(String topic) {
        System.out.println("The queue of the topic "+topic+" has closed!");
    }
}