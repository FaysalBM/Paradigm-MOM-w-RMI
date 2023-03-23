import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    public static void main(String args[])
    {
        System.out.println("Cargando Servicio RMI");

        try
        {
            System.out.println("Carregant elements RMI");
            // Cargar el servicio.
            MessageAPIImpl servicioMensajes = new MessageAPIImpl();

            // Exportar el objeto de la clase de la implementación al stub del interfase.
            MessageAPI apiMess = (MessageAPI) UnicastRemoteObject.exportObject(servicioMensajes, 0);

            Registry registry = LocateRegistry.getRegistry("127.0.0.1", 0);
            String tempR = "127.0.0.1";
            if (args.length >= 1){
                registry = LocateRegistry.getRegistry(args[0], 0);
                tempR = args[0];
            }


            // Crear la URL del registro.
            String registro ="rmi://" + tempR + "/MessengerRMI";
            // Registrar el servicio
            Naming.rebind(registro, apiMess);
            System.err.println("Server ready");

        }
        catch (RemoteException re)
        {
            System.err.println("Remote Error - " + re);
        }
        catch (Exception e)
        {
            System.err.println("Client side error - " + e);
        }
    }

}

