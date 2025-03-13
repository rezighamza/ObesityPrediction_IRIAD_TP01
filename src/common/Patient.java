package common;
import java.io.Serializable;
import java.util.List;

public class Patient implements Serializable {
    private int age;
    private double poids;
    private double taille;
    private String genre;
    private List<String> habitudesAlimentaires;

    public Patient(int age, double poids, double taille, String genre, List<String> habitudesAlimentaires) {
        this.age = age;
        this.poids = poids;
        this.taille = taille;
        this.genre = genre;
        this.habitudesAlimentaires = habitudesAlimentaires;
    }

    public double getIMC() {
        return poids / (taille * taille);
    }

    public List<String> getHabitudesAlimentaires() {
        return habitudesAlimentaires;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "age=" + age +
                ", poids=" + poids +
                ", taille=" + taille +
                ", genre='" + genre + '\'' +
                ", habitudesAlimentaires=" + habitudesAlimentaires +
                '}';
    }
    public int getAge() { return age; }
    public double getPoids() { return poids; }
    public double getTaille() { return taille; }
}
