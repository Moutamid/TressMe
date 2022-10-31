package com.moutamid.treesme.Model;

public class Post {

    private String id;
    private String postUrl;
    private String hairstyles;
    private long timestamp;
    private String dresserId;
    private int likes;
    private boolean saved;

    public Post(){

    }

    public Post(String id, String postUrl,String hairstyles ,long timestamp,String dresserId, int likes, boolean saved) {
        this.id = id;
        this.postUrl = postUrl;
        this.hairstyles = hairstyles;
        this.timestamp = timestamp;
        this.dresserId = dresserId;
        this.likes = likes;
        this.saved = saved;
    }

    public String getHairstyles() {
        return hairstyles;
    }

    public void setHairstyles(String hairstyles) {
        this.hairstyles = hairstyles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDresserId() {
        return dresserId;
    }

    public void setDresserId(String dresserId) {
        this.dresserId = dresserId;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }
}
