package client;

import common.Patient;

import java.util.Scanner;

public class Helpers {

    public Helpers() {
    }

    public static Patient inputPatientFromConsole() {
        Scanner sc = new Scanner(System.in);

        // Gender
        String gender = "";
        while (true) {
            System.out.print("Genre: (1) Male   (2) Female\nVotre choix: ");
            String input = sc.nextLine();
            if (input.equals("1")) {
                gender = "Male";
                break;
            } else if (input.equals("2")) {
                gender = "Female";
                break;
            } else {
                System.out.println("Entrée invalide. Veuillez taper 1 pour Male ou 2 pour Female.");
            }
        }

        // Age
        int age = getIntInput(sc, "Âge (en années, ex: 25): ", 0, 120);

        // Height
        double height = getDoubleInput(sc, "Taille (en mètres, ex: 1.75): ", 0.5, 2.5);

        // Weight
        double weight = getDoubleInput(sc, "Poids (en kg, ex: 70): ", 10, 300);

        // Family History
        String familyHistory = getYesNoInput(sc, "Antécédents familiaux d'obésité? (yes/no): ");

        // FAVC
        String favc = getYesNoInput(sc, "Consommation fréquente d'aliments riches en calories? (yes/no): ");

        // FCVC
        int fcvc = getIntInput(sc, "Fréquence de consommation de légumes (1 - Faible à 3 - Élevée): ", 1, 3);

        // NCP
        int ncp = getIntInput(sc, "Nombre de repas principaux par jour (1-4): ", 1, 4);

        // CAEC
        String caec = getChoiceInput(sc, "Consommation entre les repas", new String[]{"never", "sometimes", "frequently", "always"});

        // Smoke
        String smoke = getYesNoInput(sc, "Fumez-vous? (yes/no): ");

        // CH2O
        double ch2o = getDoubleInput(sc, "Consommation d'eau quotidienne (en litres, ex: 2.0): ", 0, 10);

        // SCC
        String scc = getYesNoInput(sc, "Surveillez-vous les calories consommées? (yes/no): ");

        // FAF
        int faf = getIntInput(sc, "Activité physique hebdomadaire (en heures, ex: 0-3): ", 0, 24);

        // TUE
        int tue = getIntInput(sc, "Temps quotidien devant un écran (heures, ex: 0-12): ", 0, 24);

        // CALC
        String calc = getChoiceInput(sc, "Fréquence de consommation d'alcool", new String[]{"never", "sometimes", "frequently", "always"});

        // MTRANS
        String mtrans = getChoiceInput(sc, "Moyen de transport", new String[]{"Walking", "Bike", "Motorbike", "Public_Transportation", "Automobile"});

        // Create Patient object
        return new Patient(gender, age, height, weight, familyHistory, favc, fcvc, ncp, caec, smoke, ch2o, scc, faf, tue, calc, mtrans, "");

    }
    static int getIntInput(Scanner sc, String message, int min, int max) {
        int value;
        while (true) {
            try {
                System.out.print(message);
                value = Integer.parseInt(sc.nextLine());
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("Veuillez entrer un entier entre " + min + " et " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide. Veuillez entrer un entier.");
            }
        }
    }

    static double getDoubleInput(Scanner sc, String message, double min, double max) {
        double value;
        while (true) {
            try {
                System.out.print(message);
                value = Double.parseDouble(sc.nextLine());
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("Veuillez entrer un nombre entre " + min + " et " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide. Veuillez entrer un nombre décimal.");
            }
        }
    }

    static String getYesNoInput(Scanner sc, String message) {
        String input;
        while (true) {
            System.out.print(message);
            input = sc.nextLine().trim().toLowerCase();
            if (input.equals("yes") || input.equals("no")) {
                return input;
            } else {
                System.out.println("Veuillez répondre par 'yes' ou 'no'.");
            }
        }
    }

    static String getChoiceInput(Scanner sc, String fieldName, String[] choices) {
        String input;
        System.out.println(fieldName + " (Choix possibles): ");
        for (String choice : choices) {
            System.out.println(" - " + choice);
        }
        while (true) {
            System.out.print("Votre choix: ");
            input = sc.nextLine().trim();
            for (String choice : choices) {
                if (input.equalsIgnoreCase(choice)) {
                    return choice;
                }
            }
            System.out.println("Choix invalide. Veuillez choisir parmi les options listées.");
        }
    }

}
