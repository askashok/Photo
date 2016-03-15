package com.allmenu;

/**
 * Created by BLT0059 on 5/4/2015.
 */
public class Vote_model {
    String imagepath;
    String date;
    String time;
    String title;
    String rating;
    String ranking;

    String createdby;
    String novotes;


    String spamuserid;
    String photomaster_id;
    String description;

    public String getNovotes() {
        return novotes;
    }

    public void setNovotes(String novotes) {
        this.novotes = novotes;
    }

    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    public String getSpamuserid() {
        return spamuserid;
    }

    public void setSpamuserid(String spamuserid) {
        this.spamuserid = spamuserid;
    }

    public String getPhotomaster_id() {
        return photomaster_id;
    }

    public void setPhotomaster_id(String photomaster_id) {
        this.photomaster_id = photomaster_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }
}
