package com.example.labour;

public class Package_item {
    private String title;
    private String description;
    private int photoId;

    public Package_item(String title, String description, int photoId) {
        this.title = title;
        this.description = description;
        this.photoId = photoId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPhotoId() {
        return photoId;
    }
}
