package com.example.aiderchat_proj.ui;

import android.app.Application;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.example.aiderchat_proj.BR;
import com.example.aiderchat_proj.R;
import com.example.aiderchat_proj.classes.BasicUser;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormRegister extends BaseObservable {
    private Context baseContext;
    private BasicUser.Gender gender;
    private String password;
    public boolean kids = false, girls = false, oldPpl = false;
    LatLng latLng;
    private BasicUser user = new BasicUser();
    public ObservableField<Integer> emailError = new ObservableField<>();
    public ObservableField<Integer> passwordError = new ObservableField<>();
    public ObservableField<Integer> dateError = new ObservableField<>();
    public ObservableField<Integer> locationError = new ObservableField<>();
    static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

    public FormRegister(Context context) {
        baseContext = context;
    }

    public BasicUser getUser() {
        return user;
    }

    public boolean UserDone(){
        boolean locationValid = isLocationValid(true);
        return locationValid;
    }

    @Bindable
    public boolean isValid() {
        boolean notEmptyName =
                user.getFirstName() != null && !user.getFirstName().equals("") &&
                user.getLastName() != null && !user.getLastName().equals("");
        boolean validEmail = isEmailValid(false);
        boolean validPassword = isPasswordValid(false);
        boolean validDate = isBirthDateValid(false);
        boolean validLocation = user.getLocation() != null && !user.getLocation().equals("");
        return notEmptyName && validPassword && validEmail && validDate && validLocation
                && (kids || girls || oldPpl);
    }

    public void setFirstName(String firstName) {
        user.setFirstName(firstName);
        notifyPropertyChanged(BR.valid);
    }

    public void setLastName(String lastName) {
        user.setLastName(lastName);
        notifyPropertyChanged(BR.valid);
    }

    public void setEmail(String email) {
        user.setEmailAddress(email);
        notifyPropertyChanged(BR.valid);
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.valid);
    }

    public void setBirthDate(Date date) {
        user.setBirthDate(date);
        notifyPropertyChanged(BR.valid);
    }

    public void setLocation(String location) {
        user.setLocation(location);
        notifyPropertyChanged(BR.valid);
    }

    @Bindable
    public String getFirstName() {
        return user.getFirstName();
    }

    @Bindable
    public String getLastName() {
        return user.getLastName();
    }

    @Bindable
    public String getEmail() {
        return user.getEmailAddress();
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    @Bindable
    public Date getBirthDate() {
        return user.getBirthDate();
    }

    @Bindable
    public String getLocation() { return user.getLocation();}

    public List<String> getGenders(){
        List<String> listGenders = new ArrayList<String>();
        for (BasicUser.Gender gender : BasicUser.Gender.values()) {
            listGenders.add(gender.toString());
        }
        return listGenders;
    }

    public boolean isEmailValid(boolean setMsg) {
        if (getEmail() == null)
            return false;
        String regex = "^(.+)@(.+)$";
        if (!RegexValidation(getEmail(), regex)) {
            if (setMsg)
                emailError.set(R.string.email_not_valid);
            return false;
        }
        emailError.set(null);
        return true;
    }

    public boolean RegexValidation(String value, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public boolean isPasswordValid(boolean setMsg) {
        if (getPassword() == null)
            return false;
        if (getPassword().length() < 6) {
            if (setMsg)
                passwordError.set(R.string.password_not_valid);
            return false;
        }
        passwordError.set(null);
        return true;
    }

    public boolean isBirthDateValid(boolean setMsg){
        if(user.getBirthDate() == null)
            return false;
        String regex = "^([0-2][0-9]||3[0-1])/(0[0-9]||1[0-2])/([0-9][0-9])?[0-9][0-9]$";
        if(!RegexValidation(user.getBirthDate() == null ? null : format.format(user.getBirthDate()),regex)) {
            if (setMsg)
                dateError.set(R.string.date_format_not_valid);
            return false;
        }
        dateError.set(null);
        return true;
    }

    public boolean isLocationValid(boolean setMsg){
        if(user.getLocation() == null)
            return false;
        Geocoder geocoder = new Geocoder(baseContext);
        List<Address> addresses = null;
        try {
            // Getting a maximum of 3 Address that matches the input
            // text
            addresses = geocoder.getFromLocationName(user.getLocation(), 3);
            if (addresses != null && !addresses.equals("")) {
                search(addresses);
                return true;
            }
        } catch (Exception e) {
            if (setMsg)
                locationError.set(R.string.date_format_not_valid);
            return false;
        }
        return true;
    }


    protected void search(List<Address> addresses) {
        Address address = addresses.get(0);
        latLng = new LatLng(address.getLatitude(), address.getLongitude());
        setLocation(getPlace(latLng));
    }

    public String getPlace(LatLng latLng) {
        Geocoder geocoder = new Geocoder(baseContext, Locale.getDefault());
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


    public void checkboxSwitchKids()
    {
        kids = !kids;
        notifyPropertyChanged(BR.valid);
    }

    public void checkboxSwitchGirls()
    {
        girls = !girls;
        notifyPropertyChanged(BR.valid);
    }

    public void checkboxSwitchOldPpl()
    {
        oldPpl = !oldPpl;
        notifyPropertyChanged(BR.valid);
    }


}
