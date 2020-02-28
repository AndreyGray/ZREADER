package com.example.zreader.model;

/*

This class is a model for  book items


 */

public class Book {

    private int ID = 0;
    private String title = null;
    private String thumbnail = null;
    private boolean isNew = false;
    private String thumb_ext = null;

    public Book() {
    }

    public int getID() {
        return ID;
    }
    //In this setter converting value from string to integer
    public void setID(String ID) {
        this.ID = Integer.parseInt(ID);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isNew() {
        return isNew;
    }

    //In this setter converting value from string to boolean
    public void setNew(String aNew) {
        isNew = aNew.equalsIgnoreCase("TRUE");
    }

    public String getThumb_ext() {
        return thumb_ext;
    }

    public void setThumb_ext(String thumb_ext) {
        this.thumb_ext = thumb_ext;
    }
}
