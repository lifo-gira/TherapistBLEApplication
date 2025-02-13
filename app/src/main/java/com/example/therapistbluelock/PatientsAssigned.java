package com.example.therapistbluelock;

public class PatientsAssigned {
    private String name;
    private String diagnosis;
    private String id;
    private int imageResource;
    private int age; // Add age
    private String gender; // Add gender
    private String patientid;

    public String getPatientid() {
        return patientid;
    }

    public void setPatientid(String patientid) {
        this.patientid = patientid;
    }

    // Constructor with age and gender
    public PatientsAssigned(String name, String diagnosis, String id, int imageResource, int age, String gender, String patientid) {
        this.name = name;
        this.diagnosis = diagnosis;
        this.id = id;
        this.imageResource = imageResource;
        this.age = age; // Set age
        this.gender = gender; // Set gender
        this.patientid = patientid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getId() {
        return id;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getAge() {
        return age; // Return age
    }

    public String getGender() {
        return gender; // Return gender
    }
}
