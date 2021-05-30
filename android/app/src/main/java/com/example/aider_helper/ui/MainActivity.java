package com.example.aider_helper.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.aider_helper.BR;
import com.example.aider_helper.R;
import com.example.aider_helper.classes.Channel;
import com.example.aider_helper.databinding.ActivityMainBinding;
import com.example.aider_helper.utils.FontSizeUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.membership.PNMembershipResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNUUIDMetadataResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;

import java.util.Arrays;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mBinding;
    Context context;
    static public boolean activeSubscriber = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        setContentView(R.layout.activity_main);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.setActiveSubscriber(activeSubscriber);
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setPublishKey("pub-c-6662ea57-6bf8-4f52-88f8-8cc4aa36f23a");
        pnConfiguration.setSubscribeKey("sub-c-5c8eb4aa-b1da-11eb-b48e-0ae489c2794e");
        pnConfiguration.setUuid("theClientUUID");
        pubnub = new PubNub(pnConfiguration);
    }
    PubNub pubnub;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onReadyToHelpClick(View view) {
        activeSubscriber = !activeSubscriber;
        turnSwitch();
        if(!activeSubscriber)
            return;
        UUID uuid = UUID.randomUUID();
        String helperId = uuid.toString();
        pubnub.addListener(new SubscribeCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            @Override
            public void message(PubNub pubnub, PNMessageResult event) {
                JsonObject message = event.getMessage().getAsJsonObject();
                JsonElement id =  message.get("id");
                if(id == null )
                    return;
                String waitingRoomId = id.getAsString();

                if (android.os.Build.VERSION.SDK_INT >= 26){
                    addNotification(waitingRoomId, helperId);
                }
                else{
                    generateOldNotification(waitingRoomId, helperId);
                }

            }

            @Override
            public void status(PubNub pubnub, PNStatus event) {
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult event) {
            }

            // even if you don't need these handler, you still have include them
            // because we are extending an Abstract class
            @Override
            public void signal(PubNub pubnub, PNSignalResult event) { }

            @Override
            public void uuid(PubNub pubnub, PNUUIDMetadataResult pnUUIDMetadataResult) { }

            @Override
            public void channel(PubNub pubnub, PNChannelMetadataResult pnChannelMetadataResult) { }

            @Override
            public void membership(PubNub pubnub, PNMembershipResult pnMembershipResult) { }

            @Override
            public void messageAction(PubNub pubnub, PNMessageActionResult event) { }

            @Override
            public void file(PubNub pubnub, PNFileEventResult pnFileEventResult) {

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onHelpMeClick(View view) {
        activeSubscriber = false;
        turnSwitch();
        UUID uuid = UUID.randomUUID();
        String waitingRoomId = uuid.toString();
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("user_id", waitingRoomId); //generate special id for new connect;
        intent.putExtra("admi_id",waitingRoomId);
        startActivity(intent);
    }

//    public void onHelpToClick(View view) {
//        Intent intent = new Intent(this, ListenerActivity.class);
//        startActivity(intent);
//    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addNotification(String idClient, String userId) {

        String notificationId = "channel_1";//id of channel
        String description = "ezra";//Description information of channel
        int importance = NotificationManager.IMPORTANCE_HIGH;//The Importance of channel
        NotificationChannel channel = new NotificationChannel(notificationId, description, importance);//Generating channel
        // prepare intent which is triggered if the
        // notification is selected

        BroadcastReceiver receiverApproveNotification = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pubnub.unsubscribe().channels(Arrays.asList("BroadcastAll")).execute();
                unregisterReceiver(this);
                Intent intent4 = new Intent(context, ChatActivity.class);
                intent4.putExtra("user_id", userId); //generate special id for new connect;
                intent4.putExtra("admi_id",idClient);
//                intent4.setComponent(new ComponentName("com.example.aider_helper",
//                        "com.example.aider_helper.ui.ChatActivity"));
                startActivity(intent4);
            }
        };
        Intent approveIntent = new Intent("NOTIFICATION_ACCEPT");
        PendingIntent pendingApproveIntent = PendingIntent.getBroadcast(context, 0, approveIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        registerReceiver(receiverApproveNotification, new IntentFilter("NOTIFICATION_ACCEPT"));
        //
        BroadcastReceiver receiverDeleteNotification = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pubnub.subscribe().channels(Arrays.asList("BroadcastAll")).withPresence().execute();
                unregisterReceiver(this);
            }
        };
        Intent deleteIntent = new Intent("NOTIFICATION_DELETED");
        PendingIntent pendingDeleteIntent = PendingIntent.getBroadcast(context, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        registerReceiver(receiverDeleteNotification, new IntentFilter("NOTIFICATION_DELETED"));


        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent4, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder mBuilder = new Notification.Builder(this, notificationId);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mBuilder.setAutoCancel(true);
        mBuilder.setSmallIcon(R.drawable.icon_hackathon)
                .setContentTitle("notification - Aider")
                .setContentText("a new host is looking for your help!")
                .setContentIntent(pendingApproveIntent)
                .setDeleteIntent(pendingDeleteIntent);

        // Gets an instance of the NotificationManager service

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        /*
        What Are Notification Channels?
        Notification channels enable us app developers to group our notifications into groups—channels—with
        the user having the ability to modify notification settings for the entire channel at once.
        For example, for each channel, users can completely block all notifications,
        override the importance level, or allow a notification badge to be shown.
        This new feature helps in greatly improving the user experience of an app.
        */

        mNotificationManager.createNotificationChannel(channel);
        mBuilder.setChannelId(notificationId);

        /*
        When you issue multiple notifications about the same type of event,
        it’s best practice for your app to try to update an existing notification
        with this new information, rather than immediately creating a new notification.
        If you want to update this notification at a later date, you need to assign it an ID.
        You can then use this ID whenever you issue a subsequent notification.
        If the previous notification is still visible, the system will update this existing notification,
        rather than create a new one. In this example, the notification’s ID is 001
        */

        mNotificationManager.notify(001, mBuilder.build());

    }


    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void generateOldNotification(String idClient, String userId) {
        int icon = R.drawable.icon_hackathon;
        BroadcastReceiver receiverApproveNotification = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pubnub.unsubscribe().channels(Arrays.asList("BroadcastAll")).execute();
                unregisterReceiver(this);
                Intent intent4 = new Intent(context, ChatActivity.class);
                intent4.putExtra("user_id", userId); //generate special id for new connect;
                intent4.putExtra("admi_id",idClient);
//                intent4.setComponent(new ComponentName("com.example.aider_helper",
//                        "com.example.aider_helper.ui.ChatActivity"));
                startActivity(intent4);
            }
        };
        Intent approveIntent = new Intent("NOTIFICATION_ACCEPT");
        PendingIntent pendingApproveIntent = PendingIntent.getBroadcast(context, 0, approveIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        registerReceiver(receiverApproveNotification, new IntentFilter("NOTIFICATION_ACCEPT"));
        //
        BroadcastReceiver receiverDeleteNotification = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pubnub.subscribe().channels(Arrays.asList("BroadcastAll")).withPresence().execute();
                unregisterReceiver(this);
            }
        };
        Intent deleteIntent = new Intent("NOTIFICATION_DELETED");
        PendingIntent pendingDeleteIntent = PendingIntent.getBroadcast(context, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        registerReceiver(receiverDeleteNotification, new IntentFilter("NOTIFICATION_DELETED"));



        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        nBuilder.setSound(alarmSound);
        nBuilder.setAutoCancel(true);

        // set intent so it does not start a new activity
        Notification notification  = nBuilder
                .setContentIntent(pendingApproveIntent)
                .setDeleteIntent(pendingDeleteIntent)
                .setSmallIcon(icon)
                .setWhen( System.currentTimeMillis())
                .setContentTitle("notification - Aider")
                .setContentText("a new host is looking for your help!").build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(001, notification);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void turnSwitch(){
        if(activeSubscriber){
            pubnub.subscribe().channels(Arrays.asList("BroadcastAll")).withPresence().execute();
            mBinding.IdAvailableHelpButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.light_green));
        }
        else{
            pubnub.unsubscribe().channels(Arrays.asList("BroadcastAll")).execute();
            mBinding.IdAvailableHelpButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.blue));
        }
    }






    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResume(){
        super.onResume();
        turnSwitch();
    }
}