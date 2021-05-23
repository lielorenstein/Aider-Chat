package com.example.aider_helper.classes;

public class MessageBox {
    private String sender;
    private String content;
    private String timeDate;
    private boolean id;

    private String voiceURL;

    public MessageBox(String sender, String context, String timeDate){
        this.sender = sender;
        this.content = context;
        this.timeDate = timeDate;
        this.id = false;
    }

    public String getSender(){
        return sender;
    }

    public String getContent(){
        return content;
    }

    public String getTimeDate(){
        return timeDate;
    }

    public boolean getId() {
        return id;
    }

    public void setId(boolean id) {
        this.id = id;
    }

    public void setSender(String name){
        this.sender = name;
    }

    public String getVoiceURL() {
        return voiceURL;
    }

    public void setVoiceURL(String voiceURL) {
        this.voiceURL = voiceURL;
    }
}
