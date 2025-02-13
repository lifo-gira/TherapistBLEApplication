package com.example.therapistbluelock;

public class AssignedPatientList {

    private String name;
    private String issue;
    private String id;
    private int imageResource; // For the patient image
    private int age;
    private String gender;
    private String patientid;

    // Constructor
    public AssignedPatientList(String name, String issue, String id, int imageResource, int age, String gender,String patientid) {
        this.name = name;
        this.issue = issue;
        this.id = id;
        this.imageResource = imageResource;
        this.age = age;
        this.gender = gender;
        this.patientid = patientid;
    }

    public String getPatientid() {
        return patientid;
    }

    public void setPatientid(String patientid) {
        this.patientid = patientid;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
