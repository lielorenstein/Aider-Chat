package com.example.aider_helper.data;

import androidx.lifecycle.LiveData;

import com.example.aider_helper.classes.Channel;

public class ChannelRepos {

    private static ChannelDataSource channelDataSource;
    private static ChannelRepos instance;

    public static ChannelRepos getInstance(){
        if (instance == null){
            instance = new ChannelRepos();
        }
        return instance;
    }

    private ChannelRepos(){

        channelDataSource = ChannelDataSource.getInstance();
    }

    public String addChannel(Channel channel) {
        return channelDataSource.addChannel(channel);
    }

    public LiveData<Boolean> getIsSuccessAdd() {
        return channelDataSource.getIsSuccessAdd();
    }

    public void removeChannel(String id) {
        channelDataSource.removeChannel(id);
    }
}
