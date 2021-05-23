package com.example.aider_helper.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.aider_helper.ChannelViewModel;
import com.example.aider_helper.R;
import com.example.aider_helper.classes.MessageBox;
import com.example.aider_helper.databinding.ActivityChatBinding;
import com.example.aider_helper.utils.FontSizeUtils;
import com.example.aider_helper.utils.RecyclerviewChatAdapter;
import com.example.aider_helper.utils.TextToVoice;
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
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.membership.PNMembershipResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNUUIDMetadataResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, RecyclerviewChatAdapter.ItemClickListener,
        RecyclerviewChatAdapter.ItemLongClickListener
{
    RecyclerviewChatAdapter rvChatAdapter;
    ArrayList<MessageBox> messages;
    String userId = "0";
    String adminId = "0";
    private PubNub pubnub;
    private String theChannel = "0";
    List<String> listNames = Arrays.asList("Fox", "Sunny", "White Goblin", "Ticoo", "Lorex",
            "Penguin", "Lolipop", "Rocky", "Joshy", "Tommy-chicken", "Rocket bird",
                "Rhinosaurus", "Rorex", "Ducky-Duck", "Iguana");
    final int random = new Random().nextInt(listNames.size());
    private String theEntry = listNames.get(random);
    static SimpleDateFormat format = new SimpleDateFormat("hh:mm", Locale.US);
    ActivityChatBinding activityChatBinding;
    Animation scaleUpAnim;
    FloatingActionButton scrollDownButton;
    TextView unreadMessagesBubble;
    Integer counterUnreadMessages = 0;
    ChannelViewModel channelViewModel;
    Set<String> connectedUsers = new HashSet<>();
    TextToVoice textToVoice ;



    TextView textNotifier;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent  == null) {
            return;
        }
        textToVoice = new TextToVoice(getApplicationContext());

        //
        userId = intent.getStringExtra("user_id");
        adminId = intent.getStringExtra("admi_id");
        theChannel = "M-"+adminId+"-CHANNEL";
        setContentView(R.layout.activity_chat);
        activityChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(activityChatBinding.getRoot());
        activityChatBinding.setFontSize(FontSizeUtils.getInstance());
        // init all views to activity:
        messages = new ArrayList<>();
        rvChatAdapter = new RecyclerviewChatAdapter(messages);
        activityChatBinding.recyclerviewChat.setLayoutManager(new LinearLayoutManager(this));
        activityChatBinding.recyclerviewChat.setAdapter(rvChatAdapter);

        scrollDownButton = (FloatingActionButton) findViewById(R.id.scroll_down_button);
        scrollDownButton.setScaleX(0.0f);
        scrollDownButton.setScaleY(0.0f);
        unreadMessagesBubble = (TextView) findViewById(R.id.notification_number_unread_bubble);
        unreadMessagesBubble.setScaleX(0.0f);
        unreadMessagesBubble.setScaleY(0.0f);
        scaleUpAnim = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.scale_up);

        textNotifier = (TextView) findViewById(R.id.textNotifier);

        PNConfiguration pnConfiguration = new PNConfiguration();
        // replace the key placeholders with your own PubNub publish and subscribe keys
        pnConfiguration.setPublishKey("pub-c-6662ea57-6bf8-4f52-88f8-8cc4aa36f23a");
        pnConfiguration.setSubscribeKey("sub-c-5c8eb4aa-b1da-11eb-b48e-0ae489c2794e");
        pnConfiguration.setUuid("theClientUUID");

        channelViewModel = new ViewModelProvider(this).get(ChannelViewModel.class);
        //channelViewModel.getIsSuccessAdd().observe(this, getNumUsersInChannel);

        activityChatBinding.recyclerviewChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!activityChatBinding.recyclerviewChat.canScrollVertically(1)
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
        if(!userId.equals(adminId)){
            JsonObject firstLoginMessage = new JsonObject();
            firstLoginMessage.addProperty("sender", theEntry);
            firstLoginMessage.addProperty("text", "log in command");
            firstLoginMessage.addProperty("id", userId);
            firstLoginMessage.addProperty("typeCommand", "logIn");
            pubnub.publish().channel(theChannel).message(firstLoginMessage).async(
                    new PNCallback<PNPublishResult>() {
                        @Override
                        public void onResponse(PNPublishResult result, PNStatus status) {
                            if (status.isError()) {
                                status.getErrorData().getThrowable().printStackTrace();
                            }
                        }
                    });
        }
        else{
            broadcastNewChannelArrived();
        }

        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void message(PubNub pubnub, PNMessageResult event) {
                JsonObject message = event.getMessage().getAsJsonObject();
                JsonElement sender =  message.get("sender");
                JsonElement text =  message.get("text");
                JsonElement id =  message.get("id");
                JsonElement type =  message.get("typeCommand");
                if(id == null || sender == null || text == null || type == null)
                    return;
                String name_user = sender.getAsString();
                String text_user = text.getAsString();
                String id_user = id.getAsString();
                String type_command = type.getAsString();
                if (!id_user.equals(adminId)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activityChatBinding.LayoutNotifier.setVisibility(View.GONE);
                            activityChatBinding.textNotifier.setVisibility(View.GONE);
                        }
                    });
                }
                if(type_command.equals("message")){
                        MessageBox messageBox = new MessageBox(name_user, text_user, format.format(new Date()));
                        if(id_user.equals(userId)) {
                            messageBox.setSender("You");
                            messageBox.setId(true);
                        }
                        messages.add(messageBox);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rvChatAdapter.notifyDataSetChanged();
                            }
                        });
                        scrollDownIfAtBottom();
                        if(activityChatBinding.recyclerviewChat.canScrollVertically(1)) {
                            if (!id_user.equals(userId)) {
                                if (counterUnreadMessages == 0) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            unreadMessagesBubble.startAnimation(scaleUpAnim);
                                            unreadMessagesBubble.setScaleX(1.0f);
                                            unreadMessagesBubble.setScaleY(1.0f);
                                        }
                                    });

                                }
                                counterUnreadMessages += 1;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        unreadMessagesBubble.setText(counterUnreadMessages.toString());
                                    }
                                });
                            }
                        }

                }
                else if(type_command.equals("getOut") && userId.equals(id_user)){
                    finish();
                }
                else if(type_command.equals("logIn")){
                        connectedUsers.add(id_user);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(connectedUsers.size() > 0) {
                                activityChatBinding.LayoutNotifier.setVisibility(View.GONE);
                                activityChatBinding.textNotifier.setVisibility(View.GONE);
                            }
                            else{
                                activityChatBinding.LayoutNotifier.setVisibility(View.VISIBLE);
                                activityChatBinding.textNotifier.setVisibility(View.VISIBLE);
                            }

                        }
                    });
                }
            }

            @Override
            public void status(PubNub pubnub, PNStatus event) {

                if (event.getCategory().equals(PNStatusCategory.PNConnectedCategory)){
                    submitUpdate(theEntry, "Harmless.");
                }
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
        pubnub.subscribe().channels(Arrays.asList(theChannel)).withPresence().execute();
        // listeners
        rvChatAdapter.setClickListener(this);
        rvChatAdapter.setLongClickListener(this);
    }

    protected void submitUpdate(String anEntry, String anUpdate) {
        JsonObject entryUpdate = new JsonObject();
        entryUpdate.addProperty("channelIdentifier", adminId);
        entryUpdate.addProperty("sender", anEntry);
        entryUpdate.addProperty("text", anUpdate);
        entryUpdate.addProperty("id", userId);
        entryUpdate.addProperty("typeCommand", "message");

        pubnub.publish().channel(theChannel).message(entryUpdate).async(
                new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        if (status.isError()) {
                            status.getErrorData().getThrowable().printStackTrace();
                        }
                        else {
                            activityChatBinding.recyclerviewChat.post(new Runnable() {
                                @Override
                                public void run() {
                                    activityChatBinding.recyclerviewChat.scrollToPosition(messages.size() - 1);
                                }
                            });
                        }
                        scrollDownIfAtBottom();
                    }
                });
    }

    private void scrollDownIfAtBottom(){
        if (!activityChatBinding.recyclerviewChat.canScrollVertically(1)) {
            activityChatBinding.recyclerviewChat.post(new Runnable() {
                @Override
                public void run() {
                    activityChatBinding.recyclerviewChat.scrollToPosition(messages.size() - 1);
                }
            });
            scrollDownButton.setScaleX(0);
            scrollDownButton.setScaleY(0);
            unreadMessagesBubble.setScaleX(0.0f);
            unreadMessagesBubble.setScaleY(0.0f);
            counterUnreadMessages=0;
        }
    }

    @Override
    public void onClick(View view) {
        if(activityChatBinding.entryNewText.getText().toString().equals(""))
            return;
        submitUpdate(theEntry, activityChatBinding.entryNewText.getText().toString());
        activityChatBinding.entryNewText.setText("");
    }

    @Override
    public void onItemClick(View view, int position) {
        String e = messages.get(position).getSender();
        if(e.equals("You")){
            e = theEntry;
        }
        textToVoice.playVoice(messages.get(position).getContent(),
                textToVoice.getVoice(listNames.indexOf(e)));
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    public void onScrollDownClick(View view) {
        activityChatBinding.recyclerviewChat.post(new Runnable() {
            @Override
            public void run() {
                activityChatBinding.recyclerviewChat.scrollToPosition(messages.size() - 1);
            }
        });
        scrollDownButton.setScaleX(0);
        scrollDownButton.setScaleY(0);
        unreadMessagesBubble.setScaleX(0.0f);
        unreadMessagesBubble.setScaleY(0.0f);
        counterUnreadMessages=0;
    }


    private void broadcastNewChannelArrived(){
        JsonObject entryUpdate = new JsonObject();
        entryUpdate.addProperty("channelIdentifier", "BroadcastAll");
        entryUpdate.addProperty("id", adminId);
        entryUpdate.addProperty("sender", theEntry);
        entryUpdate.addProperty("text", "");
        entryUpdate.addProperty("typeCommand", "message");
        pubnub.publish().channel("BroadcastAll").message(entryUpdate).async(
                new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        if (status.isError()) {
                            status.getErrorData().getThrowable().printStackTrace();
                        }
                    }
                });
    }



    public void onFontIncrease(View view) {
        activityChatBinding.getFontSize().size++;
        activityChatBinding.recyclerviewChat.getAdapter().notifyDataSetChanged();
        activityChatBinding.recyclerviewChat.post(new Runnable() {
            @Override
            public void run() {
                activityChatBinding.recyclerviewChat.scrollToPosition(messages.size() - 1);
                activityChatBinding.recyclerviewChat.scrollBy(0,activityChatBinding.getFontSize().size);
            }
        });
    }


    public void onFontDecrease(View view) {
        activityChatBinding.getFontSize().size--;
        activityChatBinding.recyclerviewChat.getAdapter().notifyDataSetChanged();
        activityChatBinding.recyclerviewChat.scrollBy(0,activityChatBinding.getFontSize().size);
    }

    public void LayoutOnClick(View view) {
        textToVoice.playVoice(activityChatBinding.textNotifier.getText().toString(), textToVoice.getVoice(listNames.indexOf(theEntry)));
    }

}