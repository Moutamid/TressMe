package com.moutamid.treesme.Model;

public class Favorite {

    private String id;
    private String userId;
    private String productName;
    private String productImg;
    private boolean isFavorite;

    public Favorite(){

    }

    public Favorite(String id, String userId, String productName, String productImg, boolean isFavorite) {
        this.id = id;
        this.userId = userId;
        this.productName = productName;
        this.productImg = productImg;
        this.isFavorite = isFavorite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
