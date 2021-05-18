package com.example.aiderchat_proj.ui;

import android.app.Application;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.aiderchat_proj.data.RegisterRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class RegisterViewModel extends ViewModel {
    private final String TAG = "RegisterViewModel";
    public FormRegister form;
    private RegisterRepository repository;


    public RegisterViewModel(@NonNull Context context) {
        form = new FormRegister(context);
        repository = RegisterRepository.getInstance();
    }

    public void done(){
        if(form.isLocationValid(true)){
            registerUser();
        }
    }

    public void addUser(){
        form.getUser().setId(repository.addUser(form.getUser()));
    }

    public void registerUser(){
        repository.registerUser(form.getUser().getEmailAddress(), form.getPassword());
    }

    public LiveData<Boolean> getIsSuccessAdd() {
        return repository.getIsSuccessAdd();
    }
    public LiveData<Boolean> getIsSuccessAuth() {
        return repository.getIsSuccessAuth();
    }
    public LiveData<String> getNewExceptionMessage() {
        return repository.getNewExceptionMessage();
    }


    public FirebaseUser getCurrentUser(){
        return repository.getCurrentUser();
    }

    public void removeUser() {
        repository.removeUser(form.getUser());
    }
}

