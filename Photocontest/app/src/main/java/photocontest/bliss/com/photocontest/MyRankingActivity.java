package photocontest.bliss.com.photocontest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.allmenu.AdapterMyRanking;
import com.allmenu.ImageLoadAdapter;
import com.allmenu.PhotoDayModel;
import com.allmenu.PhotoOfTheDayActivity;
import com.allmenu.Vote_model;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import photocontent.bliss.com.api.ConnectApi;

/**
 * Created by Jenifa Mary.C on 5/21/2015.
 */
public class MyRankingActivity extends Activity {
    ListView rank_list;
    TextView photo_day_details;
    ImageView back_butt;
    SVG rank_svg;
    InterstitialAd interstitial;
    ProgressDialog pd;
    String today_img_final,userid_particular;
    String todayphotos_final, imagepagepath, created_date, rank, rating, created_by, description, no_votes;
    String name,date,no_of_votes,average_rating,posit,comments;
    Vote_model model;
    ArrayList<Vote_model> model_arraylist;
    AdapterMyRanking rankingadapter;
    CustomalertDialog alert = new CustomalertDialog();
    public int number_act;
    TextView heading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_ranking);
        Initialization();

        try{
            Intent inten=getIntent();
            userid_particular =inten.getStringExtra("particular_userid");
            number_act=inten.getIntExtra("activity_num",0);
            System.out.println("userid for particular"+userid_particular+"hgsdfs"+number_act);

        }
        catch(Exception e){
            e.printStackTrace();
        }
        if(number_act==2){
            back_butt.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            rank_svg = SVGParser.getSVGFromResource(getResources(), R.raw.back);
            back_butt.setImageDrawable(rank_svg.createPictureDrawable());
            heading.setText("Ranking");

        }
        if(number_act!=2){
            SvgImageSetting();
        }
               if (NetworkValidation.checknetConnection(getApplicationContext())) {
           // CallAsynTaskMethod();
           getMyPhotosOnly();
        }
        else{
            alert.ShowAlert(MyRankingActivity.this, "Check Your Internet Connection");
        }
        ShowAd();
        back_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void getMyPhotosOnly() {
    new MyAllPhotos().execute();
    }

    private void CallAsynTaskMethod() {
        new BahindActivity().execute();
    }

    private void ShowAd() {
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

    private void SvgImageSetting() {
        try {
            //Setting svg image for user_first_name
            back_butt.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            rank_svg = SVGParser.getSVGFromResource(getResources(), R.raw.home);
            back_butt.setImageDrawable(rank_svg.createPictureDrawable());
        }
    catch(Exception e){
    e.printStackTrace();}
    }

    private void Initialization() {
        photo_day_details=(TextView)findViewById(R.id.photo_day_details);
        photo_day_details.setVisibility(View.GONE);
        rank_list = (ListView) findViewById(R.id.rank_list);
        back_butt = (ImageView) findViewById(R.id.back_butt);
        heading=(TextView)findViewById(R.id.headin);
        model_arraylist = new ArrayList<Vote_model>();

        rank_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(number_act!=2) {
                    Intent rank = new Intent(MyRankingActivity.this, PhotoOfTheDayActivity.class);
                    rank.putExtra("photo_rank_day", 2);
                    startActivity(rank);
                }
            }
        });


    }

    class BahindActivity extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MyRankingActivity.this);
            pd.setMessage("Loading");
            pd.setCancelable(true);
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                todayphotos_final = ConnectApi.ResponseForTodayPhoto();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
            try {
                readandparsejson_imageurl(todayphotos_final);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private ArrayList<Vote_model> readandparsejson_imageurl(String todayphotos_final) {
        JSONObject todayimage = null;
        try {
            todayimage = new JSONObject(todayphotos_final);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        try {

            JSONArray jsonarray = todayimage.getJSONArray("todayphotos");
            model_arraylist.clear();
            for (int i = 0; i < jsonarray.length(); i++) {

                JSONObject json_obj = jsonarray.getJSONObject(i);
                imagepagepath = json_obj.getString("photo_path");
                created_date = json_obj.getString("created_date");
                rank = json_obj.getString("rank");
                rating = json_obj.getString("average_rating");
                description = json_obj.getString("description");
                created_by = json_obj.getString("created_by");
                no_votes = json_obj.getString("no_votes");


                model = new Vote_model();
                model.setImagepath(imagepagepath);
                model.setCreatedby(created_by);
                model.setDate(created_date);
                model.setRanking(rank);
                model.setRating(rating);
                model.setNovotes(no_votes);
                model.setDescription(description);
                model_arraylist.add(model);

                if(model_arraylist.size()>0){
                    if(model_arraylist.size()>0) {
                        photo_day_details.setVisibility(View.GONE);
                        rank_list.setVisibility(View.VISIBLE);

                        rankingadapter = new AdapterMyRanking(MyRankingActivity.this, model_arraylist);
                        rank_list.setAdapter(rankingadapter);
                    }
                    else{
                        rank_list.setVisibility(View.GONE);
                        photo_day_details.setVisibility(View.VISIBLE);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return model_arraylist;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(model_arraylist.size()>0){
            try {
                photo_day_details.setVisibility(View.INVISIBLE);
                rankingadapter = new AdapterMyRanking(MyRankingActivity.this, model_arraylist);
                rank_list.setAdapter(rankingadapter);
            }

        catch(Exception e){
        e.printStackTrace();}

        }
        else{
            rank_list.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pd.dismiss();
    }


    class MyAllPhotos extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MyRankingActivity.this);
            pd.setMessage("Loading");
            pd.setCancelable(true);
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                today_img_final = ConnectApi.ResponseForTodayImage(userid_particular);
                System.out.println("userid_particularuserid_particularuserid_particular"+userid_particular);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();

       try{
            readandparseMyOwnImages(today_img_final);
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }

    private void readandparseMyOwnImages(String today_img_final) {
        JSONObject todayimage = null;

        try {
            model_arraylist= new ArrayList<>();
            todayimage = new JSONObject(today_img_final);
            if (Boolean.valueOf(todayimage.getString("status")) == true) {
            JSONArray jsonarray = todayimage.getJSONArray("mytodayphotos");

            model_arraylist.clear();
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject json_obj = jsonarray.getJSONObject(i);
                imagepagepath = json_obj.getString("photo_path");
                name = json_obj.getString("created_by");
                date = json_obj.getString("created_date");
                System.out.println("dateegbhjhvbmg" + date);
                no_of_votes = json_obj.getString("no_votes");
                System.out.println("dniscsdfbhdhdgdhjvbhjc mvd" + no_of_votes);
                average_rating = json_obj.getString("average_rating");
                System.out.println("catewdgfhgfvhgvgyfgjfeb" + average_rating);
                posit = json_obj.getString("rank");
                comments = json_obj.getString("description");

                model = new Vote_model();
                model.setImagepath(imagepagepath);
                model.setCreatedby(name);
                model.setDate(date);
                model.setNovotes(no_of_votes);
                model.setRating(average_rating);
                model.setRanking(posit);
                model.setDescription(comments);

                model_arraylist.add(model);
            }


                if (model_arraylist.size()>0) {
                    photo_day_details.setVisibility(View.GONE);
                    rank_list.setVisibility(View.VISIBLE);
                    rankingadapter = new AdapterMyRanking(MyRankingActivity.this, model_arraylist);
                    rank_list.setAdapter(rankingadapter);
                }
            }
             else{
                photo_day_details.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
