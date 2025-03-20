package client;

import common.InterfaceRMI;
import common.Patient;
import java.io.*;
import java.util.List;
import java.util.Scanner;


public class ClientService {

    private InterfaceRMI stub;
    private Helpers helper = new Helpers();
    private String clientID;

    public ClientService(InterfaceRMI stub) {
        this.stub = stub;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public void ajouterPatientsCSV(String csvPath) {
        try {
            System.out.println("Envoi des données en cours... (Veuillez patienter)");
            BufferedReader br = new BufferedReader(new FileReader(csvPath));
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                // skip the first line
                if (line.startsWith("Gender")) {
                    continue;
                }
                String[] tokens = line.split(",");
                if (tokens.length >= 17) {
                    // create a new patient object from the CSV data ( the csv data might not be in the correct format so we need to explicitly convert it to the correct data type)
                    Patient p = new Patient(
                            tokens[0],                                       // gender (String)
                            (int) Double.parseDouble(tokens[1]),             // age
                            Double.parseDouble(tokens[2]),                   // height
                            Double.parseDouble(tokens[3]),                   // weight
                            tokens[4], tokens[5],                            // familyHistory, favc
                            (int) Double.parseDouble(tokens[6]),             // fcvc
                            (int) Double.parseDouble(tokens[7]),             // ncp
                            tokens[8], tokens[9],                            // caec, smoke
                            Double.parseDouble(tokens[10]),                  // ch2o
                            tokens[11],                                      // scc
                            (int) Double.parseDouble(tokens[12]),            // faf
                            (int) Double.parseDouble(tokens[13]),            // tue
                            tokens[14], tokens[15], tokens[16]               // calc, mtrans, obesityLevel
                    );

                    stub.envoyerDonneesPatient(clientID,p);
                    count++;
                }
            }
            br.close();
            System.out.println("Envoi terminé. " + count + " patients ajoutés.");
        } catch (Exception e) {
            System.out.println("Erreur lors de la lecture du CSV.");
            e.printStackTrace();
        }
    }


    public void predirePatient() {
        try {
            Patient p = Helpers.inputPatientFromConsole();
            // Prediction
            String result = stub.predireObesite(clientID,p);
            System.out.println("Résultat: " + result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void predirePatientsCSV(String inputCsvPath, String outputCsvPath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputCsvPath));
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputCsvPath));
            String line;
            int count = 0;

            // Write CSV header in output
            bw.write("Gender,Age,Height,Weight,Prediction\n");

            while ((line = br.readLine()) != null) {
                if (line.startsWith("Gender")) {
                    continue; // Skip header
                }
                String[] tokens = line.split(",");
                if (tokens.length >= 16) {
                    Patient p = new Patient(
                            tokens[0],                                       // gender
                            (int) Double.parseDouble(tokens[1]),             // age
                            Double.parseDouble(tokens[2]),                   // height
                            Double.parseDouble(tokens[3]),                   // weight
                            tokens[4], tokens[5],
                            (int) Double.parseDouble(tokens[6]),
                            (int) Double.parseDouble(tokens[7]),
                            tokens[8], tokens[9],
                            Double.parseDouble(tokens[10]),
                            tokens[11],
                            (int) Double.parseDouble(tokens[12]),
                            (int) Double.parseDouble(tokens[13]),
                            tokens[14], tokens[15], "" // obesityLevel
                    );

                    // Send to server for prediction
                    String prediction = stub.predireObesite(clientID,p);
                    System.out.println("Patient " + (count + 1) + " → " + prediction);

                    // Write result to output CSV
                    bw.write(p.getGender() + "," + p.getAge() + "," + p.getHeight() + "," + p.getWeight() + "," + prediction + "\n");

                    count++;
                }
            }
            br.close();
            bw.close();
            System.out.println("✅ Prédictions terminées pour " + count + " patients. Résultats enregistrés dans: " + outputCsvPath);
        } catch (Exception e) {
            System.out.println("Erreur lors de la lecture ou prédiction.");
            e.printStackTrace();
        }
    }


    public void consulterStatut() {
        try {
            String status = stub.getServerStatus();
            System.out.println(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deletePatient() {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.print("Entrez le numéro du patient à supprimer: ");
            int num = Integer.parseInt(sc.nextLine());
            String response = stub.deletePatient(clientID, num);
            System.out.println(response);
        } catch (Exception e) {
            System.out.println("Erreur lors de la suppression du patient.");
            e.printStackTrace();
        }
    }

    public void modifyPatient() {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.print("Entrez le numéro du patient à modifier: ");
            int num = Integer.parseInt(sc.nextLine());
            Patient p = Helpers.inputPatientFromConsole();
            String response = stub.modifyPatient(clientID, num, p);
            System.out.println(response);
        } catch (Exception e) {
            System.out.println("Erreur lors de la modification du patient.");
            e.printStackTrace();
        }
    }

    public void listPatients() {
        try {
            List<String> patients = stub.listPatients(clientID);
            if (patients.isEmpty()) {
                System.out.println("Aucun patient trouvé.");
            } else {
                System.out.println("--- Liste de vos patients ---");
                for (String s : patients) {
                    System.out.println(s);
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération de la liste.");
            e.printStackTrace();
        }
    }
}