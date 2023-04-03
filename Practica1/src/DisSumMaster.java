import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static java.lang.Thread.sleep;

public class DisSumMaster{
    public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException, InterruptedException {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 0);
        String tempR = "127.0.0.1";
        if (args.length >= 1){
            registry = LocateRegistry.getRegistry(args[0], 0);
            tempR = args[0];
        }
        System.out.println("Preparing remotes");
        // Formatear la url del registro
        String registro ="rmi://" + tempR + "/MessengerRMI";

        // Buscar el servicio en el registro.
        Remote servicioRemoto = Naming.lookup(registro);

        // Convertir a un interfaz
        MessageAPI servicioMensaje = (MessageAPI) servicioRemoto;
        // Encender la bombilla
        System.out.println("Preparing Queue");
        servicioMensaje.MsgQ_CreateTopic("Work", null);
        while(true){
            sleep(100);
            servicioMensaje.MsgQ_Publish("Work", "Do this", 0);
        }
    }

}
