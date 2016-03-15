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
import org.json.JSONObject;

import java.util.ArrayList;

import photocontent.bliss.com.api.ConnectApi;
import photocontest.bliss.com.photocontest.CustomalertDialog;
import photocontest.bliss.com.photocontest.NetworkValidation;
import photocontest.bliss.com.photocontest.R;


/**
 * Created by Jenifa Mary.C on 5/2/2015.
 */
public class ToptenimagesActivity extends Activity{
    ImageView home_img;
    GridView top_ten_grid;
    TextView photos_details;
    SVG topten_pg_svg;
    ProgressDialog pd;
    String topten_image_uri,todayphotos_final;
    static ToptenimagesActivity topten;

    SharedPreferences pref;
    Vote_model model;
    ArrayList<Vote_model> model_arraylist;
    AdapterForVoteDetails toptenimages;
    InterstitialAd interstitial;
    Dialog dialog;
    EasyTracker easyTracker = null;
    Context context;
    String imagepagepath,date,time,title,rating,ranking,photomaster_id,spam_suerid;
     TextView dialog_title;
    EditText description_edt;
    ImageView image;
    RatingBar rating_rat;
    CustomalertDialog alert = new CustomalertDialog();
    boolean photoviewresponse,VoteDetailUpload;
    String userid,vote_img_final,created_by_str,dialog_title_str,dialog_rating_str,photomasterid,date_str,title_str,description_str,rating_value;

    VotePhoto votephoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toptenimages_layout);
        easyTracker = EasyTracker.getInstance(context);
        votephoto=new VotePhoto();
        try {
            topten=this;
            showAd();
            Initialization();
            svgimagesetting();
            apiIntegrationForTopPhotos();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        home_img.setOnClickListener(new View.OnClickListener() {
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

    public  void apiIntegrationForTopPhotos() {
       new behindActivity().execute();
    }

    private void svgimagesetting() {
        try {
            //Setting svg image for back_btn
            home_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            topten_pg_svg = SVGParser.getSVGFromResource(getResources(), R.raw.home);
            home_img.setImageDrawable(topten_pg_svg.createPictureDrawable());
        }
    catch (Exception e){
    e.printStackTrace();}
    }

    private void Initialization() {
        home_img=(ImageView)findViewById(R.id.topten_photo_home_img);
        top_ten_grid=(GridView)findViewById(R.id.topten_photo_grid);
        photos_details=(TextView)findViewById(R.id.photos_details);
        model_arraylist=new ArrayList<Vote_model>();
        photos_details.setVisibility(View.GONE);
        top_ten_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create custom dialog object
                dialog = new Dialog(ToptenimagesActivity.this);
                // Set dialog title
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                // Include dialog.xml file
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(false);

           //     LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             //   View vote_view = inflater.inflate(R.layout.votephoto_upload, null, false);
                dialog.setContentView(R.layout.votephoto_upload);
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
                    System.out.println("photomasterid hgchscs"+photomasterid);
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
            }

        });
    }

    private void validationForUpdateVote() {
        try {
            if (rating_value.trim().equalsIgnoreCase("")) {
                System.out.println("rating avlue"+rating_value);
                alert.ShowAlert(ToptenimagesActivity.this, "Select Rating");
            }
            else if (NetworkValidation.checknetConnection(ToptenimagesActivity.this)) {
                try {

                   new VoteUpdateDetails().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                alert.ShowAlert(ToptenimagesActivity.this, "Check Your Internet Connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }}

    class behindActivity extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ToptenimagesActivity.this);
            pd.setMessage("loading...");
            pd.setCancelable(true);
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                topten_image_uri = ConnectApi.ResponseForTopTenImages();
            }
            catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
try {
    parsethejsonresponse(topten_image_uri);
}
        catch (Exception e){
        e.printStackTrace();}
        }
    }

    private String parsethejsonresponse(String response) {
        String msg = null;
        try{
            JSONObject jsonobj=new JSONObject(response);
            if(Boolean.valueOf(jsonobj.getString("status"))) {
                JSONArray jsonarray = jsonobj.getJSONArray("toptenphotos");
                model_arraylist.clear();
               /* jsonarray.length()*/
                System.out.println("jsonnnnnnnnnnnnnnnnnnarray"+jsonarray.length());

                if(jsonarray.length()>=9) {

                   System.out.println("jsonarray length is"+jsonarray.length());
                    for (int i = 0; i <= 9; i++) {
                        JSONObject json_obj = jsonarray.getJSONObject(i);

                        imagepagepath = json_obj.getString("photo_path");
                        date = json_obj.getString("created_date");
                        time = json_obj.getString("created_time");
                        title = json_obj.getString("title");
                        rating = json_obj.getString("average_rating");
                        ranking = json_obj.getString("rank");
                        photomaster_id = json_obj.getString("photomaster_id");
                        spam_suerid = json_obj.getString("user_id");

                        model = new Vote_model();
                        model.setImagepath(imagepagepath);
                        model.setTime(time);
                        model.setTitle(title);
                        model.setRating(rating);
                        model.setDate(date);
                        model.setRanking(ranking);
                        model.setPhotomaster_id(photomaster_id);
                        model.setSpamuserid(spam_suerid);
                        model_arraylist.add(model);
                        System.out.println("model arraylist" + model_arraylist);

                    }

                }

               else if(jsonarray.length()<10){
                    System.out.println("length of json array is"+jsonarray.length());
                    for (int i = 0; i <jsonarray.length(); i++) {
                        JSONObject json_obj = jsonarray.getJSONObject(i);

                        imagepagepath = json_obj.getString("photo_path");
                        date = json_obj.getString("created_date");
                        time = json_obj.getString("created_time");
                        title = json_obj.getString("title");
                        rating = json_obj.getString("average_rating");
                        ranking = json_obj.getString("rank");
                        photomaster_id = json_obj.getString("photomaster_id");
                        spam_suerid = json_obj.getString("user_id");

                        model = new Vote_model();
                        model.setImagepath(imagepagepath);
                        model.setTime(time);
                        model.setTitle(title);
                        model.setRating(rating);
                        model.setDate(date);
                        model.setRanking(ranking);
                        model.setPhotomaster_id(photomaster_id);
                        model.setSpamuserid(spam_suerid);
                        model_arraylist.add(model);
                        System.out.println("model arraylist" + model_arraylist);

                    }

                }






                if(model_arraylist!=null){
                    int activity_value=2;
                    top_ten_grid.setVisibility(View.VISIBLE);
                    photos_details.setVisibility(View.GONE);
                    toptenimages=new AdapterForVoteDetails(ToptenimagesActivity.this, model_arraylist,activity_value);
                    top_ten_grid.setAdapter(toptenimages);
                    toptenimages.notifyDataSetChanged();
                }
            }

            else{
                top_ten_grid.setVisibility(View.GONE);
                photos_details.setVisibility(View.VISIBLE);
            }






        }
        catch(Exception e){
            e.printStackTrace();
        }

        return msg;
    }

    class VoteUpdateDetails extends AsyncTask<Void,Void,Void>{
       /* @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ToptenimagesActivity.this);
            pd.setMessage("loading...");
            pd.setCancelable(true);
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }
*/
        @Override
        protected Void doInBackground(Void... params) {
            try {
                pref = getApplicationContext().getSharedPreferences("Options", MODE_PRIVATE);
                created_by_str = pref.getString("FirstName_Key", "");
                Intent votephoto=getIntent();
                userid=votephoto.getStringExtra("userid");
                System.out.println("created by created by" + created_by_str+"userid"+userid);


                System.out.println("all deta"+  userid+ date_str+ rating_value+title_str+photomasterid+ created_by_str+description_str );
                vote_img_final = ConnectApi.ResponseForRatingUser(userid, date_str, rating_value, title_str, photomasterid, created_by_str, description_str);
                System.out.println("votephotodetails are"+vote_img_final);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
              dialog.dismiss();
            try {
            parsethevote_photouploadresp(vote_img_final);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void parsethevote_photouploadresp(String vote_img_final) {

        try {

            JSONObject jsonobj = new JSONObject(vote_img_final);

            if (Boolean.valueOf(jsonobj.getString("status"))) {
                Toast.makeText(ToptenimagesActivity.this, "Vote Updated Successfully", Toast.LENGTH_SHORT).show();

                try {
                  //  toptenimages.notifyDataSetChanged();
                    apiIntegrationForTopPhotos();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            else {
                alert.ShowAlert(ToptenimagesActivity.this,"Something Missing");

            }


        } catch (Exception e) {
            e.printStackTrace();
        }}

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
        pd.dismiss();
    }


}
