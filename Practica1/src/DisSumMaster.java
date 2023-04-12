import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class DisSumMaster{
    public int totalResult = 0;
    public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException, InterruptedException {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 0);
        String tempR = "127.0.0.1";
        if (args.length >= 1){
            registry = LocateRegistry.getRegistry(args[2], 0);
            tempR = args[2];
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
        int numWorkers = Integer.parseInt(args[1]);
        Semaphore tempSem = servicioMensaje.getSemaphore(numWorkers);
        servicioMensaje.MsgQ_CreateTopic("Work", "RR");
        servicioMensaje.MsgQ_CreateQueue("Results");
        System.out.println("Result expected: " + SumatorioMPrimos.calcularSumaPrimos(0, Long.parseLong(args[0])));
        System.out.println("Waiting for the semaphore");
        servicioMensaje.catchSem();
        System.out.println("Semaphore released");
        int calculate = Integer.parseInt(args[0]);
        int control_start=0;
        int control_end=0;
        int MaxIndex = (calculate);
        int NumIndexes = (MaxIndex / numWorkers);
        for (int h = 0; h < numWorkers; h++) {
            int StartIndex, FinishIndex;
            StartIndex = (int) ((NumIndexes * h) + control_start);
            FinishIndex = (int) ((NumIndexes * (h + 1)) + control_end);
            if ((MaxIndex % numWorkers) > h) {
                control_end++;
                FinishIndex += control_end;
            }
            int finalStartIndex = StartIndex;
            int finalFinishIndex = FinishIndex;
            String range = finalStartIndex + "-" + finalFinishIndex;
            servicioMensaje.MsgQ_Publish("Work", range, 0);
            System.out.println("Message Published");
            control_start = control_end;
        }
        System.out.println("Messages Published");
        Vector<Integer> received = new Vector<>();
        int result = 0;
        int cont = 0;
        while(cont != numWorkers){
            String x = servicioMensaje.MsgQ_ReceiveMessage("Results", 0);
            if(x != null){
                cont++;
                if(!received.contains(Integer.parseInt(x))){
                    received.add(Integer.parseInt(x));
                    result += Integer.parseInt(x);
                }
            }
        }
        System.out.println("Result total received: " + result);
        servicioMensaje.MsgQ_CloseQueue("Results");
        System.out.println("Results Queue closed!");
        servicioMensaje.MsgQ_CloseTopic("Work");
        System.out.println("Work Topic queue closed!");
        System.out.println("Queue and Topic closed");
    }

}
