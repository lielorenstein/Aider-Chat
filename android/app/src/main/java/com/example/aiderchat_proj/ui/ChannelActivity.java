package com.example.aiderchat_proj.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiderchat_proj.R;
import com.example.aiderchat_proj.classes.MessageBox;
import com.example.aiderchat_proj.utils.RecyclerviewChatAdapter;
import com.example.aiderchat_proj.databinding.ActivityChannelBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.membership.PNMembershipResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNUUIDMetadataResult;

public class ChannelActivity extends AppCompatActivity implements View.OnClickListener, RecyclerviewChatAdapter.ItemClickListener,
        RecyclerviewChatAdapter.ItemLongClickListener
{
    RecyclerviewChatAdapter rvChatAdapter;
    ArrayList<MessageBox> messages;
    String user_id = "th5456hgfhgi";
    private PubNub pubnub;
    private String theChannel = "backend-session";
    private String theEntry = "Earth";

    static SimpleDateFormat format = new SimpleDateFormat("hh:mm", Locale.US);
    ActivityChannelBinding activityChannelBinding;

    Animation scaleUpAnim;
    FloatingActionButton scrollDownButton;
    TextView unreadMessagesBubble;
    Integer counterUnreadMessages = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        activityChannelBinding = ActivityChannelBinding.inflate(getLayoutInflater());
        setContentView(activityChannelBinding.getRoot());

        // init all views to activity:
        messages = new ArrayList<>();
        rvChatAdapter = new RecyclerviewChatAdapter(messages);
        activityChannelBinding.recyclerviewChat.setLayoutManager(new LinearLayoutManager(this));
        activityChannelBinding.recyclerviewChat.setAdapter(rvChatAdapter);

        scrollDownButton = (FloatingActionButton) findViewById(R.id.scroll_down_button);
        scrollDownButton.setScaleX(0.0f);
        scrollDownButton.setScaleY(0.0f);
        unreadMessagesBubble = (TextView) findViewById(R.id.notification_number_unread_bubble);
        unreadMessagesBubble.setScaleX(0.0f);
        unreadMessagesBubble.setScaleY(0.0f);
        scaleUpAnim = AnimationUtils.loadAnimation(ChannelActivity.this, R.anim.scale_up);

        PNConfiguration pnConfiguration = new PNConfiguration();
        // replace the key placeholders with your own PubNub publish and subscribe keys
        pnConfiguration.setPublishKey("pub-c-6662ea57-6bf8-4f52-88f8-8cc4aa36f23a");
        pnConfiguration.setSubscribeKey("sub-c-5c8eb4aa-b1da-11eb-b48e-0ae489c2794e");
        pnConfiguration.setUuid("theClientUUID");

        activityChannelBinding.recyclerviewChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!activityChannelBinding.recyclerviewChat.canScrollVertically(1)
                        && scrollDownButton.getScaleX() == 1f
                        && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    scrollDownButton.setScaleX(0.0f);
                    scrollDownButton.setScaleY(0.0f);
                    unreadMessagesBubble.setScaleX(0.0f);
                    unreadMessagesBubble.setScaleY(0.0f);
                    counterUnreadMessages=0;
                }
                else if(scrollDownButton.getScaleX() == 0 ){
                    scrollDownButton.startAnimation(scaleUpAnim);
                    scrollDownButton.setScaleX(1.0f);
                    scrollDownButton.setScaleY(1.0f);
                }
            }
        });
        
        pubnub = new PubNub(pnConfiguration);
        pubnub.addListener(new SubscribeCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void message(PubNub pubnub, PNMessageResult event) {
                JsonObject message = event.getMessage().getAsJsonObject();
                JsonElement sender =  message.get("sender");
                JsonElement text =  message.get("text");
                JsonElement id =  message.get("id");
                if(id == null || sender == null || text == null)
                    return;
                String name_user = sender.getAsString();
                String text_user = text.getAsString();
                String id_user = id.getAsString();
                MessageBox messageBox = new MessageBox(name_user, text_user, format.format(new Date()));
                if(id_user.equals(user_id))
                    messageBox.setId(true);
                messages.add(messageBox);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rvChatAdapter.notifyDataSetChanged();
                    }
                });
                scrollDownIfAtBottom();
                if(activityChannelBinding.recyclerviewChat.canScrollVertically(1)){
                    if(!id_user.equals(user_id)){
                        if(counterUnreadMessages==0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    unreadMessagesBubble.startAnimation(scaleUpAnim);
                                    unreadMessagesBubble.setScaleX(1.0f);
                                    unreadMessagesBubble.setScaleY(1.0f);
                                }
                            });

                        }
                        counterUnreadMessages+=1;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                unreadMessagesBubble.setText(counterUnreadMessages.toString());
                            }
                        });
                    }


                }

            }

            @Override
            public void status(PubNub pubnub, PNStatus event) {
                displayMessage("[STATUS: " + event.getCategory() + "]",
                        "connected to channels: " + event.getAffectedChannels());

                if (event.getCategory().equals(PNStatusCategory.PNConnectedCategory)){
                    submitUpdate(theEntry, "Harmless.");
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult event) {
                displayMessage("[PRESENCE: " + event.getEvent() + ']',
                        "uuid: " + event.getUuid() + ", channel: " + event.getChannel());
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

        pubnub.subscribe().channels(Arrays.asList(theChannel)).withPresence().execute();




        // listeners
        rvChatAdapter.setClickListener(this);
        rvChatAdapter.setLongClickListener(this);

    }

    protected void submitUpdate(String anEntry, String anUpdate) {
        JsonObject entryUpdate = new JsonObject();
        entryUpdate.addProperty("sender", anEntry);
        entryUpdate.addProperty("text", anUpdate);
        entryUpdate.addProperty("id", user_id);

        pubnub.publish().channel(theChannel).message(entryUpdate).async(
                new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        if (status.isError()) {
                            status.getErrorData().getThrowable().printStackTrace();
                        }
                        else {
                            activityChannelBinding.recyclerviewChat.post(new Runnable() {
                                @Override
                                public void run() {
                                    activityChannelBinding.recyclerviewChat.scrollToPosition(messages.size() - 1);
                                }
                            });
                        }
                        scrollDownIfAtBottom();
                    }
                });
    }

    private void scrollDownIfAtBottom(){
        if (!activityChannelBinding.recyclerviewChat.canScrollVertically(1)) {
            activityChannelBinding.recyclerviewChat.post(new Runnable() {
                @Override
                public void run() {
                    activityChannelBinding.recyclerviewChat.scrollToPosition(messages.size() - 1);
                }
            });
            scrollDownButton.setScaleX(0);
            scrollDownButton.setScaleY(0);
            unreadMessagesBubble.setScaleX(0.0f);
            unreadMessagesBubble.setScaleY(0.0f);
            counterUnreadMessages=0;
        }
    }

    protected void displayMessage(String messageType, String aMessage) {
        String newLine = "\n";

        final StringBuilder textBuilder = new StringBuilder()
//                .append(messagesText.getText().toString())
                .append(messageType)
                .append(newLine)
                .append(aMessage)
                .append(newLine).append(newLine);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                messagesText.setText(textBuilder.toString());
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(activityChannelBinding.entryNewText.getText().toString().equals(""))
            return;
        submitUpdate(theEntry, activityChannelBinding.entryNewText.getText().toString());
        activityChannelBinding.entryNewText.setText("");
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    public void onScrollDownClick(View view) {
        activityChannelBinding.recyclerviewChat.post(new Runnable() {
            @Override
            public void run() {
                activityChannelBinding.recyclerviewChat.scrollToPosition(messages.size() - 1);
            }
        });
        scrollDownButton.setScaleX(0);
        scrollDownButton.setScaleY(0);
        unreadMessagesBubble.setScaleX(0.0f);
        unreadMessagesBubble.setScaleY(0.0f);
        counterUnreadMessages=0;
    }
}