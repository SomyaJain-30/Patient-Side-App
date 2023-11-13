package com.example.btp_prop1;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Doctor {
    int image = R.drawable.doctor_image;
    private String about;
    private String clinicAddress;
    private String emailAddress;

    private String contact;
    private String education;
    private String experience;
    private String gender;
    private List<String> languages;
    private String name;
    private String profileUrl;
    private String role;
    private Map<String, List<String>> slots;
    private List<String> specialization;
    private List<Integer> surveyData;

    public Doctor(int image, String name, String speciality) {
        this.image = image;
        this.name = name;
        this.specialization = Arrays.asList(speciality.split(","));
    }

    public Doctor() {

    }
    public Doctor(String about, String clinicAddress, String emailAddress, String contactNumber, String education, String experience,
                  String gender, List<String> languages, String name, String profileUrl, String role,
                  Map<String, List<String>> slots, List<String> specialization, List<Integer> surveyData) {
        this.contact = contactNumber;
        image = R.drawable.doctor_image;
        this.about = about;
        this.clinicAddress = clinicAddress;
        this.emailAddress = emailAddress;
        this.education = education;
        this.experience = experience;
        this.gender = gender;
        this.languages = languages;
        this.name = name;
        this.profileUrl = profileUrl;
        this.role = role;
        this.slots = slots;
        this.specialization = specialization;
        this.surveyData = surveyData;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public String getSpeciality() {
        return String.join(", ", specialization);
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Map<String, List<String>> getSlots() {
        return slots;
    }

    public void setSlots(Map<String, List<String>> slots) {
        this.slots = slots;
    }

    public List<String> getSpecialization() {
        return specialization;
    }

    public void setSpecialization(List<String> specialization) {
        this.specialization = specialization;
    }

    public List<Integer> getSurveyData() {
        return surveyData;
    }

    public void setSurveyData(List<Integer> surveyData) {
        this.surveyData = surveyData;
    }
}
