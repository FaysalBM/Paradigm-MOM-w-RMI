import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class DisSumMaster{
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
        servicioMensaje.MsgQ_CreateTopic("Work", null);
        servicioMensaje.MsgQ_CreateTopic("Results", null);
        try {
            tempSem.acquire();
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
                System.out.println("Fill : " + h + " " + StartIndex + " " + FinishIndex);
                int finalStartIndex = StartIndex;
                int finalFinishIndex = FinishIndex;
                String range = finalStartIndex + "-" + finalFinishIndex;
                servicioMensaje.MsgQ_Publish("Work", range, 0);
                control_start = control_end;
            }
        }catch (InterruptedException e) {
            // Handle InterruptedException
        }


    }

}
