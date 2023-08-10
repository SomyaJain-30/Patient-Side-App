package com.example.btp_prop1;

import java.util.List;
import java.util.Map;

public class Doctor {
    int image;
    private String name, speciality;
    private int experience;
    private String address;
    private String contact;
    private String emailAddress;
    private String education, gender;
    private Map<String, List<String>> slots;

    public Doctor(int image, String name, String speciality){
        this.image= image;
        this.name= name;
        this.speciality=speciality;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Map<String, List<String>> getSlots() {
        return slots;
    }

    public void setSlots(Map<String, List<String>> slots) {
        this.slots = slots;
    }
}
