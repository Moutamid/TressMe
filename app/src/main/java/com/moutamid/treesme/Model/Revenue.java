package com.moutamid.treesme.Model;

public class Revenue {

    private String id;
    private String total;
    private String date;

    public Revenue(){

    }

    public Revenue(String id, String total, String date) {
        this.id = id;
        this.total = total;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
