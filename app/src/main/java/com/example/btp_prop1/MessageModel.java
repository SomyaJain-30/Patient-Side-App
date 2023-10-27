package com.example.btp_prop1;

public class MessageModel {
    private String sender,message;

    public MessageModel() {
    }

    public MessageModel(String message, String sender){
        this.message = message;
        this.sender = sender;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
