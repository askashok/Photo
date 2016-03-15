package photocontest.bliss.com.photocontest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.internal.b;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Locale;

import photocontent.bliss.com.api.ConnectApi;

/**
 * Created by Jenifa Mary.C on 5/4/2015.
 */
public class ProfileActivity extends Activity implements View.OnClickListener {
    ImageView profileimage,firstname_img,secondname_img,email_img,mob_img;
    TextView home_butt,edit_butt;
    EditText firstname_edt, lastname_edt, email_edt, mobileno_edt;
    String edit_mobile_str,edit_email_str,edit_lname_str,edit_name_sr,userid, timestamp, imagepagepath, encodedImage,temp,profile_final_str,firstname_str,lastname_str,emal_str,mobile_str,profile_photo_str,update_profile_final_str,facebook_userid;
    SharedPreferences pref;
    SVG create_account_svg;
    boolean check=false;
    static Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Photo Contest";
    byte[] bitmapBytes;
    ProgressDialog prodis;
    CustomalertDialog alert = new CustomalertDialog();
    boolean profile_view=true;
    boolean update_profile=false;
    InterstitialAd interstitial;
    EasyTracker easyTracker = null;
    Context context;
    int value;
    String profile_image;
    LoginActivity loginact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile);
        try{
            value=1;
            pref=getApplication().getSharedPreferences("Options", MODE_PRIVATE);
            userid = pref.getString("userid_key", "");
            System.out.println("userrrrrridddddd"+userid);
            easyTracker = EasyTracker.getInstance(context);
            profile_image=loginact.profile_picture;
            System.out.println("profile_imagesare "+profile_image);
        InitializeMetod();
        setAllSvgImages();
            showAd();
        calltheprofileApi();
        }
        catch(Exception e){
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

    private void setAllSvgImages() {
        try {
            //Setting svg image for user_first_name
            firstname_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.username);
            firstname_img.setImageDrawable(create_account_svg.createPictureDrawable());

            //Setting svg image for user_first_name
            secondname_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.username);
            secondname_img.setImageDrawable(create_account_svg.createPictureDrawable());

            //Setting svg image for user_first_name
            email_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.email);
            email_img.setImageDrawable(create_account_svg.createPictureDrawable());

            //Setting svg image for user_first_name
            mob_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.phone);
            mob_img.setImageDrawable(create_account_svg.createPictureDrawable());

        }
    catch(Exception e){
    e.printStackTrace();}
    }

    class ProfileBehind extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prodis=new ProgressDialog(ProfileActivity.this);
            prodis.setTitle("Loading");
            prodis.setCancelable(true);
            prodis.setCanceledOnTouchOutside(false);
            prodis.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if(profile_view==true) {
                    try {
                        profile_final_str = ConnectApi.callprofileApi(userid);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                if(update_profile==true){
                    try{
                        System.out.println("all detaisl"+userid+encodedImage+firstname_str+lastname_str+emal_str+mobile_str);
                        update_profile_final_str = ConnectApi.callUpdateProfile(userid, encodedImage, edit_name_sr, edit_lname_str, edit_email_str, edit_mobile_str);
                    }
                  catch (Exception e){
                      e.printStackTrace();
                  }
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            prodis.dismiss();
            try {
                if(profile_view==true) {
                    try {
                        parseTheProfileDetails(profile_final_str);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                if(update_profile==true){
                    try{
                        parseUpdationProfileScreen(update_profile_final_str);
                    }
                  catch (Exception e){
                      e.printStackTrace();
                  }
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void parseUpdationProfileScreen(String update_profile_final_str) {
        try {
            JSONObject update_obj = new JSONObject(update_profile_final_str);
            String msg=update_obj.getString("msg");
            String stat=update_obj.getString("status");
            Toast.makeText(ProfileActivity.this,msg,Toast.LENGTH_SHORT).show();
            setEnablefalsemethod();
            edit_butt.setText("Edit");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return;
    }

    private void parseTheProfileDetails(String profile_final_str) {
        JSONObject photoday_obj;
        try {
            photoday_obj = new JSONObject(profile_final_str);
            JSONArray photoday_array = photoday_obj.getJSONArray("result");

               JSONObject photoday_sub_obj = photoday_array.getJSONObject(0);
               facebook_userid=photoday_sub_obj.getString("facebook_id");
               System.out.println("facebook user id"+facebook_userid);
               System.out.println("length offacebook userid"+facebook_userid.isEmpty());
               firstname_str = photoday_sub_obj.getString("first_name");
               lastname_str=photoday_sub_obj.getString("last_name");
               emal_str=photoday_sub_obj.getString("email_id");
               mobile_str=photoday_sub_obj.getString("phone_no");
               profile_photo_str=photoday_sub_obj.getString("profile_photo");


try {

    if(facebook_userid.isEmpty()==false){

        firstname_edt.setText(firstname_str);
        lastname_edt.setText(lastname_str);
        Picasso.with(ProfileActivity.this)
                .load(profile_image).into(profileimage);

        setEnablefalsemethod();
        profileimage.setEnabled(false);
        edit_butt.setEnabled(false);
    }
   else if(!profile_photo_str.equalsIgnoreCase("http://182.71.230.166:99/photocontest/"))
    {
        try {

                Picasso.with(ProfileActivity.this)
                        .load(profile_photo_str).into(profileimage);
            firstname_edt.setText(firstname_str);
            lastname_edt.setText(lastname_str);
            email_edt.setText(emal_str);
            mobileno_edt.setText(mobile_str);
            setEnablefalsemethod();
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
    else{

        firstname_edt.setText(firstname_str);
        lastname_edt.setText(lastname_str);
        email_edt.setText(emal_str);
        mobileno_edt.setText(mobile_str);
        setEnablefalsemethod();
    }
}
catch(Exception e){
    e.printStackTrace();
}


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setEnablefalsemethod() {
        profileimage.setEnabled(false);
        firstname_edt.setEnabled(false);
        lastname_edt.setEnabled(false);
        email_edt.setEnabled(false);
        mobileno_edt.setEnabled(false);
    }

    private void calltheprofileApi() {
        if(NetworkValidation.checknetConnection(getApplicationContext())) {
            new ProfileBehind().execute();
        }
        else{
            alert.ShowAlert(ProfileActivity.this, "Check Your Internet Connection");
        }
    }

    private void InitializeMetod() {
        home_butt = (TextView) findViewById(R.id.home_butt);
        profileimage = (ImageView) findViewById(R.id.profile_image);
        firstname_edt = (EditText) findViewById(R.id.firstname_edt);
        lastname_edt = (EditText) findViewById(R.id.lastname_edt);
        email_edt = (EditText) findViewById(R.id.email_edt);
        mobileno_edt = (EditText) findViewById(R.id.mobileno_edt);
        edit_butt=(TextView)findViewById(R.id.edit_btn);
        firstname_img=(ImageView)findViewById(R.id.profile_firstname_img);
        secondname_img=(ImageView)findViewById(R.id.profile_secondname_img);
        email_img=(ImageView)findViewById(R.id.profile_email_img);
        mob_img=(ImageView)findViewById(R.id.profile_mob_img);

        edit_butt.setOnClickListener(this);
        profileimage.setOnClickListener(this);
        home_butt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_image:
                try {
                    choosingimagedialog();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.home_butt:
                finish();
                break;
            case R.id.edit_btn:
                if(check) {

                        profile_view = false;
                        update_profile = true;

                        edit_name_sr = firstname_edt.getText().toString();
                        edit_lname_str = lastname_edt.getText().toString();
                        edit_email_str = email_edt.getText().toString();
                        edit_mobile_str = mobileno_edt.getText().toString();
                        System.out.println("namesssssssss" + edit_name_sr + edit_lname_str);
                        if (firstname_edt.getText().toString().trim().equalsIgnoreCase("")) {
                            alert.ShowAlert(ProfileActivity.this, "Enter First Name");
                        } else if (lastname_edt.getText().toString().trim().equalsIgnoreCase("")) {
                            alert.ShowAlert(ProfileActivity.this, "Enter Last Name");
                        } else if (email_edt.getText().toString().trim().equalsIgnoreCase("")) {
                            alert.ShowAlert(ProfileActivity.this, "Enter Email");
                        } else if (mobileno_edt.getText().toString().equalsIgnoreCase("")) {
                            alert.ShowAlert(ProfileActivity.this, "Enter Mobile Number");
                        } else if (profile_photo_str.equalsIgnoreCase("")) {
                            alert.ShowAlert(ProfileActivity.this, "Select Profile Image");
                        } else {
                            try {
                                calltheprofileApi();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        check = false;
                        setEnablefalsemethod();
            }
                else
                {
                    if(facebook_userid.isEmpty()) {
                        setEnabletureMethod();
                        edit_butt.setText("Submit");
                        check = true;
                    }
                   else if(facebook_userid.isEmpty()==false){
                        if(emal_str.isEmpty()==false&&mobile_str.isEmpty()==false){
                            mobileno_edt.setEnabled(true);
                            email_edt.setEnabled(true);
                        }
                        else if(emal_str.isEmpty()==false) {
                            mobileno_edt.setEnabled(false);
                            email_edt.setEnabled(true);
                        }
                        else if(mobile_str.isEmpty()==false){
                            email_edt.setEnabled(false);
                            mobileno_edt.setEnabled(true);
                        }
                        edit_butt.setText("Submit");
                        check=true;
                    }
                }
                break;
        }
    }

    private void setEnabletureMethod() {
        profileimage.setEnabled(true);
        firstname_edt.setEnabled(true);
        lastname_edt.setEnabled(true);
        mobileno_edt.setEnabled(true);

    }

    private void choosingimagedialog() {

        AlertDialog alertdialog = new AlertDialog.Builder(ProfileActivity.this).create();
        alertdialog.setTitle("Choose Image");
        alertdialog.setMessage("Choose from Gallary or Take Photo");

        alertdialog.setButton("Gallary", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                arg0.dismiss();
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);
            }
        });
        alertdialog.setButton2("Take Photo",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        startActivityForResult(intent, 0);

                    }
                });
        alertdialog.setButton3("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        alertdialog.show();
    }

    private Uri getOutputMediaFileUri(int mediaTypeImage) {
        return Uri.fromFile(getOutputMediaFile(mediaTypeImage));
    }

    private File getOutputMediaFile(int type) {
        File mediastoredir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        if (!mediastoredir.exists()) {
            if (!mediastoredir.mkdir()) {
                return null;
            }
        }
        timestamp = new SimpleDateFormat("yyyy MMdd_HHmmss",
                Locale.getDefault()).format(new java.util.Date());
        File mediafile;

        if (type == MEDIA_TYPE_IMAGE) {
            mediafile = new File(mediastoredir.getPath() + File.separator + "IMG_" + timestamp + ".jpg");
            imagepagepath = mediastoredir.getPath() + File.separator + "IMG_" + ".jpg";
        }
        else {
            return null;
        }
        return mediafile;
    }

    @Override
    protected void onActivityResult(int Reguestcode, int resultcode, Intent data) {
        super.onActivityResult(Reguestcode, resultcode, data);
        if (Reguestcode == 0) {
            if (resultcode == RESULT_OK) {
                try {
                    previewCapturedImage();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if (resultcode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
        if (Reguestcode == 1) {
            if (resultcode == RESULT_OK && null != data) {
                try {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                    imagepagepath = cursor.getString(columnIndex);
                    cursor.close();
                    // resized image to fix the image view
                    BitmapFactory.Options option = new BitmapFactory.Options();
                    option.inSampleSize = 4;
                    Bitmap bitmap = BitmapFactory.decodeFile(imagepagepath, option);
                    Matrix matrix = new Matrix();

                    Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                    Bitmap resized = Bitmap.createScaledBitmap(rotatedBitmap, 150, 150, true);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    resized.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                    bitmapBytes = stream.toByteArray();
                    profileimage.setImageBitmap(resized);

                    profile_view=false;
                    update_profile=true;

                 //   calltheprofileApi();

                    // converting to base64
                    encodedImage = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }



    private void previewCapturedImage() {
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);

            Matrix matrix = new Matrix();
            matrix.postRotate(getImageOrientation(imagepagepath));
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            Bitmap resized = Bitmap.createScaledBitmap(rotatedBitmap, 150, 150, true);

            //convert the bitmap into String
            ByteArrayOutputStream baos=new  ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
            byte [] b=baos.toByteArray();
            temp=Base64.encodeToString(b, Base64.DEFAULT);

            //set the profilescrenn image
            profileimage.setImageBitmap(resized);
            profile_view=false;
            update_profile=true;

          //  calltheprofileApi();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            bitmapBytes = stream.toByteArray();

            timestamp = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss", Locale.getDefault()).format(new java.util.Date());

            // converting to base64
            encodedImage = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private float getImageOrientation(String imagepagepath) {

        int rotate = 0;
        try {

            File imageFile = new File(imagepagepath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
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
            prodis.dismiss();
        }
    }
}
