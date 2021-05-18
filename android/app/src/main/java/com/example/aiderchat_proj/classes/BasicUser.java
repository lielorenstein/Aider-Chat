package com.example.aiderchat_proj.classes;

import java.util.Date;
import java.util.List;

public class BasicUser {
    String id;
    String firstName;
    String lastName;
    Date birthDate;
    String emailAddress;
    Gender gender;
    String location;
    List<EnumChannel> channels;
    //שדכשדכdsfdsf
    public BasicUser(){};


    public BasicUser(String id, String firstName, String lastName, Date birthDate,
                     String emailAddress, Gender gender, String location,
                     List<EnumChannel> channels){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.emailAddress = emailAddress;
        this.gender = gender;
        this.location = location;
        this.channels = channels;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<EnumChannel> getChannels() {
        return channels;
    }

    public void setChannels(List<EnumChannel> channels) {
        this.channels = channels;
    }


    public enum Gender {
        mail(0), female(1), other(2);
        private final Integer code;
        Gender(Integer value) {
            this.code = value;
        }
        public Integer getCode() {
            return code;
        }
    }


    public enum EnumChannel {
        kid(0), girl(1), old_man(2);
        private final Integer code;
        EnumChannel(Integer value) {
            this.code = value;
        }

        public Integer getCode() {
            return code;
        }
    }
}
