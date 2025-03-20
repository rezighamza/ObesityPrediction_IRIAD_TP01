package common;

import java.io.Serializable;

public class Patient implements Serializable {
    private String gender;
    private int age;
    private double height;
    private double weight;
    private String familyHistory;
    private String favc;
    private int fcvc;
    private int ncp;
    private String caec;
    private String smoke;
    private double ch2o;
    private String scc;
    private int faf;
    private int tue;
    private String calc;
    private String mtrans;
    private String obesityLevel;

    public Patient(String gender, int age, double height, double weight, String familyHistory, String favc, int fcvc, int ncp,
                   String caec, String smoke, double ch2o, String scc, int faf, int tue, String calc, String mtrans, String obesityLevel) {
        this.gender = gender;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.familyHistory = familyHistory;
        this.favc = favc;
        this.fcvc = fcvc;
        this.ncp = ncp;
        this.caec = caec;
        this.smoke = smoke;
        this.ch2o = ch2o;
        this.scc = scc;
        this.faf = faf;
        this.tue = tue;
        this.calc = calc;
        this.mtrans = mtrans;
        this.obesityLevel = obesityLevel;
    }

    // Getters
    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public String getObesityLevel() {
        return obesityLevel;
    }

    public String getFamilyHistory() {
        return familyHistory;
    }

    public String getFavc() {
        return favc;
    }

    public int getFcvc() {
        return fcvc;
    }

    public int getNcp() {
        return ncp;
    }

    public String getCaec() {
        return caec;
    }

    public String getSmoke() {
        return smoke;
    }

    public double getCh2o() {
        return ch2o;
    }

    public String getScc() {
        return scc;
    }

    public int getFaf() {
        return faf;
    }

    public int getTue() {
        return tue;
    }

    public String getCalc() {
        return calc;
    }

    public String getMtrans() {
        return mtrans;
    }


    public void getDetails() {
        System.out.println("Patient data:" + "\n" +
                "gender : " + this.gender + "\n" +
                "age : " + this.age + "\n" +
                "height : " + this.height + "\n" +
                "weight : " + this.weight + "\n" +
                "familyHistory : " + this.familyHistory + "\n" +
                "favc : " + this.favc + "\n" +
                "fcvc : " + this.fcvc + "\n" +
                "ncp : " + this.ncp + "\n" +
                "caec : " + this.caec + "\n" +
                "smoke : " + this.smoke + "\n" +
                "ch2o : " + this.ch2o + "\n" +
                "scc : " + this.scc + "\n" +
                "faf : " + this.faf + "\n" +
                "tue : " + this.tue + "\n" +
                "calc : " + this.calc + "\n" +
                "mtrans : " + this.mtrans + "\n" +
                "obesityLevel : " + this.obesityLevel + "\n"
        );
    }

}