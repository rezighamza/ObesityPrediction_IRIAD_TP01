package server;

import common.InterfaceRMI;
import common.Patient;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.ModeleIA;
import java.io.File;
import java.util.Map;


public class ServeurService extends UnicastRemoteObject implements InterfaceRMI {
    // On server side
    // Key: clientID_patientCount (e.g., "client1_0", "client1_1")
    Map<String, Patient> patientMap = new HashMap<>();

    // Also maintain how many patients each client has added
    Map<String, Integer> clientPatientCount = new HashMap<>();

    private List<Patient> patients = new ArrayList<>();
    private ModeleIA modeleIA = new ModeleIA();
    private boolean trainingComplete = false;
    private static final int N = 2111;

    public ServeurService() throws RemoteException {
        super();
    }

    @Override
    public void checkExistingModel() throws RemoteException {
        String modelPath = "src/main/java/resources/model/xgboost_obesity_model.bin";
        File modelFile = new File(modelPath);
        if (modelFile.exists()) {
            try {
                modeleIA.loadModel(modelPath);
                trainingComplete = true;
                System.out.println("Modèle existant chargé.");
            } catch (Exception e) {
                System.out.println("Erreur lors du chargement du modèle existant.");
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void envoyerDonneesPatient(String clientID,Patient patient) throws RemoteException {
        patients.add(patient);
        String m = addPatient(clientID,patient);
        System.out.println(m+ ". Nombre total: " + patients.size());
        if (patients.size() >= N && !trainingComplete) {
            lancerEntrainement();
        }
    }

    private void lancerEntrainement() {
        System.out.println("Début de l'entraînement...");
        try {
            modeleIA.trainModele(patients);
            trainingComplete = true;
            System.out.println("Entraînement terminé. Prédictions activées.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String predireObesite(String clientID,Patient patient) throws RemoteException {
        if (!trainingComplete) {
            return "Erreur: Modèle non entraîné. Veuillez réessayer plus tard.";
        }
        // print patient data
        patient.getDetails();
        String m = addPatient(clientID,patient);
        return modeleIA.prédire(patient);
    }

    @Override
    public String getServerStatus() throws RemoteException {
        checkExistingModel();
        return "Nombre de patients: " + patients.size() + ", Modèle entraîné: " + trainingComplete;
    }
    @Override
    public synchronized String addPatient(String clientID, Patient p) {
        int count = clientPatientCount.getOrDefault(clientID, 0);
        String key = clientID + "_" + count;
        patientMap.put(key, p);
        clientPatientCount.put(clientID, count + 1);
        return "Patient ajouté avec ID: " + key;
    }

    @Override
    public synchronized String deletePatient(String clientID, int patientNumber) {
        String key = clientID + "_" + patientNumber;
        if (patientMap.containsKey(key)) {
            patientMap.remove(key);
            return "Patient " + key + " supprimé.";
        } else {
            return "Aucun patient trouvé avec l'ID: " + key;
        }
    }

    @Override
    public synchronized String modifyPatient(String clientID, int patientNumber, Patient updatedPatient) {
        String key = clientID + "_" + patientNumber;
        if (patientMap.containsKey(key)) {
            patientMap.put(key, updatedPatient);
            return "Patient " + key + " modifié.";
        } else {
            return "Aucun patient trouvé avec l'ID: " + key;
        }
    }

    @Override
    public synchronized List<String> listPatients(String clientID) {
        List<String> list = new ArrayList<>();
        for (String key : patientMap.keySet()) {
            if (key.startsWith(clientID + "_")) {
                Patient p = patientMap.get(key);
                StringBuilder sb = new StringBuilder();
                sb.append(key).append(":\n")
                        .append("Gender: ").append(p.getGender()).append("\n")
                        .append("Age: ").append(p.getAge()).append("\n")
                        .append("Height: ").append(p.getHeight()).append("\n")
                        .append("Weight: ").append(p.getWeight()).append("\n")
                        .append("Family History: ").append(p.getFamilyHistory()).append("\n")
                        .append("FAVC: ").append(p.getFavc()).append("\n")
                        .append("FCVC: ").append(p.getFcvc()).append("\n")
                        .append("NCP: ").append(p.getNcp()).append("\n")
                        .append("CAEC: ").append(p.getCaec()).append("\n")
                        .append("SMOKE: ").append(p.getSmoke()).append("\n")
                        .append("CH2O: ").append(p.getCh2o()).append("\n")
                        .append("SCC: ").append(p.getScc()).append("\n")
                        .append("FAF: ").append(p.getFaf()).append("\n")
                        .append("TUE: ").append(p.getTue()).append("\n")
                        .append("CALC: ").append(p.getCalc()).append("\n")
                        .append("MTRANS: ").append(p.getMtrans()).append("\n")
                        .append("Obesity Level: ").append(p.getObesityLevel()).append("\n");

                list.add(sb.toString());
            }
        }
        return list;
    }
}
