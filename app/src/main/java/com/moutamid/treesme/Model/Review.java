package com.moutamid.treesme.Model;

public class Review {

    private String id;
    private User users;
    private String type;
    private int rate;

    public Review() {
    }

    public Review(User users, String type, int rate) {
       // this.id = id;
        this.users = users;
        this.type = type;
        this.rate = rate;
    }

    public User getUsers() {
        return users;
    }

    public void setUsers(User users) {
        this.users = users;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

}
