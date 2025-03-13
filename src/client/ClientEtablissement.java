package client;
import common.InterfaceRMI;
import common.Patient;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;

public class ClientEtablissement {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            InterfaceRMI stub = (InterfaceRMI) registry.lookup("ObesityPredictionService");

            ClientService clientService = new ClientService(stub);
            System.out.println("Client Etablissement en cours d'exécution...");
            clientService.envoyerDonneesPatient();
            System.out.println("Données envoyées avec succès.");
            Patient patient = new Patient(30, 80, 1.75, "M", Arrays.asList("FAVC", "FCVC"));

            clientService.predireObesite(patient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
