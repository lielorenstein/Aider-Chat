package com.example.aiderchat_proj.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.aiderchat_proj.classes.BasicUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BasicUserDataSource {
    private MutableLiveData<Boolean> isSuccessAuth= new MutableLiveData<>();
    private MutableLiveData<Boolean> isSuccessAdd= new MutableLiveData<>();
    private MutableLiveData<String> newExceptionMessage= new MutableLiveData<>();
    public MutableLiveData<Boolean> getIsSuccessAuth() {
        return isSuccessAuth;
    }
    public MutableLiveData<Boolean> getIsSuccessAdd() { return isSuccessAdd; }
    public MutableLiveData<String> getNewExceptionMessage() {
        return newExceptionMessage;
    }



    public void removeUser(String id) {
        users.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("UsersDataSource", "removed from database");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("UsersDataSource", "fail to remove from database");
            }
        });
        getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("UsersDataSource", "removed from authentication");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("UsersDataSource", "failed to remove from authentication");
            }
        });
    }


    public interface changedListener {
        void change();
    }

    private changedListener listener;

    public void setChangedListener(changedListener l) {
        listener = l;
    }

    public FirebaseUser getCurrentUser(){
        return mAuth.getCurrentUser();
    }

    public List<BasicUser> getUsersList() {
        return usersList;
    }

    List<BasicUser> usersList;
    DatabaseReference users;
    private FirebaseAuth mAuth;
    private BasicUserDataSource() {
        usersList = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        users = firebaseDatabase.getReference("Existingusers");
        mAuth = FirebaseAuth.getInstance();
    }

    private static BasicUserDataSource instance;

    public static BasicUserDataSource getInstance() {
        if (instance == null)
            instance = new BasicUserDataSource();
        return instance;
    }


    public String addUser(BasicUser p) {
        String id = users.push().getKey();
        p.setId(id);
        users.child(id).setValue(p).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                isSuccessAdd.setValue(Boolean.TRUE);
                usersList.add(p);
                Log.i("UsersDataSource", "success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isSuccessAdd.setValue(Boolean.FALSE);
                newExceptionMessage.setValue(e.getMessage());
                Log.i("UsersDataSource", "fail");
            }
        });
        return id;
    }


    public void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i("UsersDataSource", "success");
                            isSuccessAuth.setValue(Boolean.TRUE);
                        } else {
                            Log.e("UsersDataSource","createAccount: Fail!", task.getException());
                            isSuccessAuth.setValue(Boolean.FALSE);
                            newExceptionMessage.setValue(Objects.requireNonNull(task.getException()).getMessage());
                        }
                    }
                });
    }

}
