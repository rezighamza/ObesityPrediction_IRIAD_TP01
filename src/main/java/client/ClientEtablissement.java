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
            String clientID = "";
            while (clientID.equals("")) {
                System.out.print("Veuillez entrer votre ID de centre de santé: ");
                clientID = sc.nextLine();
                clientService.setClientID(clientID);
            }
            while (true) {
                boolean modelReady = false;
                try {
                    String status = stub.getServerStatus();
                    if (status.contains("Modèle entraîné: true")) {
                        modelReady = true;
                    }
                } catch (Exception e) {
                    System.out.println("Impossible de contacter le serveur pour le statut.");
                    e.printStackTrace();
                }

                System.out.println("\n--- Menu ---");
                if (modelReady) {
                    System.out.println("1. Ajouter un patient & Prédire obésité");
                    System.out.println("2. Prédire obésité via CSV");
                    System.out.println("3. Supprimer un patient");
                    System.out.println("4. Modifier un patient");
                    System.out.println("5. Lister mes patients");
                    System.out.println("6. Consulter statut serveur");
                    System.out.println("7. Quitter");
                } else {
                    System.out.println("1. Ajouter patients via CSV");
                    System.out.println("2. Supprimer un patient");
                    System.out.println("3. Modifier un patient");
                    System.out.println("4. Lister mes patients");
                    System.out.println("5. Consulter statut serveur");
                    System.out.println("6. Quitter");
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
                            clientService.predirePatient();
                            break;
                        case 2:
                            System.out.print("Entrez le chemin du fichier CSV d'entrée: ");
                            String inputCsv = sc.nextLine();
                            System.out.print("Entrez le chemin du fichier CSV de sortie: ");
                            String outputCsv = sc.nextLine();
                            clientService.predirePatientsCSV(inputCsv, outputCsv);
                            break;
                        case 3:
                            clientService.deletePatient();
                            break;
                        case 4:
                            clientService.modifyPatient();
                            break;
                        case 5:
                            clientService.listPatients();
                            break;
                        case 6:
                            clientService.consulterStatut();
                            break;
                        case 7:
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
                            clientService.deletePatient();
                            break;
                        case 3:
                            clientService.modifyPatient();
                            break;
                        case 4:
                            clientService.listPatients();
                            break;
                        case 5:
                            clientService.consulterStatut();
                            break;
                        case 6:
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
