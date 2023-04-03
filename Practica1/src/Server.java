import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;

public class Server extends MessageAPIImpl{
    protected Server() throws RemoteException {
    }

    public static void main(String args[])
    {
        System.out.println("Cargando Servicio RMI");

        try
        {
            System.out.println("Carregant elements RMI");
            // Cargar el servicio.
            MessageAPIImpl servicioMensajes = new MessageAPIImpl();

            servicioMensajes.MsqQ_Init("127.0.0.1");

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

