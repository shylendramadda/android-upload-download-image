package com.shylendra.androiduploadimagetoserver;

public class EntityObject {
    private String image;
    private String name;
    public EntityObject(String image, String name) {
        this.image = image;
        this.name = name;
    }
    public String getImage() {
        return image;
    }
    public String getName() {
        return name;
    }
}