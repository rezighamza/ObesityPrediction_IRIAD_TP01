package server;

import common.InterfaceRMI;
import common.Patient;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ServeurService extends UnicastRemoteObject implements InterfaceRMI {

    private List<Patient> patients = new ArrayList<>();
    private boolean trainingComplete = false;
    private static final int N = 5;

    public ServeurService() throws RemoteException {
        super();
    }

    @Override
    public synchronized void envoyerDonneesPatient(Patient patient) throws RemoteException {
        patients.add(patient);
        System.out.println("Patient ajouté: " + patient.getGender() + ", Age: " + patient.getAge() + ". Nombre total: " + patients.size());
        if (patients.size() >= N && !trainingComplete) {
            lancerEntrainement();
        }
    }

    private void lancerEntrainement() {
        System.out.println("Début de l'entraînement...");
        try {
            Thread.sleep(5000);  // Simule l'entraînement
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        trainingComplete = true;
        System.out.println("Entraînement terminé. Prédictions activées.");
    }

    @Override
    public String predireObesite(Patient patient) throws RemoteException {
        if (!trainingComplete) {
            return "Erreur: Modèle non entraîné. Veuillez réessayer plus tard.";
        }
        return "Résultat de prédiction (simulé) pour patient âge " + patient.getAge() + ": " + patient.getObesityLevel();
    }

    @Override
    public String getServerStatus() throws RemoteException {
        return "Nombre de patients: " + patients.size() + ", Modèle entraîné: " + trainingComplete;
    }
}
