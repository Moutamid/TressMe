package com.example.treesme.Model;

public class Benefits {

    private String id;
    private String title;
    private String category;
    private String price;

    public Benefits(){

    }

    public Benefits(String id, String title, String category, String price) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
