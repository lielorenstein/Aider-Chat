package com.example.aiderchat_proj.classes;

public class MessageBox {
    private String sender;
    private String content;
    private String timeDate;
    private boolean id;

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
}
