package com.example.nomis.androidpokedex;

public class Classification {

    private String classification;
    private int id;

    public Classification(){

    }

    public Classification(int id, String classification){
        this.id = id;
        this.classification = classification;
    }

    public int getId() {
        return id;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public void setId(int id) {
        this.id = id;
    }
}
