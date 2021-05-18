package com.example.aiderchat_proj.utils;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.aiderchat_proj.ui.RegisterViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory{
    private Context context;

    public ViewModelFactory(Context context){
        this.context = context;
    }
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            return (T) new RegisterViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}