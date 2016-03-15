package photocontest.bliss.com.photocontest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import photocontent.bliss.com.api.ConnectApi;
import photocontent.bliss.com.api.PhotocontentApi;
import photocontent.bliss.com.api.ServiceHandler;
import photocontent.bliss.com.api.ServiceHandlerUrl;

/**
 * Created by Jenifa Mary.c on 4/23/2015.
 */

//This Activity is used to create the Account
public class NewAccount extends Activity implements PhotocontentApi{
    Context context;
    EditText firstname_edt,lastname_edt,emailname_edt,password_edt,conf_passs_edt,mobleno_edt;
    ImageView firstname_img,lastname_img,emailname_img,password_img,conf_pass_img,mobile_img;
    TextView create_btn,cancel_btn;
    String fstname_str,lstname_str,emlname_str,psw_str,confpsw_str,mblno_str,result,user_id,massage,status;
    SVG create_account_svg;
    ProgressDialog pDialog;

    ServiceHandler sh;
    boolean create=false;
    InterstitialAd interstitial;
    ProgressDialog progress;
    EasyTracker easyTracker = null;
    // Alert Dialog Manager
    CustomalertDialog alert = new CustomalertDialog();
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.creating_new_account);
        context=this;
        easyTracker = EasyTracker.getInstance(context);
       /* showAd();*/
        //Initialize the all ids
        InitializeAllValues();

        //Create new Field
        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
try {
    fstname_str = firstname_edt.getText().toString();
    lstname_str = lastname_edt.getText().toString();
    emlname_str = emailname_edt.getText().toString();
    psw_str = password_edt.getText().toString();
    confpsw_str = conf_passs_edt.getText().toString();
    mblno_str = mobleno_edt.getText().toString();

    /*editor.putString("FirstName_Key", fstname_str);
    editor.putString("LastName_Key", lstname_str);
    editor.putString("Email_Key", emlname_str);
    editor.putString("Mobile_Key", mblno_str);
    editor.commit();*/
    //This method is used to validate the all details
    validateDetails();
}
catch(Exception e){
    e.printStackTrace();
}
                }
        });
        
        //Cancel these Activity
        cancel_btn.setOnClickListener(new View.OnClickListener() {
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

    private void InitializeAllValues() {
        firstname_edt=(EditText)findViewById(R.id.edt_firstname);
        lastname_edt=(EditText)findViewById(R.id.edt_lastname);
        emailname_edt=(EditText)findViewById(R.id.edt_email);
        password_edt=(EditText)findViewById(R.id.edt_passwrd);
        conf_passs_edt=(EditText)findViewById(R.id.edt_confirm_pswd);
        mobleno_edt=(EditText)findViewById(R.id.edt_mobnum);
        create_btn=(TextView)findViewById(R.id.create_btn);
        cancel_btn=(TextView)findViewById(R.id.cancel_btn);
        firstname_img=(ImageView)findViewById(R.id.create_firstname_img);
        lastname_img=(ImageView)findViewById(R.id.create_secondname_img);
        emailname_img=(ImageView)findViewById(R.id.create_email_img);
        password_img=(ImageView)findViewById(R.id.create_password_img);
        conf_pass_img=(ImageView)findViewById(R.id.create_confirm_password_img);
        mobile_img=(ImageView)findViewById(R.id.create_mob_img);
        sh=new ServiceHandler();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Options", MODE_PRIVATE);
        editor=pref.edit();

        try {
            //Setting svg image for user_first_name
            firstname_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.username);
            firstname_img.setImageDrawable(create_account_svg.createPictureDrawable());

            //Setting svg image for User_last_name
            lastname_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg= SVGParser.getSVGFromResource(getResources(), R.raw.username);
            lastname_img.setImageDrawable(create_account_svg.createPictureDrawable());

            //Setting svg image for user_email_name
            emailname_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.email);
            emailname_img.setImageDrawable(create_account_svg.createPictureDrawable());

            //Setting svg image for User_password_name
            password_img.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
            create_account_svg= SVGParser.getSVGFromResource(getResources(), R.raw.pwd);
            password_img.setImageDrawable(create_account_svg.createPictureDrawable());

            //Setting svg image for user_confirm password_name
            conf_pass_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.pwd);
            conf_pass_img.setImageDrawable(create_account_svg.createPictureDrawable());

            //Setting svg image for User_mobile number_name
            mobile_img.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
            create_account_svg= SVGParser.getSVGFromResource(getResources(), R.raw.phone);
            mobile_img.setImageDrawable(create_account_svg.createPictureDrawable());
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }


    //validate the all fields
    private void validateDetails() {
        if(firstname_edt.getText().toString().trim().equalsIgnoreCase("")){
            alert.ShowAlert(NewAccount.this, "Enter First Name");
        }
        else if(lastname_edt.getText().toString().trim().equalsIgnoreCase("")){
            alert.ShowAlert(NewAccount.this, "Enter Last Name");
        }
        else if(emailname_edt.getText().toString().trim().equalsIgnoreCase("")){
            alert.ShowAlert(NewAccount.this, "Enter Email ID");
        }
        else if (!emailname_edt.getText().toString().matches("[a-zA-Z0-9+._%-+]{1,256}" + "@"+ "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."+ "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+")) {
            alert.ShowAlert(NewAccount.this, "Invalid Email ID");
        }
        else if(password_edt.getText().toString().trim().equalsIgnoreCase("")){
            alert.ShowAlert(NewAccount.this, "Enter Password");
        }
        else if(conf_passs_edt.getText().toString().trim().equalsIgnoreCase("")){
            alert.ShowAlert(NewAccount.this, "Enter Confirm Password");
        }
        else if(!password_edt.getText().toString().trim().equalsIgnoreCase(conf_passs_edt.getText().toString().trim())){
            alert.ShowAlert(NewAccount.this, "Password and Confirm password must be same");
        }
        else if(mobleno_edt.getText().toString().trim().equalsIgnoreCase("")){
            alert.ShowAlert(NewAccount.this, "Enter Mobile Number");
        }

        else{
              try {
                  create=true;

                  if(NetworkValidation.checknetConnection(getApplicationContext())){
                      InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                      imm.hideSoftInputFromWindow(mobleno_edt.getWindowToken(), 0);
                         new Profile_asyn().execute();
                  }
                     else{
                         alert.ShowAlert(NewAccount.this, "Check Your Internet Connection");
                        }
                    }
                     catch (Exception e) {
                         e.printStackTrace();
                    }
                }

    }



        //aynstask for get the registration response and parse the response
         class Profile_asyn extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(NewAccount.this);
                pDialog.setMessage("Loading ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {

                    try {
                        result = sh.makeServiceCall(ServiceHandlerUrl.getregistrationUrl(fstname_str, lstname_str, emlname_str, psw_str, confpsw_str, mblno_str), ServiceHandler.GET);
                    } catch (Exception e) {
                        e.printStackTrace();

                }

             return result;
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.dismiss();
                try {
                    if(create==true){
                        JSONObject reader = new JSONObject(result);
                        if (Boolean.valueOf(reader.getString("status")) == true) {

                            status = reader.getString("status");
                            massage = reader.getString("msg");

                            Toast.makeText(NewAccount.this, "User Registered Sucessfully", Toast.LENGTH_SHORT).show();

                            if (NetworkValidation.checknetConnection(getApplicationContext())) {
                                LoginUrl();
                                clearAllvalue();
                            } else {
                                alert.ShowAlert(NewAccount.this, "Check your Internet Connection");
                                clearAllvalue();
                            }
                        }
                        else {
                            alert.ShowAlert(NewAccount.this, "Sorry.This Email ID is already Taken.");
                        }
                    }


                }
                catch (Exception e) {
                    e.printStackTrace();

                }

            }
        }

    private void LoginUrl() {
        new LoginActivity().execute();
    }

    class LoginActivity extends AsyncTask<Void,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=new ProgressDialog(NewAccount.this);
            progress.setMessage("Loading");
            progress.setCanceledOnTouchOutside(false);
            progress.setCancelable(true);
            progress.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                result = sh.makeServiceCall(ServiceHandlerUrl.getLoginUrl(emlname_str, confpsw_str), ServiceHandler.GET);
                System.out.println("dvchggdfvdgjfbdfhbgjdg"+result);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return result;
    }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            progress.dismiss();
            try {
                JSONObject reader = new JSONObject(result);
                JSONObject obj = reader.getJSONObject("result");
                user_id = obj.getString("user_id");

                SharedPreferences pref = getApplicationContext().getSharedPreferences("Options", MODE_PRIVATE);
                editor = pref.edit();
                editor.putString("FirstName_Key",fstname_str);
                editor.commit();

                Intent loginscreen=new Intent(NewAccount.this,AgreeTerms.class);
                loginscreen.putExtra("UserId",user_id);
                startActivity(loginscreen);
                finish();


            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void clearAllvalue() {
        firstname_edt.setText("");
        lastname_edt.setText("");
        emailname_edt.setText("");
        password_edt.setText("");
        conf_passs_edt.setText("");
        mobleno_edt.setText("");
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
        try {
            progress.dismiss();
            pDialog.dismiss();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
