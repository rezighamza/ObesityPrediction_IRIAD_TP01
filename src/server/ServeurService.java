package server;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.LocateRegistry;
import java.util.*;
import common.InterfaceRMI;
import common.Patient;

public class ServeurService implements InterfaceRMI {
    private final List<Patient> patients;

    public ServeurService() {
        this.patients = new ArrayList<>();
    }

    private boolean trainingComplete = false;
    private static final int N = 5;

    @Override
    public synchronized void envoyerDonneesPatient(Patient patient) throws RemoteException {
        patients.add(patient);
        System.out.println("Patient ajouté, Nombre total: " + patients.size());
        if (patients.size() >= N && !trainingComplete) {
            lancerEntrainement();
        }
    }

    private void lancerEntrainement() {
        System.out.println("Début de l'entraînement...");
        try {
            Thread.sleep(5000);
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
        return "Résultat de prédiction";
    }
}
