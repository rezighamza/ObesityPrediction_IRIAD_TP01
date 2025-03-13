package model;
import java.util.List;

public class ModeleIA {
    private Object modele; // Placeholder for actual AI model

    public void chargerData(List<Object> data) {
        System.out.println("Chargement des données...");
    }

    public void trainModele() {
        System.out.println("Entraînement du modèle...");
    }

    public void testModele() {
        System.out.println("Test du modèle...");
    }

    public String prédire(Object input) {
        return "Prédiction (dummy): Obésité probable";
    }
}
