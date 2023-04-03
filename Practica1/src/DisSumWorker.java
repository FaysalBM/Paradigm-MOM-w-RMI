import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class DisSumWorker implements TopicListenerInterface{

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
        DisSumWorker monitor = new DisSumWorker();

        // Exportar el objeto de la clase de la implementación al stub del interfase.
        TopicListenerInterface emonitor = (TopicListenerInterface) UnicastRemoteObject.exportObject(monitor, 0);
        servicioMensaje.MsgQ_Subscribe("Work", emonitor);
        while(true){

        }
    }

    @Override
    public void onTopicMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void onTopicClose(String topic) {
        System.out.println(topic);
    }
}
