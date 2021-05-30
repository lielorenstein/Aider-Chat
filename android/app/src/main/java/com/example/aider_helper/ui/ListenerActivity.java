package com.example.aider_helper.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.example.aider_helper.ChannelViewModel;
import com.example.aider_helper.R;
import com.example.aider_helper.classes.Channel;
import com.example.aider_helper.databinding.ActivityListenerBinding;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;

import java.util.Arrays;

public class ListenerActivity extends AppCompatActivity {
    ChannelViewModel channelViewModel;
    ActivityListenerBinding mBinding;
    Channel channel = new Channel("0");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_listener);
        channelViewModel = new ViewModelProvider(this).get(ChannelViewModel.class);
        mBinding.setChannelViewModel(channelViewModel);
        //channel.setId(channelViewModel.getSomeChannel());
        // report
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setPublishKey("pub-c-6662ea57-6bf8-4f52-88f8-8cc4aa36f23a");
        pnConfiguration.setSubscribeKey("sub-c-5c8eb4aa-b1da-11eb-b48e-0ae489c2794e");
        pnConfiguration.setUuid("theClientUUID");
        PubNub pubnub = new PubNub(pnConfiguration);
        // open channel by id
        //pubnub.subscribe().channels(Arrays.asList(channel.getId()+"Broadcast")).withPresence().execute();
    }
}