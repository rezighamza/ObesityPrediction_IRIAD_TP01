package server;
import common.InterfaceRMI;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServeurObesite {
    public static void main(String[] args) {
        try {
            ServeurService service = new ServeurService();
            InterfaceRMI stub = (InterfaceRMI) UnicastRemoteObject.exportObject(service, 0);

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("ObesityPredictionService", stub);

            System.out.println("Serveur Obésité en cours d'exécution...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
