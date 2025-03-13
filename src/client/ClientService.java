package client;
import common.InterfaceRMI;
import common.Patient;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.rmi.*;

public class ClientService {
    private final InterfaceRMI stub;

    public ClientService(InterfaceRMI stub) {
        this.stub = stub;
    }

    public void envoyerDonneesPatient() {
        try {
            for(int i = 0; i < 5; i++) {
                Patient patient = new Patient(20 + i, 80, 1.75, "M", Arrays.asList("FAVC", "FCVC"));
                stub.envoyerDonneesPatient(patient);
                Thread.sleep(1000);
            }
        } catch (RemoteException e) {
            System.err.println("Erreur lors de l'envoi des données: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }




    public void predireObesite(Patient p) {
        try {
            String prediction = stub.predireObesite(p);
            System.out.println("Résultat de la prédiction: " + prediction);
        } catch (RemoteException e) {
            System.err.println("Erreur lors de la prédiction: " + e.getMessage());
        }
    }


}
