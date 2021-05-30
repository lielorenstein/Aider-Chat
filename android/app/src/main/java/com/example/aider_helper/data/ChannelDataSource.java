package com.example.aider_helper.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.aider_helper.classes.Channel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChannelDataSource {

    private MutableLiveData<Boolean> isSuccessAdd= new MutableLiveData<>();

    private static ChannelDataSource instance;

    public static ChannelDataSource getInstance() {
        if (instance == null)
            instance = new ChannelDataSource();
        return instance;
    }



    DatabaseReference mChannelRef;
    private ChannelDataSource() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mChannelRef = firebaseDatabase.getReference("AiderChannels");
        mChannelRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                user = snapshot.child(getCurrentUser().getUid()).getValue(BasicUser.class);
//                if(notifyUserListener != null)
//                    notifyUserListener.notifyDataChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public String addChannel(Channel p) {
        String id = mChannelRef.push().getKey();
        p.setId(id);
        mChannelRef.child(id).setValue(p).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                isSuccessAdd.setValue(Boolean.TRUE);
                Log.i("UsersDataSource", "success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isSuccessAdd.setValue(Boolean.FALSE);
//                newExceptionMessage.setValue(e.getMessage());
                Log.i("UsersDataSource", "fail");
            }
        });
        return id;
    }

    public LiveData<Boolean> getIsSuccessAdd() {
        return isSuccessAdd;
    }

    public void removeChannel(String id) {
        if(id != null) {
            mChannelRef.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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
        }
    }
}
