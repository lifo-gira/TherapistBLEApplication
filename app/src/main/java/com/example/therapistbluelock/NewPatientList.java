package com.example.therapistbluelock;

public class NewPatientList {
    private String name;
    private int image;
    private String id,patid;

    public NewPatientList(String name, int image, String id, String patid) {
        this.name = name;
        this.image = image;
        this.id = id;
        this.patid = patid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatid() {
        return patid;
    }

    public void setPatid(String patid) {
        this.patid = patid;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public String getId() {
        return id;
    }
}
