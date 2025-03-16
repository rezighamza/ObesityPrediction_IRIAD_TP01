package client;

import common.InterfaceRMI;
import java.rmi.*;
import java.util.Scanner;

public class ClientEtablissement {

    public static void main(String[] args) {
        try {
            InterfaceRMI stub = (InterfaceRMI) Naming.lookup("rmi://localhost/ObesityPrediction");
            ClientService clientService = new ClientService(stub);
            Scanner sc = new Scanner(System.in);
            while (true) {
                boolean modelReady = false;
                try {
                    String status = stub.getServerStatus();
                    //System.out.println("\nStatut Serveur: " + status);
                    if (status.contains("Modèle entraîné: true")) {
                        modelReady = true;
                    }
                } catch (Exception e) {
                    System.out.println("Impossible de contacter le serveur pour le statut.");
                    e.printStackTrace();
                }

                // Affichage dynamique du menu
                System.out.println("\n--- Menu ---");
                System.out.println("1. Ajouter patients via CSV");
                if (modelReady) {
                    System.out.println("2. Prédire obésité");
                    System.out.println("3. Consulter statut serveur");
                    System.out.println("4. Quitter");
                } else {
                    System.out.println("2. Consulter statut serveur");
                    System.out.println("3. Quitter");
                }

                System.out.print("Choix: ");
                String input = sc.nextLine();
                int choix;
                try {
                    choix = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Veuillez entrer un nombre valide.");
                    continue;
                }


                if (modelReady) {
                    switch (choix) {
                        case 1:
                            System.out.print("Entrez le chemin du fichier CSV: ");
                            String path = sc.nextLine();
                            clientService.ajouterPatientsCSV(path);
                            break;
                        case 2:
                            clientService.predirePatient();
                            break;
                        case 3:
                            clientService.consulterStatut();
                            break;
                        case 4:
                            System.exit(0);
                        default:
                            System.out.println("Choix invalide.");
                    }
                } else {
                    switch (choix) {
                        case 1:
                            System.out.print("Entrez le chemin du fichier CSV: ");
                            String path = sc.nextLine();
                            clientService.ajouterPatientsCSV(path);
                            break;
                        case 2:
                            clientService.consulterStatut();
                            break;
                        case 3:
                            System.exit(0);
                        default:
                            System.out.println("Choix invalide.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
