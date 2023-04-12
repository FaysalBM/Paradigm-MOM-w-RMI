import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static java.lang.System.exit;
import static java.lang.Thread.sleep;

public class DisSumWorker implements TopicListenerInterface{
    static MessageAPI servicioMensaje;
    int totalWork = 0;
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
        servicioMensaje = (MessageAPI) servicioRemoto;
        DisSumWorker monitor = new DisSumWorker();

        // Exportar el objeto de la clase de la implementación al stub del interfase.
        TopicListenerInterface emonitor = (TopicListenerInterface) UnicastRemoteObject.exportObject(monitor, 0);
        servicioMensaje.MsgQ_Subscribe("Work", emonitor);
        servicioMensaje.MsgQ_Subscribe("Log", emonitor);
        System.out.println("Client es subscriu a work");
        while(true){

        }
    }

    @Override
    public void onTopicMessage(String message) throws MalformedURLException, RemoteException {
        System.out.println("Calculating the range " + message);
        // Split the string based on the '-' delimiter
        String[] parts = message.split("-");
        // Parse the substrings to integers
        int firstNumber = Integer.parseInt(parts[0]);
        int secondNumber = Integer.parseInt(parts[1]);
        long result = SumatorioMPrimos.calcularSumaPrimos(firstNumber, secondNumber);
        servicioMensaje.MsgQ_SendMessage("Results", String.valueOf(result), 0);
        System.out.println("Client envia resultat a Results");
        totalWork++;
    }

    @Override
    public void onTopicClose(String topic) throws InterruptedException {
        System.out.println("The topic queue " + topic + "has been closed");
        System.out.println("Total Work done: "+ totalWork);
    }
}
