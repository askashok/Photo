package com.allmenu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import photocontent.bliss.com.api.ConnectApi;
import photocontest.bliss.com.photocontest.CustomalertDialog;
import photocontest.bliss.com.photocontest.ModelforHistory;
import photocontest.bliss.com.photocontest.NetworkValidation;
import photocontest.bliss.com.photocontest.R;

/**
 * Created by BLT0059 on 6/12/2015.
 */
public class HistoryActivity extends Activity implements View.OnClickListener{
   ImageView back_butt,camera_butt;
    TextView todays_snap,photos_details;
    ExpandableListView expand_list;
    SVG history_svg;
    CameraActivity camera;
    ProgressDialog pd;
    ArrayList<String> date_list;
   // ArrayList<ModelforHistory> tem_list_datas;
    String user_id,allimage_img_final,create_date,photo_path,compare_date;

   //ArrayList<HistoryImageModel> history_model_arraylist;
    HashMap<String, String> map;
    CustomalertDialog alert = new CustomalertDialog();

    // create an arrayList to store values



   // ArrayList<String>  dates_parent_list;
    //ArrayList<Object> images_child_list;





    ArrayList<String> photos;
    ArrayList<HashMap<String, String>> jsonlist;
    ModelforHistory history_model;
    ArrayList<ModelforHistory> hist_model_arralist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_main);

        InitializeAllValues();
        setSvg();
        Intent getuserid=getIntent();
        user_id=getuserid.getStringExtra("userid");
        if (NetworkValidation.checknetConnection(getApplicationContext())) {
            try {
                loadingHistoryImages();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            alert.ShowAlert(HistoryActivity.this, "Check Your Internet Connection");
        }
    }

    private void loadingHistoryImages() {
        try {
            new LatestHistoryofImages().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSvg() {
        try {
            //Setting svg image for back_btn
            back_butt.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            history_svg = SVGParser.getSVGFromResource(getResources(), R.raw.home);
            back_butt.setImageDrawable(history_svg.createPictureDrawable());

            //Setting svg image for location
            camera_butt.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            history_svg = SVGParser.getSVGFromResource(getResources(), R.raw.camera_active);
            camera_butt.setImageDrawable(history_svg.createPictureDrawable());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void InitializeAllValues() {
        back_butt=(ImageView)findViewById(R.id.back_btn);
        camera_butt=(ImageView)findViewById(R.id.choose_camera_btn);
        todays_snap=(TextView)findViewById(R.id.todays_snap);
        photos_details=(TextView)findViewById(R.id.photos_details);

        expand_list=(ExpandableListView)findViewById(R.id.history_ima);
        expand_list.setDivider(null);
        expand_list.setGroupIndicator(null);




        back_butt.setOnClickListener(this);
        camera_butt.setOnClickListener(this);
        todays_snap.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
     switch (v.getId()){
         case R.id.back_btn:
             finish();
             break;
         case R.id.choose_camera_btn:
        //  camera.choosingimagedialog();
             break;
         case R.id.todays_snap:
             Intent today=new Intent(HistoryActivity.this,CameraActivity.class);
             startActivity(today);
             finish();
             break;
    }
    }
    class LatestHistoryofImages extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(HistoryActivity.this);
            pd.setMessage("Loading...");
            pd.setCanceledOnTouchOutside(false);
            pd.setCancelable(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                    System.out.println("useridcbsdfhjdfvjdfgh"+user_id);
                    allimage_img_final = ConnectApi.ResponseForLatestHistory(user_id);
                    System.out.println("alll latest images"+allimage_img_final);
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
            try{
                parsethelatesthistoryimage(allimage_img_final);
              /*  setGroupParents();
                setChildData();*/
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }



    private void parsethelatesthistoryimage(String allimage_img_final) {
        date_list=new ArrayList<String>();
        hist_model_arralist=new ArrayList<ModelforHistory>();

        try{
            JSONObject json_obj1=new JSONObject(allimage_img_final);
            JSONArray json_arra1=json_obj1.getJSONArray("result");

          /*  dates_parent_list= new ArrayList<String>();
            images_child_list=new ArrayList<Object>();*/

            for(int i=0;i<json_arra1.length();i++) {

                JSONObject json_obj2 = json_arra1.getJSONObject(i);
                create_date = json_obj2.getString("created_date");
                System.out.println("craeted date" + create_date);


              /*  dates_parent_list.add(create_date);
               System.out.println("datesdatesdates"+dates_parent_list.size());*/
                history_model = new ModelforHistory();

                history_model.setDates(create_date);

                JSONArray json_arra2 = json_obj2.getJSONArray("datewise_data");

                photos = new ArrayList<String>();
                System.out.println("json size ssssssssss" + json_arra2.length());


                for (int j = 0; j < json_arra2.length(); j++) {

                    JSONObject json_obj3 = json_arra2.getJSONObject(j);
                    photo_path = json_obj3.getString("photo_path");
                    compare_date = json_obj3.getString("created_date");
                    photos.add(photo_path);



                    history_model.setUrls(photos);

                }

                System.out.println("photos size ssssssssss" + photos.size());

              /*  images_child_list.add(photos);
                System.out.println("images arraylist"+images_child_list.size());*/

                hist_model_arralist.add(history_model);

                System.out.println("size of tem arrraylist"+hist_model_arralist.size());
            }





          /*  if((hist_model_arralist.size() >0)){
                HistoryAdapter adapter=new HistoryAdapter(HistoryActivity.this,hist_model_arralist) ;
                adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);

                expand_list.setAdapter(adapter);


            }
            else{
                Toast.makeText(HistoryActivity.this,"Multimap is empty",Toast.LENGTH_SHORT).show();
            }*/





        }
        catch(Exception e){
            e.printStackTrace();
        }







       /* try{

          //  history_model_arraylist=new ArrayList<>();
            hist_model_arralist=new ArrayList<ModelforHistory>();
            JSONObject json_obj1=new JSONObject(allimage_img_final);
            JSONArray json_arra1=json_obj1.getJSONArray("result");

            for(int i=0;i<json_arra1.length();i++){

                JSONObject json_obj2=json_arra1.getJSONObject(i);
                create_date=json_obj2.getString("created_date");
                System.out.println("craeted date"+create_date);

                history_model=new ModelforHistory();
                history_model.setDates(create_date);

                JSONArray json_arra2=json_obj2.getJSONArray("datewise_data");

                photos=new ArrayList<String>();
                System.out.println("json size ssssssssss"+json_arra2.length());
                for(int j=0;j<json_arra2.length();j++){

                    JSONObject json_obj3=json_arra2.getJSONObject(j);
                    photo_path=json_obj3.getString("photo_path");
                    compare_date=json_obj3.getString("created_date");
                    photos.add(photo_path);

                    //System.out.println("photos are "+photos);
                   // System.out.println("photos size issssssssss"+photos.size());

                    String no_votes=json_obj3.getString("no_votes");
                    String rank=json_obj3.getString("rank");


                }
                System.out.println("photos size ssssssssss"+photos.size());

                history_model.setUrls(photos);
                hist_model_arralist.add(history_model);


            }

            System.out.println("size of arraylist"+hist_model_arralist.size());


            if((hist_model_arralist.size() >0)){
              HistoryAdapter history=new HistoryAdapter(HistoryActivity.this,hist_model_arralist) ;
                history_list.setAdapter(history);
            }
            else{
                Toast.makeText(HistoryActivity.this,"Multimap is empty",Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e){
            e.printStackTrace();

        }*/ }
}
