package com.example.aiderchat_proj.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.aiderchat_proj.R;
import com.example.aiderchat_proj.classes.BasicUser;
import com.example.aiderchat_proj.databinding.ActivityRegisterBinding;
import com.example.aiderchat_proj.utils.ViewModelFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private RegisterViewModel registerViewModel;
    ActivityRegisterBinding mBinding;
    //private SharedPreferences sharedPreferences;
    Observer<Boolean> isAuthSuccess, isAddSuccess;
    Observer<String> newExceptionMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        ViewModelFactory viewModelFactory = new ViewModelFactory(getBaseContext());
        registerViewModel = new ViewModelProvider(this, viewModelFactory).get(RegisterViewModel.class);
        isAuthSuccess = bool -> {
            if(bool){
                registerViewModel.addUser();
            }
            else{
                registerViewModel.removeUser();
            }
        };
        isAddSuccess = bool -> {
            if(bool) {
                sendEmailVerification();
            }
            else{
                registerViewModel.removeUser();
            }
        };
        newExceptionMessage = message -> {
            Snackbar.make(mBinding.btnEmailSignIn, message, Snackbar.LENGTH_SHORT).show();
        };


        registerViewModel.getIsSuccessAuth().observe(this, isAuthSuccess);
        registerViewModel.getIsSuccessAdd().observe(this, isAddSuccess);
        registerViewModel.getNewExceptionMessage().observe(this, newExceptionMessage);

        mBinding.setRegisterViewModel(registerViewModel);
    }

    private void sendEmailVerification() {
        final FirebaseUser user = registerViewModel.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Re-enable Verify Email button
                        if (task.isSuccessful()) {
                            //savePreferences();
                            registerViewModel.getIsSuccessAuth().removeObserver(isAuthSuccess);
                            registerViewModel.getIsSuccessAdd().removeObserver(isAddSuccess);
                            registerViewModel.getNewExceptionMessage().removeObserver(newExceptionMessage);
                            Intent newIntent = new Intent();
                            newIntent.putExtra("EML", registerViewModel.form.getEmail());
                            newIntent.putExtra("PSS", registerViewModel.form.getPassword());
                            setResult(Activity.RESULT_OK, newIntent);
                            finish();
                        }
                    }
                });
    }


    public void onClickPickDate(View view) {
        TextView editDate = (TextView) view;
        DatePickerDialog picker;
        java.util.Calendar calenderDate = java.util.Calendar.getInstance();
        int day = calenderDate.get(java.util.Calendar.DAY_OF_MONTH);
        int month = calenderDate.get(java.util.Calendar.MONTH);
        int year = calenderDate.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(RegisterActivity.this, android.R.style.Theme_Holo_Dialog,(view1, year1, monthOfYear, dayOfMonth) ->
                editDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1), year,month, day);
        picker.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
        picker.show();
    }

    public void onClickDone(View view){
        //if(isLocationValid(true))
            registerViewModel.registerUser();
    }





    public boolean isLocationValid(boolean setMsg){
        String stringAddress = registerViewModel.form.getUser().getLocation();
        if(stringAddress == null || stringAddress.equals(""))
            return false;
        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> addresses = null;
        try {
            // Getting a maximum of 3 Address that matches the input
            // text
            addresses = geocoder.getFromLocationName(stringAddress, 3);
            if (addresses != null && !addresses.equals(""))
                search(addresses);
            return true;
        } catch (Exception e) {
            if (setMsg)
                registerViewModel.form.locationError.set(R.string.date_format_not_valid);
            return false;
        }
    }

    protected void search(List<Address> addresses) {
        Address address = addresses.get(0);
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        registerViewModel.form.setLocation(getPlace(latLng));
    }

    public String getPlace(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                return addresses.get(0).getAddressLine(0);
            }
            return "unknown place: \n (" + latLng.latitude + ", " + latLng.longitude + ")";
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return "IOException ...";
    }


}
