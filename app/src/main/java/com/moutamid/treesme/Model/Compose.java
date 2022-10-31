package com.moutamid.treesme.Model;

public class Compose {

    private String type;
    private String message;
    private String userId;
    private long timestamp;

    public Compose(){

    }

    public Compose(String message) {
        this.message = message;
    }

    public Compose(String type, String message, String userId, long timestamp) {
        this.type = type;
        this.message = message;
        this.userId = userId;
        this.timestamp = timestamp;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
