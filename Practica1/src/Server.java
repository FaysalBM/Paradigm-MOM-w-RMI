import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    public static void main(String args[])
    {
        System.out.println("Cargando Servicio RMI");

        try
        {
            // Cargar el servicio.
            MessageAPIImpl servicioBombilla = new MessageAPIImpl();

            // Exportar el objeto de la clase de la implementación al stub del interfase.
            MessageAPI bombilla = (MessageAPI) UnicastRemoteObject.exportObject(servicioBombilla, 0);

            // Enlazar el objeto remoto (stub) con el registro de RMI.
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("MessagingAPI", bombilla);
            System.err.println("Server ready");

            // Create a thread, and pass the sensor server.
            // This will activate the run() method, and trigger
            // regular temperature changes.
            Thread thread = new Thread ((Runnable) servicioBombilla);
            thread.start();
        }
        catch (RemoteException re)
        {
            System.err.println("Remote Error - " + re);
        }
        catch (Exception e)
        {
            System.err.println("Error - " + e);
        }
    }

}

