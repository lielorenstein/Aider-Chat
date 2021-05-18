package com.example.aiderchat_proj.data;


import androidx.lifecycle.LiveData;

import com.example.aiderchat_proj.classes.BasicUser;
import com.google.firebase.auth.FirebaseUser;

public class RegisterRepository {
    private static BasicUserDataSource userDataSource;
    private static RegisterRepository instance;

    public static RegisterRepository getInstance(){
        if (instance == null){
            instance = new RegisterRepository();
        }
        return instance;
    }

    private RegisterRepository(){
        userDataSource = BasicUserDataSource.getInstance();
    }

    public String addUser(BasicUser user) {
        return userDataSource.addUser(user);
    }

    public void registerUser(String email, String pass) {
        userDataSource.registerUser(email, pass);
    }

    public LiveData<Boolean> getIsSuccessAdd() {
        return userDataSource.getIsSuccessAdd();
    }

    public LiveData<Boolean> getIsSuccessAuth() {
        return userDataSource.getIsSuccessAuth();
    }

    public LiveData<String> getNewExceptionMessage() {
        return userDataSource.getNewExceptionMessage();
    }

    public FirebaseUser getCurrentUser(){
        return userDataSource.getCurrentUser();
    }


    public void removeUser(BasicUser user) {
        userDataSource.removeUser(user.getId());
    }
}
