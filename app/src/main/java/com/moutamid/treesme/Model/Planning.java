package com.moutamid.treesme.Model;

public class Planning {

    private String id;
    private String time;
    private String date;
    private String dresserId;

    public Planning(){

    }

    public Planning(String id, String time, String date, String dresserId) {
        this.id = id;
        this.time = time;
        this.date = date;
        this.dresserId = dresserId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDresserId() {
        return dresserId;
    }

    public void setDresserId(String dresserId) {
        this.dresserId = dresserId;
    }
}
