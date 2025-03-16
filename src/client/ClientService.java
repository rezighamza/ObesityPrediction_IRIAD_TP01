package client;

import common.InterfaceRMI;
import common.Patient;
import java.io.*;
import java.util.Scanner;

public class ClientService {

    private InterfaceRMI stub;

    public ClientService(InterfaceRMI stub) {
        this.stub = stub;
    }

    public void ajouterPatientsCSV(String csvPath) {
        try {
            System.out.println("Envoi des données en cours... (Veuillez patienter)");
            BufferedReader br = new BufferedReader(new FileReader(csvPath));
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 17) {
                    Patient p = new Patient(
                            tokens[0],
                            Integer.parseInt(tokens[1]),
                            Double.parseDouble(tokens[2]),
                            Double.parseDouble(tokens[3]),
                            tokens[4], tokens[5],
                            Integer.parseInt(tokens[6]),
                            Integer.parseInt(tokens[7]),
                            tokens[8], tokens[9],
                            Double.parseDouble(tokens[10]),
                            tokens[11],
                            Integer.parseInt(tokens[12]),
                            Integer.parseInt(tokens[13]),
                            tokens[14], tokens[15], tokens[16]
                    );
                    stub.envoyerDonneesPatient(p);
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
            Scanner sc = new Scanner(System.in);
            System.out.print("Genre: ");
            String gender = sc.nextLine();
            System.out.print("Âge: ");
            int age = sc.nextInt();
            System.out.print("Taille: ");
            double height = sc.nextDouble();
            System.out.print("Poids: ");
            double weight = sc.nextDouble();
            sc.nextLine();
            Patient p = new Patient(gender, age, height, weight, "", "", 0, 0, "", "", 0.0, "", 0, 0, "", "", "");
            String result = stub.predireObesite(p);
            System.out.println("Résultat: " + result);
        } catch (Exception e) {
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
}