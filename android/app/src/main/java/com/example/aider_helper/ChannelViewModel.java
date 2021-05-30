package com.example.aider_helper;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.aider_helper.classes.Channel;
import com.example.aider_helper.data.ChannelRepos;

public class ChannelViewModel extends AndroidViewModel {

    private ChannelRepos repository;

    public ChannelViewModel(@NonNull Application application) {
        super(application);
        repository = ChannelRepos.getInstance();
    }


    public Channel getChannelInstance(){
        return null;
    }

    public String addChannel(Channel channel) {
        return repository.addChannel(channel);
    }

    public LiveData<Boolean> getIsSuccessAdd() {
        return repository.getIsSuccessAdd();
    }

    public void removeChannel(String id) {
        repository.removeChannel(id);
    }
}
