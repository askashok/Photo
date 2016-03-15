package photocontest.bliss.com.photocontest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.ProfilePictureView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.internal.lo;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import photocontent.bliss.com.api.ConnectApi;
import photocontent.bliss.com.api.ServiceHandler;
import photocontent.bliss.com.api.ServiceHandlerUrl;

/**
 * Created by Jenifa Mary.C on 4/23/2015.
 */


//Activity For Login

public class LoginActivity extends Activity {
    EditText username_edt, password_edt;
    RelativeLayout login_buttn;

    ImageView name_img, password_img, facebook_img, logo_img;
    EasyTracker easyTracker = null;
    TextView forgetpwd_btn, createacct;
    SVG create_account_svg;
    Context context;
    ProgressDialog pDialog;
    String result, fname, lName, userid, Name, mailid, name_str, password_str, user_id, msg, status,profile_final_str,firstname_str;
    public static String profile_picture;
    ServiceHandler sh;
    SharedPreferences.Editor editor;
    boolean facebooklogin = false;
    boolean login = false;
    int value;

    CustomalertDialog alert = new CustomalertDialog();
    private PendingAction pendingAction = PendingAction.NONE;
    private CallbackManager callbackManager;

    private enum PendingAction {
        NONE,

    }
    private final String PENDING_ACTION_BUNDLE_KEY = "com.facebook.samples.hellofacebook:PendingAction";
    private ProfileTracker profileTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        context = this;
        easyTracker = EasyTracker.getInstance(context);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.loginpage);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        System.out.println("widthheight"+width+"height"+height);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "photocontest.bliss.com.photocontest",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        //This method is used to call the all ids
        InitializeAllValues();


        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
            pendingAction = PendingAction.valueOf(name);
        }

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        if (NetworkValidation.checknetConnection(getApplicationContext())) {
                            handlePendingAction();
                            updateUI();
                        }
                        else{
                            alert.ShowAlert(LoginActivity.this, "Check Your Internet Connection");
                        } }

                    @Override
                    public void onCancel() {
                        if (pendingAction != PendingAction.NONE) {
                            showAlert();
                            pendingAction = PendingAction.NONE;
                        }

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        if (pendingAction != PendingAction.NONE
                                && exception instanceof FacebookAuthorizationException) {
                            showAlert();
                            pendingAction = PendingAction.NONE;
                        }

                    }

                    private void showAlert() {
                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle(R.string.cancelled)
                                .setMessage(R.string.permission_not_granted)
                                .setPositiveButton(R.string.ok, null)
                                .show();
                    }
                });

                profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if (NetworkValidation.checknetConnection(getApplicationContext())) {

                    handlePendingAction();

                    updateUI();
                    // It's possible that we were waiting for Profile to be populated in order to
                    // post a status update.
                }
                else{
                    alert.ShowAlert(LoginActivity.this, "Check Your Internet Connection");
                }
            }
        };

                                      //Create new Account
                                      createacct.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v){
                                              Intent createaccount = new Intent(LoginActivity.this, NewAccount.class);
                                              startActivity(createaccount);
                                              /*finish();*/
                                          }
                                      });


                                       //Function for login Activity
                                       login_buttn.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               name_str = username_edt.getText().toString();
                                               password_str = password_edt.getText().toString();
                                               //This method is used to validate the Login Activity
                                               validationForLogin();
                                           }
                                       });


                                         //Forget password Fuctions
                                         forgetpwd_btn.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 Intent forgetpsw = new Intent(LoginActivity.this, ForgetPswActivity.class);
                                                 startActivity(forgetpsw);
                                                 /*finish();*/
                                             }
                                         });


    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }


    private void handlePendingAction() {
        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;
        switch (previouslyPendingAction) {
            case NONE:
                break;
        }
    }

    private void updateUI() {
        {
            boolean enableButtons = AccessToken.getCurrentAccessToken() != null;
            Profile profile = Profile.getCurrentProfile();
            if (profile != null) {
                fname = profile.getFirstName();
                lName = profile.getLastName();
                userid = profile.getId();
                Name = profile.getName();
                mailid = "";
                profile_picture = String.valueOf(profile.getProfilePictureUri(100,100));


                facebooklogin = true;
                new facebooklogin_async().execute();
            }
            else {

            }
        }
    }


    class facebooklogin_async extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Loading ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                 value=2;
                 result=ConnectApi.callFacebookLogin(fname, lName, userid, Name, mailid);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            pDialog.dismiss();
            try {

                if (facebooklogin == true) {
                    JSONObject reader = new JSONObject(result);
                    msg = reader.getString("msg");
                    if (Boolean.valueOf(reader.getString("status")) == true) {
                    JSONObject obj = reader.getJSONObject("user_id");

                    System.out.println("jsonobj is "+obj.getString("user_id").isEmpty());


                        user_id = obj.getString("user_id");
                        Toast.makeText(LoginActivity.this,"Photo Contest User Registered Successfully",Toast.LENGTH_SHORT).show();
                        Intent menu = new Intent(LoginActivity.this, AgreeTerms.class);
                        menu.putExtra("UserId", user_id);
                        startActivity(menu);


                        SharedPreferences pref = getApplicationContext().getSharedPreferences("Options", MODE_PRIVATE);
                        editor = pref.edit();
                        editor.putString("userid_key", user_id);
                        editor.putString("FirstName_Key",fname);
                        editor.commit();

                        finish();
                 }
                    else {
                        alert.ShowAlert(LoginActivity.this, msg);
                    }
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void validationForLogin() {
        if (username_edt.getText().toString().trim().equalsIgnoreCase("")) {
            alert.ShowAlert(LoginActivity.this, "Enter Email ID");
        } else if (!username_edt.getText().toString().matches("[a-zA-Z0-9+._%-+]{1,256}" + "@" + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "." + "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+")) {
            alert.ShowAlert(LoginActivity.this, "Invalid Email ID");
        } else if (password_edt.getText().toString().trim().equalsIgnoreCase("")) {
            alert.ShowAlert(LoginActivity.this, "Enter Password");
        } else {
            if (NetworkValidation.checknetConnection(getApplicationContext())) {
                try {
                new login_asyntask().execute();
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
            else {
                alert.ShowAlert(LoginActivity.this, "Check Your Internet Connection");
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void InitializeAllValues() {

        username_edt = (EditText) findViewById(R.id.login_name);
        password_edt = (EditText) findViewById(R.id.login_password);
        login_buttn = (RelativeLayout) findViewById(R.id.login_btn);
        forgetpwd_btn = (TextView) findViewById(R.id.forgetpassword_btn);
        createacct = (TextView) findViewById(R.id.createnewaccount_btn);
        name_img = (ImageView) findViewById(R.id.user_id_img);
        password_img = (ImageView) findViewById(R.id.user_password_img);
        facebook_img = (ImageView) findViewById(R.id.facebook_img);
        logo_img = (ImageView) findViewById(R.id.Photocontest_title);
        final EditText input = new EditText(LoginActivity.this);
        input.setTextColor(getResources().getColor(R.color.black));


        sh = new ServiceHandler();

        try {
            //Setting svg image for user_first_name
            name_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.username);
            name_img.setImageDrawable(create_account_svg.createPictureDrawable());

            //Setting svg image for User_last_name
            password_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), (R.raw.pwd));
            password_img.setImageDrawable(create_account_svg.createPictureDrawable());

            //setting svg image for facebook image
            facebook_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.fb);
            facebook_img.setImageDrawable(create_account_svg.createPictureDrawable());

            //setting svg imege logo image
            logo_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.logo);
            logo_img.setImageDrawable(create_account_svg.createPictureDrawable());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    //This Asyntask is used to get the response and parse the datas
    class login_asyntask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Loading ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                    value=1;
                    result = sh.makeServiceCall(ServiceHandlerUrl.getLoginUrl(name_str, password_str), ServiceHandler.GET);

                    if (result != null) {
                        login = true;

                }

            }

            catch (Exception e) {
                Toast.makeText(LoginActivity.this,"HTTP Host Exception",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();

            try {
                JSONObject reader = new JSONObject(result);
                status = reader.getString("status");
                if (login == true) {
                    if (Boolean.valueOf(reader.getString("status")) == true) {
                        msg = reader.getString("msg");
                        JSONObject obj = reader.getJSONObject("result");
                        user_id = obj.getString("user_id");
                        firstname_str=obj.getString("first_name");


                        SharedPreferences pref = getApplicationContext().getSharedPreferences("Options", MODE_PRIVATE);
                        editor = pref.edit();
                        editor.putString("userid_key", user_id);
                        editor.putString("FirstName_Key",firstname_str);
                        editor.commit();

                        Toast.makeText(LoginActivity.this,"Login Successfully",Toast.LENGTH_SHORT).show();
                        //geCreatedbyvalue();

                        Intent menu = new Intent(LoginActivity.this, MenuPage.class);
                        menu.putExtra("UserId", user_id);
                        startActivity(menu);
                        finish();
                    }
                    if (Boolean.valueOf(reader.getString("status")) == false) {
                        msg = reader.getString("msg");
                        alert.ShowAlert(LoginActivity.this, msg);
                    }
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private void geCreatedbyvalue() {
        new CreatedByValue().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
       // showAd();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        /*super.onBackPressed();*/
        if(value==1&value==2) {
            pDialog.dismiss();
        }
        if(value==3){
            pDialog.dismiss();
        }
        finish();
    }
    class CreatedByValue extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setTitle("Loading");
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                value=3;
                profile_final_str = ConnectApi.callprofileApi(user_id);
                System.out.println("finalllllllllllprofile"+profile_final_str);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            parseTheProfileDetails(profile_final_str);
        }
    }

    private void parseTheProfileDetails(String profile_final_str) {
        JSONObject photoday_obj;
        try {
            photoday_obj = new JSONObject(profile_final_str);
            JSONArray photoday_array = photoday_obj.getJSONArray("result");

            JSONObject photoday_sub_obj = photoday_array.getJSONObject(0);
            firstname_str = photoday_sub_obj.getString("first_name");
            System.out.println("firsjghsgcshchschdsgchgs"+firstname_str);
            SharedPreferences pref = getApplicationContext().getSharedPreferences("Options", MODE_PRIVATE);
            editor = pref.edit();
            editor.putString("FirstName_Key", firstname_str);
            editor.commit();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}

