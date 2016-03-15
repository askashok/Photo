package photocontent.bliss.com.api;

import android.os.StrictMode;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jenifa Mary.C on 4/23/2015.
 */
public class ConnectApi implements Errorresponse {

    private static String updateProfileUrl = PhotocontentApi.photoupload_url;
    private static String url=PhotocontentApi.getmy_todays_photos_url;
    private static String allimageurl=PhotocontentApi.getmyphotos_url;
    private static String response = "null";
    private static String toptenimage_url=PhotocontentApi.todaytopten_photos_url;
    private static  String allusertoday_photosurl=PhotocontentApi.todays_photos_url;
    private static  String getmyphotovote_url=PhotocontentApi.votephoto_url;
    private static String photo_of_the_day=PhotocontentApi.photo_of_the_day_url;
    private static String vote_photos_details_url=PhotocontentApi.getmyphotos_vote_url;
    private static String spam_details_url=PhotocontentApi.spam_user_url;
    private static String getmyphotos_votedetails_url=PhotocontentApi.getmyphotos_vote_url;
    private static String getprofile_url=PhotocontentApi.show_myprofile_url;
    private static String getupdateprofile_url=PhotocontentApi.update_profile_url;
    private static String getfacebook_url=PhotocontentApi.registration_url;
    private static String getLatestHistory_url=PhotocontentApi.Latest_history_url;
    public ConnectApi() {

    }

    /**
     * Method will connect App with WEBSERVICE and return the Response as JSON format if request
     * was Successful otherwise throws Error
     *
     * @param URL Valid URL
     * @return
     */

    public static String call_getmethod(String URL) {

        try {
            System.out.println("inside get method" + URL);
            int statuscode;
            //StringBuilder json = new StringBuilder();
            StringBuffer json = new StringBuffer();
            InputStream is = null;
            BufferedReader br = null;
            HttpEntity httpEntity;
            StatusLine statusLine;
            DefaultHttpClient httpclient = null;
            HttpGet httpget;

            try {
                System.out.println("inside try catch");
                httpclient = new DefaultHttpClient();

                httpget = new HttpGet(URL);
                System.out.println("httpget---" + httpget);
                HttpResponse httpResponse = httpclient.execute(httpget);
                statusLine = httpResponse.getStatusLine();
                statuscode = statusLine.getStatusCode();

                if (statuscode == 200) {
                    httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                    br = new BufferedReader(new InputStreamReader(is));

                    while ((response = br.readLine()) != null) {

                        json.append(response);
                    }

                    response = json.toString();

                    Log.i("Response From Server", response);

                } else {
                    Log.i("NO_RESPONSE", response);
                    response = NO_RESPONSE;

                }

            } catch (HttpResponseException hre) {
                response = NO_RESPONSE;
                hre.printStackTrace();
            } catch (HttpHostConnectException hce) {
                response = HTTP_HOST_EXCEPTION;
                hce.printStackTrace();
            } catch (UnknownHostException e) {
                response = UNKNOWN_HOST;
            } catch (NoHttpResponseException nr) {
                response = NO_RESPONSE;
                nr.printStackTrace();
            } catch (ConnectTimeoutException cte) {
                response = TIMEOUT_EXCEPTION;
                cte.printStackTrace();
            } catch (SocketTimeoutException ste) {
                response = SOCKETTIMEOUT_EXCEPTION;
                ste.printStackTrace();
            } catch (SocketException se) {
                response = SOCKET_EXCEPTION;
                se.printStackTrace();
            } catch (Exception e) {
                response = UNABLE_TO_CONNECT;
                e.printStackTrace();
            } finally {

                try {
                    if (br != null) {
                        br.close();
                    }

                    if (is != null) {
                        is.close();
                        httpclient.getConnectionManager().shutdown();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } catch (Exception e) {
            Log.i("NO_RESPONSE", response);
            response = NO_RESPONSE;
            // response = DriverMembers.NO_RESPONSE;
            e.printStackTrace();

        }

        return response;

    }

    public static String call_registration_post(String result, List<NameValuePair> postparameter) {
        try {
            int statuscode;
            StringBuilder builder = new StringBuilder();
            InputStream inputstram = null;
            BufferedReader bufferread = null;
            HttpEntity httpentity = null;
            StatusLine statusline;
            DefaultHttpClient defaluthttp = null;
            HttpPost post;
            try {

                defaluthttp = new DefaultHttpClient();
                post = new HttpPost(result);
                System.out.println("url value is url is" + result);
                System.out.println("URL----" + PhotocontentApi.registration_url);

                UrlEncodedFormEntity urlencoeded = new UrlEncodedFormEntity(postparameter);
                post.setEntity(httpentity);
                HttpResponse httpResponse = defaluthttp.execute(post);

                statusline = httpResponse.getStatusLine();
                statuscode = statusline.getStatusCode();


                if (statuscode == 200) {

                    httpentity = httpResponse.getEntity();
                    System.out.println("httpEntity----" + httpentity);
                    inputstram = httpentity.getContent();
                    System.out.println("is>>>>>----" + inputstram);
                    bufferread = new BufferedReader(new InputStreamReader(inputstram));
                    System.out.println("br>>>>>>>>----" + bufferread);
                    while ((response = bufferread.readLine()) != null) {

                        builder.append(response);
                        System.out.println("json>>>>>>>>----" + builder);
                    }

                    response = builder.toString();
                    Log.i("Response From Server", response);

                } else {

                    response = NO_RESPONSE;
                }


            } catch (HttpResponseException hre) {
                response = NO_RESPONSE;
                hre.printStackTrace();
            } catch (HttpHostConnectException hce) {
                response = NO_RESPONSE;
                hce.printStackTrace();
            } catch (UnknownHostException e) {
                response = UNKNOWN_HOST;
            } catch (NoHttpResponseException nr) {
                response = NO_RESPONSE;
                nr.printStackTrace();
            } catch (ConnectTimeoutException cte) {
                response = TIMEOUT_EXCEPTION;
                cte.printStackTrace();
            } catch (SocketTimeoutException ste) {
                response = SOCKETTIMEOUT_EXCEPTION;
                ste.printStackTrace();
            } catch (SocketException se) {
                response = SOCKET_EXCEPTION;
                se.printStackTrace();
            } catch (Exception e) {
                response = UNABLE_TO_CONNECT;
                e.printStackTrace();
            } finally {

                try {
                    if (bufferread != null) {
                        bufferread.close();
                    }

                    if (inputstram != null) {
                        inputstram.close();
                        defaluthttp.getConnectionManager().shutdown();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }


    public static String getAllPhotos(String getallimages_url) {
        try {
            int statusCode;
            StringBuilder sb = new StringBuilder();
            InputStream is = null;
            HttpEntity httpen;
            BufferedReader buff = null;
            StatusLine sl;
            DefaultHttpClient defclient = null;
            HttpGet httpget;
            try {
                defclient = new DefaultHttpClient();
                httpget = new HttpGet(getallimages_url);
                System.out.println("httpget---" + httpget);
                HttpResponse httpResponse = defclient.execute(httpget);

                sl = httpResponse.getStatusLine();
                statusCode = sl.getStatusCode();

                if (statusCode == 200) {

                    httpen = httpResponse.getEntity();
                    is = httpen.getContent();
                    buff = new BufferedReader(new InputStreamReader(is));

                    while ((response = buff.readLine()) != null) {

                        sb.append(response);
                    }

                    response = sb.toString();

                    Log.i("Response From Server", response);

                } else {

                    // response = DriverMembers.NO_RESPONSE;
                }

            } catch (HttpResponseException hre) {
                response = NO_RESPONSE;
                hre.printStackTrace();
            } catch (HttpHostConnectException hce) {
                response = HTTP_HOST_EXCEPTION;
                hce.printStackTrace();
            } catch (UnknownHostException e) {
                response = UNKNOWN_HOST;
            } catch (NoHttpResponseException nr) {
                response = NO_RESPONSE;
                nr.printStackTrace();
            } catch (ConnectTimeoutException cte) {
                response = TIMEOUT_EXCEPTION;
                cte.printStackTrace();
            } catch (SocketTimeoutException ste) {
                response = SOCKETTIMEOUT_EXCEPTION;
                ste.printStackTrace();
            } catch (SocketException se) {
                response = SOCKET_EXCEPTION;
                se.printStackTrace();
            } catch (Exception e) {
                response = UNABLE_TO_CONNECT;
                e.printStackTrace();
            } finally {

                try {
                    if (buff != null) {
                        buff.close();
                    }

                    if (is != null) {
                        is.close();
                        defclient.getConnectionManager().shutdown();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } catch (Exception e) {

            // response = DriverMembers.NO_RESPONSE;
            e.printStackTrace();

        }

        return response;

    }


    public static String ResponseForUploadimage(String user_id, String title, String location, String userfile, String description, String category_id, String active, String created_by) {

        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("user_id", user_id));
        postParameters.add(new BasicNameValuePair("title", title));
        postParameters.add(new BasicNameValuePair("location", location));
        postParameters.add(new BasicNameValuePair("userfile", userfile));
        postParameters.add(new BasicNameValuePair("description", description));
        postParameters.add(new BasicNameValuePair("category_id", category_id));
        postParameters.add(new BasicNameValuePair("active", active));
        postParameters.add(new BasicNameValuePair("created_by", created_by));

        return CallPostParameters(updateProfileUrl, postParameters);
    }


    private static String CallPostParameters(String updateProfileUrl, List<NameValuePair> postParameters) {
        String response_str = "";

        strictMode();

        BufferedReader bufferedReader = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(updateProfileUrl);

        try {

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParameters);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer stringBuffer = new StringBuffer("");
            String line = "";
            String LineSeparator = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + LineSeparator);
            }

            bufferedReader.close();
            response_str = stringBuffer.toString();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("get " + updateProfileUrl + " Result>>>>>>>>>>>>: " + response_str);

        return response_str;
    }

    private static void strictMode() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


    public static String ResponseForTodayImage(String user_id) {
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("user_id", user_id));
        return CallTodayImage(url, postParameters);
    }

    private static String CallTodayImage(String url, List<NameValuePair> postParameters) {
        String response_str ="";

        strictMode();

        BufferedReader bufferedReader = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(url);

        try {

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParameters);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer stringBuffer = new StringBuffer("");
            String line = "";
            String LineSeparator = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + LineSeparator);
            }

            bufferedReader.close();
            response_str = stringBuffer.toString();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("get "+url+" Result>>>>>>>>>>>>: "+response_str);

        return  response_str;
    }


    public static String ResponseForAllImages(String user_id) {
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("user_id", user_id));
        return CallMyAllImage(allimageurl, postParameters);
    }

    private static String CallMyAllImage(String allimageurl, List<NameValuePair> postParameters) {
        String response_str ="";

        strictMode();

        BufferedReader bufferedReader = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(allimageurl);

        try {

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParameters);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer stringBuffer = new StringBuffer("");
            String line = "";
            String LineSeparator = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + LineSeparator);
            }

            bufferedReader.close();
            response_str = stringBuffer.toString();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("get "+allimageurl+" Result>>>>>>>>>>>>: "+response_str);

        return  response_str;
    }

    public static String ResponseForTopTenImages() {
        return CallPostparameter(toptenimage_url);
    }

    private static String CallPostparameter(String toptenimage_url) {
        String response_str = "";

        strictMode();

        BufferedReader bufferedReader = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(toptenimage_url);

        try {

           /* UrlEncodedFormEntity entity = new UrlEncodedFormEntity();
            request.setEntity(entity);*/
            HttpResponse response = httpClient.execute(request);
            bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer stringBuffer = new StringBuffer("");
            String line = "";
            String LineSeparator = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + LineSeparator);
            }

            bufferedReader.close();
            response_str = stringBuffer.toString();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("get " + toptenimage_url + " Result>>>>>>>>>>>>: " + response_str);

        return response_str;
    }

    public static String ResponseForTodayAllPhotos() {
        Log.v("alluser details ",CallPostparameter(allusertoday_photosurl));
        return CallPostparameter(allusertoday_photosurl);
    }

    public static String responseForVoteDetail(String userid, String photomaster_id, String created_by_str, String rating_str, String rating_time) {
        List<NameValuePair> parameter=new ArrayList<NameValuePair>();
        parameter.add(new BasicNameValuePair("Photo_master_id", photomaster_id));
        parameter.add(new BasicNameValuePair("userid", userid));
        parameter.add(new BasicNameValuePair("rating_value", rating_str));
        parameter.add(new BasicNameValuePair("ratingdate", rating_time));
        parameter.add(new BasicNameValuePair("created_by", created_by_str));
        return callPostParamer(getmyphotovote_url,parameter);
    }

    private static String callPostParamer(String getmyphotovote_url, List<NameValuePair> parameter) {
        String response_str ="";

        strictMode();

        BufferedReader bufferedReader = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(getmyphotovote_url);

        try {

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameter);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer stringBuffer = new StringBuffer("");
            String line = "";
            String LineSeparator = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + LineSeparator);
            }

            bufferedReader.close();
            response_str = stringBuffer.toString();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("get "+getmyphotovote_url+"getmyphotovote_urlgetmyphotovote_url"+response_str);

        return  response_str;
    }

    public static String ResponseForPhotooftheDay() {

        return CallPostparameter(photo_of_the_day);
    }

    public static String responseForallVoteDetails(String photomaster_id) {
        List<NameValuePair> parameters=new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("photomaster_id", photomaster_id));
        System.out.println("urllllllllllllllllll"+vote_photos_details_url);
        return callPostParaterVotedetail(vote_photos_details_url,parameters);
    }

    private static String callPostParaterVotedetail(String vote_photos_details_url, List<NameValuePair> parameters) {
        String response_str ="";

        strictMode();

        BufferedReader bufferedReader = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(vote_photos_details_url);

        try {

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer stringBuffer = new StringBuffer("");
            String line = "";
            String LineSeparator = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + LineSeparator);
            }

            bufferedReader.close();
            response_str = stringBuffer.toString();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("get "+vote_photos_details_url+"vote_photos_details_urlvote_photos_details_url"+response_str);

        return  response_str;
    }

    public static String ResponseForSpamUser(String user_id, String description, String photomaster_id, String spamuserid) {
        List<NameValuePair> parameters=new ArrayList<NameValuePair>();
        Log.v("adapteruserid imna",user_id+spamuserid);
        parameters.add(new BasicNameValuePair("photomaster_id", photomaster_id));
        parameters.add(new BasicNameValuePair("user_id", user_id));
        parameters.add(new BasicNameValuePair("description", description));
        parameters.add(new BasicNameValuePair("spamuser_id", spamuserid));
        return callPostParaterSpamdetail(spam_details_url, parameters);
    }

    private static String callPostParaterSpamdetail(String spam_details_url, List<NameValuePair> parameters) {
        String response_str ="";

        strictMode();

        BufferedReader bufferedReader = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(spam_details_url);

        try {

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer stringBuffer = new StringBuffer("");
            String line = "";
            String LineSeparator = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + LineSeparator);
            }

            bufferedReader.close();
            response_str = stringBuffer.toString();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("get "+spam_details_url+"vote_photos_details_urlvote_photos_details_url"+response_str);

        return  response_str;
    }

    public static String ResponseForRatingUser(String userid,String date_str, String rating_value,String title_str, String photomasterid, String created_by,String comments) {
       System.out.println("all details are here  userid"+userid+"date"+date_str+"rating"+rating_value+"title"+title_str+"photomaster"+photomasterid+"craeter"+created_by+"commeny"+comments);
        List<NameValuePair> parameters=new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("photomaster_id", photomasterid));
        parameters.add(new BasicNameValuePair("user_id", userid));
        parameters.add(new BasicNameValuePair("created_by", created_by));
        parameters.add(new BasicNameValuePair("comments", comments));
        parameters.add(new BasicNameValuePair("rating",rating_value));
        parameters.add(new BasicNameValuePair("rating_datetime",date_str));
        parameters.add(new BasicNameValuePair("title",title_str));

        return callPostParaterRate(getmyphotovote_url, parameters);
    }

    private static String callPostParaterRate(String getmyphotovote_url, List<NameValuePair> parameters) {
        String response_str ="";

        strictMode();

        BufferedReader bufferedReader = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(getmyphotovote_url);

        try {

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer stringBuffer = new StringBuffer("");
            String line = "";
            String LineSeparator = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + LineSeparator);
            }

            bufferedReader.close();
            response_str = stringBuffer.toString();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("get "+getmyphotovote_url+"vote_photos_details_urlvote_photos_details_url"+response_str);

        return  response_str;
    }

    public static String ResponseForTodayPhoto() {
        return CallPostparameter(allusertoday_photosurl);
    }

    public static String refressVotePhotoDetail(String photomas_id) {
        List<NameValuePair> parameters=new ArrayList<NameValuePair>();
        Log.v("adapteruserid imnais",photomas_id);
        parameters.add(new BasicNameValuePair("photomaster_id", photomas_id));
        return CallRefressMethod(getmyphotos_votedetails_url, parameters);
    }

    private static String CallRefressMethod(String getmyphotos_votedetails_url, List<NameValuePair> parameters) {
        String response_str ="";

        strictMode();

        BufferedReader bufferedReader = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(getmyphotos_votedetails_url);

        try {

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer stringBuffer = new StringBuffer("");
            String line = "";
            String LineSeparator = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + LineSeparator);
            }

            bufferedReader.close();
            response_str = stringBuffer.toString();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("get "+getmyphotos_votedetails_url+"vote_photos_details_urlvote_photos_details_url"+response_str);

        return  response_str;
    }


    public static String callprofileApi(String userid) {
        List<NameValuePair> parameters=new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("user_id", userid));
        return callProfile(getprofile_url,parameters);
    }

    private static String callProfile(String getprofile_url, List<NameValuePair> parameters) {
        String response_str ="";

        strictMode();

        BufferedReader bufferedReader = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(getprofile_url);

        try {

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer stringBuffer = new StringBuffer("");
            String line = "";
            String LineSeparator = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + LineSeparator);
            }

            bufferedReader.close();
            response_str = stringBuffer.toString();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("get "+getprofile_url+"vote_photos_details_urlvote_photos_details_url"+response_str);

        return  response_str;
    }

    public static String callUpdateProfile(String userid, String temp, String firstname_str, String lastname_str, String email_str, String mobile_str) {
        List<NameValuePair> parameters=new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("user_id",userid));
        parameters.add(new BasicNameValuePair("userfile",temp));
        parameters.add(new BasicNameValuePair("first_name", firstname_str));
        parameters.add(new BasicNameValuePair("last_name",lastname_str));
        parameters.add(new BasicNameValuePair("address",email_str));
        parameters.add(new BasicNameValuePair("phone_no",mobile_str));
        return CallRefressMethod(getupdateprofile_url, parameters);
    }

    public static String callFacebookLogin(String fname, String lName, String userid, String name, String mailid) {
        List<NameValuePair> parameters=new ArrayList<>();
    parameters.add(new BasicNameValuePair("first_name",fname));
    parameters.add(new BasicNameValuePair("last_name",lName));
    parameters.add(new BasicNameValuePair("facebook_id",userid));
    parameters.add(new BasicNameValuePair("facebook_username",name));
    parameters.add(new BasicNameValuePair("email_id",mailid));
    return CallFacebookMethod(getfacebook_url,parameters);}

    private static String CallFacebookMethod(String getfacebook_url, List<NameValuePair> parameters) {
        String response_str ="";

        strictMode();

        BufferedReader bufferedReader = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(getfacebook_url);

        try {

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer stringBuffer = new StringBuffer("");
            String line = "";
            String LineSeparator = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + LineSeparator);
            }

            bufferedReader.close();
            response_str = stringBuffer.toString();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("get "+getfacebook_url+"vote_photos_details_urlvote_photos_details_url"+response_str);

        return  response_str;}

    public static String ResponseForLatestHistory(String user_id) {
        List<NameValuePair> parameters=new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("user_id",user_id));
    return callLatestHistory(getLatestHistory_url,parameters);}

    private static String callLatestHistory(String getLatestHistory_url, List<NameValuePair> parameters) {
        String response_str ="";

        strictMode();

        BufferedReader bufferedReader = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(getLatestHistory_url);

        try {

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer stringBuffer = new StringBuffer("");
            String line = "";
            String LineSeparator = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + LineSeparator);
            }

            bufferedReader.close();
            response_str = stringBuffer.toString();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("get "+getLatestHistory_url+"vote_photos_details_urlvote_photos_details_url"+response_str);

        return  response_str;}
}


