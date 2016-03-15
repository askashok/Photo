package com.allmenu;

/**
 * Created by BLT0059 on 5/16/2015.
 */
public class PhotoDayModel {
    String name;
    String comments;
    String your_vote;
    String rating;
    String userid;

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getYour_vote() {
        return your_vote;
    }

    public void setYour_vote(String your_vote) {
        this.your_vote = your_vote;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
