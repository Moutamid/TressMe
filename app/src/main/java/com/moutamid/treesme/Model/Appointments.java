package com.moutamid.treesme.Model;

public class Appointments {

    private String id;
    private String style;
    private String dresserId;
    private String time;
    private String date;
    private String userId;
    private String place;
    private String price;

    public Appointments(){}

    public Appointments(String id, String style, String dresserId, String time, String date, String userId, String place) {
        this.id = id;
        this.style = style;
        this.dresserId = dresserId;
        this.time = time;
        this.date = date;
        this.userId = userId;
        this.place = place;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getDresserId() {
        return dresserId;
    }

    public void setDresserId(String dresserId) {
        this.dresserId = dresserId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
