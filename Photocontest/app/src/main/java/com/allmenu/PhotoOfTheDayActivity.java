package com.allmenu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import photocontent.bliss.com.api.ConnectApi;
import photocontest.bliss.com.photocontest.CustomalertDialog;
import photocontest.bliss.com.photocontest.NetworkValidation;
import photocontest.bliss.com.photocontest.R;


/**
 * Created by Jenifa Mary.C on 5/2/2015.
 */
public class PhotoOfTheDayActivity extends Activity{
    ImageView back_btn,main_image;
    RatingBar main_ratingbar,sub_ratingbar;
    ProgressDialog progress,progressdialog;
    SVG todayphotos_pg_svg;
    String userid;
    String photoofthe_day_response,photo,your_vote,no_ofvote,average_rating,photomasterid,votePhoto_details,username,rating,comments_str;
    PhotoDayModel photo_master;
    ArrayList<PhotoDayModel> photoofday_arraylist;
    AdapterforPhotoDay photo_adapter;
    InterstitialAd interstitial;
    EasyTracker easyTracker = null;
    Context context;
    CustomalertDialog alert = new CustomalertDialog();
    Float myrating,averagerating;
    int numberbasedAct;
    RelativeLayout subtitle_layout;
    TextView heading,num_vote,votes;
    ListView photo_ofday_list;
    RelativeLayout photo_day_content;
    TextView photo_day_details;
    int width,height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_day);
        Initialize();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width= size.x;
        height = size.y;

        try{
             Intent numbers=getIntent();
             numberbasedAct=numbers.getIntExtra("photo_rank_day",0);

            if(numberbasedAct==2){
                //image_back
                back_btn.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                todayphotos_pg_svg = SVGParser.getSVGFromResource(getResources(), R.raw.back);
                back_btn.setImageDrawable(todayphotos_pg_svg.createPictureDrawable());
                heading.setText("Ranking");
                subtitle_layout.setVisibility(View.GONE);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        easyTracker = EasyTracker.getInstance(context);
        if(numberbasedAct!=2) {
            setAllSvgImages();
        }
        showAd();
        try {
            if (NetworkValidation.checknetConnection(getApplicationContext())) {
                apiIntegrationForPhotoofTheDay();
            }
            else{
                alert.ShowAlert(PhotoOfTheDayActivity.this, "Check Your Internet Connection");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showAd() {
        interstitial = new InterstitialAd(this);
        // Insert the Ad Unit ID
        interstitial.setAdUnitId(getResources().getString(R.string.adUnitId));
        // Locate the Banner Ad in activity_main.xml
        AdView adView = (AdView) this.findViewById(R.id.adView);
        // Request for Ads
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("").build();// Add a test device to show Test Ads
        // Load ads into Banner Ads
        adView.loadAd(adRequest);
        // Load ads into Interstitial Ads
        interstitial.loadAd(adRequest);
        // Prepare an Interstitial Ad Listener
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
                // Call displayInterstitial() function
                displayInterstitial();
            }
        });
    }

    private void displayInterstitial() {
        // If Ads are loaded, show Interstitial else show nothing.
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }

    private void apiIntegrationForPhotoofTheDay() {
        new behindActivity().execute();
    }

    class behindActivity extends AsyncTask<Void,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=new ProgressDialog(PhotoOfTheDayActivity.this);
            progress.setMessage("Loading");
            progress.setCanceledOnTouchOutside(false);
            progress.setCancelable(true);
            progress.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                photoofthe_day_response = ConnectApi.ResponseForPhotooftheDay();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return photoofthe_day_response;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            try {
                parsethePhotooftheday(photoofthe_day_response);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void parsethePhotooftheday(String photoofthe_day_response) {
        JSONObject photoday_obj;
        progress.dismiss();
        try{
           photoday_obj=new JSONObject(photoofthe_day_response);

            if (Boolean.valueOf(photoday_obj.getString("status")) == true) {
                JSONArray photoday_array = photoday_obj.getJSONArray("photooftheday");
                for (int i = 0; i < photoday_array.length(); i++) {
                    JSONObject photoday_sub_obj = photoday_array.getJSONObject(i);

                    photo = photoday_sub_obj.getString("photo_path");
                    your_vote = photoday_sub_obj.getString("rating");
                    no_ofvote = photoday_sub_obj.getString("no_votes");
                    average_rating = photoday_sub_obj.getString("average_rating");
                    photomasterid = photoday_sub_obj.getString("photomaster_id");

                    myrating = Float.parseFloat(your_vote);
                    averagerating = Float.parseFloat(average_rating);


                    main_ratingbar.setRating(myrating);
                    sub_ratingbar.setRating(averagerating);
                    num_vote.setText(no_ofvote);
                    Picasso.with(PhotoOfTheDayActivity.this).load(photo).placeholder(R.drawable.animation_loding)
                            .noFade().resize(3000, 1500).into(main_image);

                    try {
                        if (NetworkValidation.checknetConnection(getApplicationContext())) {
                            methodForVoteDetails();
                        }
                        else{
                            alert.ShowAlert(PhotoOfTheDayActivity.this,"Check Your Internet Connection");
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
           else{
                photo_day_details.setVisibility(View.VISIBLE);
            }





        }
        catch(Exception e){
            e.printStackTrace();
        }
        return;
    }

    private void methodForVoteDetails() {
    try{
     new asynVoteDetails().execute();
    }
    catch(Exception e){
        e.printStackTrace();
    }
    }

    private void ResizeimageMethod(String photo) {
        try {

        }
    catch (Exception e){
    e.printStackTrace();}
    }

    private void setAllSvgImages() {
        try {
            //Setting svg image for back_btn
            back_btn.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            todayphotos_pg_svg = SVGParser.getSVGFromResource(getResources(), R.raw.home);
            back_btn.setImageDrawable(todayphotos_pg_svg.createPictureDrawable());
        }
    catch(Exception e){
    e.printStackTrace();}
    }

    private void Initialize() {
        back_btn=(ImageView)findViewById(R.id.back_btn);
        main_ratingbar=(RatingBar)findViewById(R.id.ratingBar_one);
        sub_ratingbar=(RatingBar)findViewById(R.id.ratingBar_center);
        num_vote=(TextView)findViewById(R.id.num_vote);
        photo_day_content=(RelativeLayout)findViewById(R.id.photo_day_content);

        photo_day_details=(TextView)findViewById(R.id.photo_day_details);
        photo_day_details.setVisibility(View.GONE);
        photo_ofday_list=(ListView)findViewById(R.id.photo_ofday_list);
        heading=(TextView)findViewById(R.id.heading);
        subtitle_layout=(RelativeLayout)findViewById(R.id.subtitle_layout);
        votes=(TextView)findViewById(R.id.total_vote);
        main_image=(ImageView)findViewById(R.id.image_view);
        photoofday_arraylist=new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }


    class asynVoteDetails extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*progressdialog=new ProgressDialog(PhotoOfTheDayActivity.this);
            progressdialog.setTitle("Loading");
            progressdialog.setCancelable(true);
            progressdialog.setCanceledOnTouchOutside(false);
            progressdialog.show();*//**/
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                votePhoto_details = ConnectApi.responseForallVoteDetails(photomasterid);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress.dismiss();
            try {

                jsonParsetheAllDetails(votePhoto_details);
            }
        catch(Exception e){
        e.printStackTrace();}
        }
    }

    private void jsonParsetheAllDetails(String votePhoto_details) {
        JSONObject todayimage= null;

        try{
            todayimage = new JSONObject(votePhoto_details);
            if (Boolean.valueOf(todayimage.getString("status")) == true) {
            photoofday_arraylist=new ArrayList<>();
            JSONArray jsonarray = todayimage.getJSONArray("mytodayphotos");

            for(int i=0;i<jsonarray.length();i++) {
                JSONObject json_obj = jsonarray.getJSONObject(i);
                username = json_obj.getString("created_by");
                rating = json_obj.getString("rating");
                comments_str = json_obj.getString("comments");
                userid = json_obj.getString("user_id");

                photo_master = new PhotoDayModel();
                photo_master.setComments(comments_str);
                photo_master.setName(username);
                photo_master.setYour_vote(rating);
                photo_master.setUserid(userid);

                photoofday_arraylist.add(photo_master);

            }
                if(photoofday_arraylist!=null){
                    try {
                        if(photoofday_arraylist.size()>0) {

                            photo_day_details.setVisibility(View.GONE);
                            photo_day_content.setVisibility(View.VISIBLE);
                            photo_adapter = new AdapterforPhotoDay(PhotoOfTheDayActivity.this, photoofday_arraylist);
                            photo_ofday_list.setAdapter(photo_adapter);
                        }
                        else{
                            photo_day_content.setVisibility(View.GONE);
                            photo_day_details.setVisibility(View.VISIBLE);
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();}
                }
             }

    }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(photoofday_arraylist!=null){
            try {
                if(photoofday_arraylist.size()>0) {
                    photo_day_details.setVisibility(View.INVISIBLE);
                    photo_adapter = new AdapterforPhotoDay(PhotoOfTheDayActivity.this, photoofday_arraylist);
                    photo_ofday_list.setAdapter(photo_adapter);
                }
                else{
                    photo_day_content.setVisibility(View.INVISIBLE);
                }
            }
        catch (Exception e){
        e.printStackTrace();}
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        progress.dismiss();

    }
}
