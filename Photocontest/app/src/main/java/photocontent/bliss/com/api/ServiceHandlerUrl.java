package photocontent.bliss.com.api;

import android.util.Log;

/**
 * Created by BLT0059 on 4/24/2015.
 */
public class ServiceHandlerUrl {
    private static String registr_url;
    private static String login_url;
    private static String forgetpsw_url;
    private static String facebooklogin_url;
    private static String today_photo_url;
    private static String getmyphotos_votedetails_url;

    public static String getregistrationUrl(String fstname_str, String lstname_str, String emlname_str, String psw_str, String confpsw_str, String mblno_str) {
        registr_url=PhotocontentApi.registration_url+"first_name="+fstname_str+"&last_name="+lstname_str+"&phone_no="+mblno_str+"&email_id="+emlname_str+"&password="+psw_str;
        return registr_url;
    }

    public static String getLoginUrl(String name_str, String password_str) {
        login_url=PhotocontentApi.login_url+"email_id="+name_str+"&password="+password_str;
        return login_url;
    }

    public static String getRemberMe(String name_str) {
        forgetpsw_url=PhotocontentApi.forget_password_url+"email_id="+name_str;
        return forgetpsw_url;
    }

    public static String getfacebookLoginUrl(String fname, String lName, String userid, String name, String mailid) {
        facebooklogin_url=PhotocontentApi.registration_url+"facebook_id="+userid+"&first_name="+fname+"&last_name="+lName+"&facebook_username="+name+"&email_id="+mailid+"";
        Log.v("facebookurl",facebooklogin_url);
        return facebooklogin_url;
    }


    public static String getTodayPhotoUrl() {
        today_photo_url=PhotocontentApi.todays_photos_url;
        Log.v("todays_photos_urltodays_photos_url",today_photo_url);
        return today_photo_url;
    }

    public static String refressVotePhotoDetail(String photomas_id) {
        getmyphotos_votedetails_url=PhotocontentApi.getmyphotos_vote_url+"photomaster_id="+photomas_id;
        Log.v("getmyphotos_votedetails_urlgetmyphotos_votedetails_url",getmyphotos_votedetails_url);
        return getmyphotos_votedetails_url;
    }
}
