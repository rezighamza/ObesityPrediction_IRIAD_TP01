package server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ServeurObesite {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            ServeurService service = new ServeurService();
            Naming.rebind("rmi://localhost/ObesityPrediction", service);
            System.out.println("Serveur prêt.");
            // Keep server running
            try {
                Thread.sleep(Long.MAX_VALUE); // Sleep forever
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
