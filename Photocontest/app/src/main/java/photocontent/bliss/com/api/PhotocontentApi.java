package photocontent.bliss.com.api;

/**
 * Created by Jenifa Mary.c on 4/23/2015.
 */

public interface PhotocontentApi {

    String hostUrl="http://182.71.230.166:99/photocontest/index.php/services/";
    String registration_url=hostUrl+"registeration?";
    String login_url=hostUrl+"login?";
    String photoupload_url=hostUrl+"photo_upload";
    String votephoto_url=hostUrl+"votephoto";
    String getmyphotos_url=hostUrl+"getmyphotos";
    String getmy_todays_photos_url=hostUrl+"getmytodayphotos";
    String photo_of_the_day_url=hostUrl+"photooftheday";
    String todays_photos_url=hostUrl+"todayphotos?";
    String getmyphotos_vote_url=hostUrl+"getmyphotos_votedetails?";
    String todaytopten_photos_url=hostUrl+"todaytopten";
    String forget_password_url=hostUrl+"forgotpassword?";
    String spam_user_url=hostUrl+"spam_user";
    String show_myprofile_url=hostUrl+"show_myprofile";
    String update_profile_url=hostUrl+"update_profile";
    String Latest_history_url=hostUrl+"getmyhistory?";

 }
