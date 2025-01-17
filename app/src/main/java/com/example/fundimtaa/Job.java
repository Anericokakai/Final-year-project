package com.example.fundimtaa;
public class Job {
    private String jobId;
    private String clientId;
    private String documentId;
    private String jobName;
    private String jobStartDate;

    private String minExperience;
    private  String location;
    private String price;
   private String jobDescription;
   private  boolean isCompleted;
    // Default constructor required by Firestore
    public Job() {
        // Default constructor is necessary for Firestore
    }

    public Job(String jobId,String clientId,String documentId,String jobName,
               String jobStartDate, String minExperience,
               String location, String price,String jobDescription,boolean isCompleted) {
        this.jobId = jobId;
        this.clientId = clientId;
        this.documentId= documentId;
        this.jobName = jobName;
        this.jobStartDate = jobStartDate;
        this.minExperience = minExperience;
        this.location = location;
        this.price = price;
        this.jobDescription = jobDescription;
        this.isCompleted = isCompleted;
    }

    // Getter methods
    public String getJobId() {
        return jobId;
    }
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getClientId(){return clientId;}
    public String getJobName() {
        return jobName;
    }

    public String getJobStartDate() {
        return jobStartDate;
    }

    public String getMinExperience() {
        return minExperience;
    }

    public void setMinExperience(String minExperience) {
        this.minExperience = minExperience;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
public void setClientId(String clientId){
this.clientId = clientId;}
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}

