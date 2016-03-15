package com.allmenu;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import photocontent.bliss.com.api.ConnectApi;
import photocontent.bliss.com.api.ServiceHandler;
import photocontent.bliss.com.api.ServiceHandlerUrl;
import photocontest.bliss.com.photocontest.CustomalertDialog;
import photocontest.bliss.com.photocontest.NetworkValidation;
import photocontest.bliss.com.photocontest.R;

/**
 * Created by Jenifa Mary.C on 4/30/2015.
 */
public class VotePhoto extends Activity{
    ImageView vote_img_hme;
    SVG camera_snap_pg_svg;
    ProgressDialog pd,pg;

    GridView vote_photo_grid;
    TextView photos_details;
    String votePhoto_details;
    ArrayList<String> images_list;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String photomasterid,date_str,title_str,created_by_str;
    String imagepagepath,topten_final,userid,created_date,created_time,title,rank,spamuserid,description,photomas_id,todayphotos_final,rating;
    ServiceHandler sh;
    Context context;
    EasyTracker easyTracker = null;
    SharedPreferences pref;
    Boolean VoteDetailUpload=false;
    Boolean photoviewresponse=true;
    Vote_model model;
    ArrayList<Vote_model> model_arraylist;
    AdapterForVoteDetails allimageadat;
    InterstitialAd interstitial;
    int activitynumber;
    TextView votephot;
    Boolean topten=false;
    static VotePhoto act;
    int toptenimagerefres;
    Dialog dialog;
    TextView dialog_title;
    EditText description_edt;
    ImageView image;
    RatingBar rating_rat;
    String dialog_title_str,dialog_rating_str,description_str,rating_value,vote_img_final;
    CustomalertDialog alert = new CustomalertDialog();
    int value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.vote_photo);
        Initialize();
        svgimagesetting();
        showAd();

        context=this;
        act=this;
        easyTracker = EasyTracker.getInstance(context);
        vote_photo_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                value=1;

                try {

                    // Create custom dialog object

                    dialog = new Dialog(context);
                    // Set dialog title
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    // Include dialog.xml file
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(false);

                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View vote_view = inflater.inflate(R.layout.votephoto_upload, null, false);
                    dialog.setContentView(vote_view);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    Window window = dialog.getWindow();
                    lp.copyFrom(window.getAttributes());
                    //This makes the dialog take up the full width
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    window.setAttributes(lp);


                    // set values for custom dialog components - text, image and button
                    dialog_title = (TextView) dialog.findViewById(R.id.title_upload_edt);
                    description_edt = (EditText) dialog.findViewById(R.id.description_edt);
                    image = (ImageView) dialog.findViewById(R.id.vote_snap_img);
                    rating_rat = (RatingBar) dialog.findViewById(R.id.server_ratingBar);
                    try {
                        dialog_title_str = model_arraylist.get(position).getTitle();
                        System.out.println("tttttttttttttt"+dialog_title_str);
                        dialog_rating_str = model_arraylist.get(position).getRating();
                        photomasterid = model_arraylist.get(position).getPhotomaster_id();
                        date_str=model_arraylist.get(position).getDate();
                        title_str=model_arraylist.get(position).getTitle();


                        // dialog_rating_float = Float.parseFloat(ConvertValue(dialog_rating_str));
                        description_str=model_arraylist.get(position).getDescription();
                        rating_value ="";
                        description_edt.setText(description_str);
                        dialog_title.setText(dialog_title_str);
                        dialog_title.setEnabled(false);

           /* rating.setRating(dialog_rating_float);*/
                        Picasso.with(context).load(model_arraylist.get(position).getImagepath())
                                .placeholder(R.drawable.animation_loding).resize(300, 300).into(image);
                        description_str = description_edt.getText().toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    rating_rat.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating,
                                                    boolean fromUser) {
                           // vote_float = rating;
                            rating_value = String.valueOf(rating);


                        }
                    });


                    TextView submit = (TextView) dialog.findViewById(R.id.submit_btn);
                    dialog.show();


                    // if decline button is clicked, close the custom dialog
                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            
                            try {
                                validationForUpdateVote();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        try{
            Intent votephoto=getIntent();
            userid=votephoto.getStringExtra("userid");
            activitynumber=votephoto.getIntExtra("votephotointent",0);
        }
catch (Exception e){
    e.printStackTrace();
}

    vote_img_hme.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    });
    }

    private void validationForUpdateVote() {
        try {
            if (rating_value.trim().equalsIgnoreCase("")) {
                System.out.println("rating avlue"+rating_value);
                alert.ShowAlert(VotePhoto.this, "Select Rating");
            }
            else if (NetworkValidation.checknetConnection(context)) {
                try {
                    VoteDetailUpload=true;
                    photoviewresponse=false;
                    new VoteUpdateDetails().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                alert.ShowAlert(context, "Check Your Internet Connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public  void callAsynTask() {

        new behindActivity().execute();
    }

    public void callAsynTaskRefresh() {
        //System.out.println("sdjhcghsdcvfdghvdgjvfdhgvfdjhvhvbhgjdv"+userid);
        topten=true;
        photoviewresponse=false;
        VoteDetailUpload=false;
        toptenimagerefres=1;
        activitynumber=2;
        new behindActivity().execute();
    }


    class behindActivity extends AsyncTask<Void,Void,Void>{
      //  ProgressDialog pd;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(toptenimagerefres==1){

        }
            pd = new ProgressDialog(VotePhoto.this);
            pd.setMessage("Loading");
            pd.setCancelable(true);
            pd.setCanceledOnTouchOutside(false);
            pd.show();


    }

    @Override
    protected Void doInBackground(Void... params) {
        if(photoviewresponse==true){
            try{
                photoviewresponse=true;
                VoteDetailUpload=false;
                todayphotos_final =ConnectApi.ResponseForTodayPhoto();
               }
           catch (Exception e){
               e.printStackTrace();
           }
        }

            if(topten==true){
                try{
                    topten_final= ConnectApi.ResponseForTopTenImages();
                    parsethejsonresponse(topten_final);
                }
                catch (Exception e){
                    e.printStackTrace();

                }
            }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        pd.dismiss();

        if(photoviewresponse==true){

            photoviewresponse=true;
            VoteDetailUpload=false;
            try {
                readandparsejson_imageurl(todayphotos_final);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        if(VoteDetailUpload==true){

            photoviewresponse=false;
            VoteDetailUpload=true;
            try {
                jsonParsetheAllDetails(votePhoto_details);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        if(VoteDetailUpload==true){

        }

        if(model_arraylist!=null){
            int activity_value=1;
            try {
                if(model_arraylist.size()>0) {
                    photos_details.setVisibility(View.GONE);
                    vote_photo_grid.setVisibility(View.VISIBLE);
                    allimageadat = new AdapterForVoteDetails(VotePhoto.this, model_arraylist, activity_value);
                    vote_photo_grid.setAdapter(allimageadat);
                    allimageadat.notifyDataSetChanged();

                }
                else{
                    vote_photo_grid.setVisibility(View.GONE);
                    photos_details.setVisibility(View.VISIBLE);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }
    }

}


    @Override
    protected void onResume() {

        super.onResume();
        if(activitynumber==1) {
            photoviewresponse=true;
            try {
                callAsynTask();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        if(activitynumber==2){
            votephot.setText("Top 10 Photos");
            topten=true;
            try {
                callAsynTask();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void parsethejsonresponse(String topten_final) {
        String msg = null;
        try{
            JSONObject jsonobj=new JSONObject(topten_final);

            if(Boolean.valueOf(jsonobj.getString("status"))) {
                msg = jsonobj.getString("msg");
                boolean status = Boolean.valueOf(jsonobj.getString("status"));
                msg = jsonobj.getString("msg");
            }

            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private ArrayList<Vote_model> readandparsejson_imageurl(String allimage_img_final) {
        JSONObject todayimage = null;
        try {
            todayimage = new JSONObject(allimage_img_final);
          /*  images_list = new ArrayList<String>();*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (Boolean.valueOf(todayimage.getString("status")) == true) {
            JSONArray jsonarray = todayimage.getJSONArray("todayphotos");
            model_arraylist.clear();
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject json_obj = jsonarray.getJSONObject(i);

                imagepagepath = json_obj.getString("photo_path");
                created_date = json_obj.getString("created_date");
                created_time = json_obj.getString("created_time");
                title = json_obj.getString("title");
                rank = json_obj.getString("rank");
                rating = json_obj.getString("average_rating");
                spamuserid = json_obj.getString("user_id");
                description = json_obj.getString("description");
                photomas_id = json_obj.getString("photomaster_id");


                model = new Vote_model();
                model.setImagepath(imagepagepath);
                model.setTime(created_time);
                model.setDate(created_date);
                model.setTitle(title);
                model.setRanking(rank);
                model.setRating(rating);

                model.setSpamuserid(spamuserid);
                model.setDescription(description);
                model.setPhotomaster_id(photomas_id);
                model_arraylist.add(model);

            }
            }
            else{
                photos_details.setVisibility(View.VISIBLE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return model_arraylist;
    }

    private void jsonParsetheAllDetails(String votePhoto_details) {
        JSONObject todayimage= null;
        try {
            todayimage = new JSONObject(votePhoto_details);
            images_list=new ArrayList<String>();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try{
            JSONArray jsonarray = todayimage.getJSONArray("mytodayphotos");
                for(int i=0;i<jsonarray.length();i++) {
                    JSONObject json_obj = jsonarray.getJSONObject(i);
                    imagepagepath = json_obj.getString("photo_path");
                }}
        catch (Exception e){
            e.printStackTrace();
        }
                    return ;
                }


    private void svgimagesetting() {
        try {
            //Setting svg image for back_btn
            vote_img_hme.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            camera_snap_pg_svg = SVGParser.getSVGFromResource(getResources(), R.raw.home);
            vote_img_hme.setImageDrawable(camera_snap_pg_svg.createPictureDrawable());
        }
    catch(Exception e){
    e.printStackTrace();}
    }

    private void Initialize() {
         votephot=(TextView)findViewById(R.id.votephoto);
         vote_img_hme = (ImageView) findViewById(R.id.vote_photo_home_img);
         vote_photo_grid=(GridView)findViewById(R.id.vote_photo_grid);
         photos_details=(TextView)findViewById(R.id.photos_details);
         model_arraylist=new ArrayList<Vote_model>();
        photos_details.setVisibility(View.GONE);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(value==1) {
            dialog.dismiss();
        }
        if(value==2) {
            pd.dismiss();
        }

    }

    class VoteUpdateDetails extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg=new ProgressDialog(VotePhoto.this);
            pg.setTitle("Loading");
            pg.setCancelable(true);
            pg.setCanceledOnTouchOutside(false);
            pg.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                try {
                    pref = context.getSharedPreferences("Options", context.MODE_PRIVATE);
                    created_by_str = pref.getString("FirstName_Key", "");
                    System.out.println("created by created by" + created_by_str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("all deta"+  userid+ date_str+ rating_value+title_str+photomasterid+ created_by_str+description_str );
                vote_img_final = ConnectApi.ResponseForRatingUser(userid, date_str, rating_value, title_str, photomasterid, created_by_str, description_str);
                System.out.println("votephotodetails are"+vote_img_final);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pg.dismiss();
            try {
                parsethevote_photouploadresp(vote_img_final);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void parsethevote_photouploadresp(String vote_img_final) {

        dialog.dismiss();
        try {

            JSONObject jsonobj = new JSONObject(vote_img_final);

            if (Boolean.valueOf(jsonobj.getString("status"))) {
                Toast.makeText(context, "Vote Updated Successfully", Toast.LENGTH_SHORT).show();
                photoviewresponse=true;
                try {
                    callAsynTask();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            else {
                alert.ShowAlert(context,"Something Missing");

            }


        } catch (Exception e) {
            e.printStackTrace();
        }}
}
