package com.allmenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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

import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import android.provider.MediaStore;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.common.access.WatsAppImageCompression;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import photocontent.bliss.com.api.ConnectApi;

import photocontest.bliss.com.photocontest.CustomalertDialog;
import photocontest.bliss.com.photocontest.ModelforHistory;
import photocontest.bliss.com.photocontest.NetworkValidation;
import photocontest.bliss.com.photocontest.R;

/**
 * Created by Jenifa Mary.C on 4/27/2015.
 */
public class CameraActivity extends Activity implements LocationListener{
    GridView loadimage_grid;
    EasyTracker easyTracker = null;
    Context context;
    TextView submit,today_snap, history_snap,photos_details;

    ImageView back_btn, camera_btn, location_image;
    RelativeLayout upload_img_layout, main_layout_, upload_dialog_layout;
    AutoCompleteTextView places_edt;
    SVG camera_snap_pg_svg;

   Bitmap resized_sdcard;
    Bitmap rotatedBit = null;
    int captureimage;

    double myLatitude,myLongitude;
    GPSTracker gps;
    String addr;
String photo_path,create_date,compare_date;
    ModelforHistory history_model;
    ArrayList<String> photos;
    Dialog dialog;
    private static final String IMAGE_DIRECTORY_NAME = "Photo Contest";
    String url,time,noofvotes,rankin,date, catego, today_img_final, upload_img_final, allimage_img_final, rating_time, user_id, title_str, location, description_str, active, created_by, encodedImage, timestamp, imagepagepath, photomaster_id;
    int category_id = 1, active_int = 1;
    byte[] bitmapBytes;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    EditText title, description;
    ImageView mainimage;
    static Uri fileUri;
    ProgressDialog pd;
    ArrayList<String> date_list;
    ArrayList<ModelforHistory> hist_model_arralist;
    ExpandableListView  expand_list;
  //  HistoryImageModel history_model;
    ArrayList<HistoryImageModel> history_model_arraylist;

    ArrayList<String> images_list;
    public static final int MEDIA_TYPE_IMAGE = 1;
    CustomalertDialog alert = new CustomalertDialog();
    InterstitialAd interstitial;
    SharedPreferences pref;
    SharedPreferences.Editor editor;


    private static final String TAG_RESULT = "predictions";
    JSONObject json;
    JSONArray contacts = null;
    String[] search_text;
    ArrayList<String> names;
    ArrayAdapter<String> adp;
    String browserKey = "AIzaSyDw4a3iFGDvnHexMGF53nks0-q2H8-AC-w";



    protected LocationManager locationManager;
    protected LocationListener locationListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.camera_snap);


        context = this;
        gps=new GPSTracker(context);
        easyTracker = EasyTracker.getInstance(context);
        catego = Integer.toString(category_id);
        active = Integer.toString(active_int);
        try {
            pref = getApplication().getSharedPreferences("Options", MODE_PRIVATE);
            created_by = pref.getString("FirstName_Key", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //initialize the all ids.
        Initialization();


      /*  locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);*/

        getMyLocation();

        showAd();
        history_snap.setBackgroundColor(Color.parseColor("#257487"));
        today_snap.setBackgroundColor(Color.parseColor("#359EB9"));

        try {
            Intent images = getIntent();
            user_id = images.getStringExtra("UserId");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //set all svg images
        SetAllSvgImages();


       // image_today_check = true;
        if (NetworkValidation.checknetConnection(getApplicationContext())) {
            try {
                loadingTodayImageUrl();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            alert.ShowAlert(CameraActivity.this, "Check Your Internet Connection");
        }
        //Adapter loaded for the first time while entering into activity( photo history )

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    choosingimagedialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        history_snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if (NetworkValidation.checknetConnection(getApplicationContext())) {
                    try {
                      ///  loadingHistoryImages();
                        Intent hist=new Intent(CameraActivity.this,HistoryActivity.class);
                        hist.putExtra("userid",user_id);
                        startActivity(hist);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    alert.ShowAlert(CameraActivity.this, "Check Your Internet Connection");
                }*/


                if (NetworkValidation.checknetConnection(getApplicationContext())) {
                    try {
                        history_snap.setBackgroundColor(Color.parseColor("#359EB9"));
                        today_snap.setBackgroundColor(Color.parseColor("#257487"));
                        expand_list.setVisibility(View.VISIBLE);
                        loadimage_grid.setVisibility(View.GONE);
                        photos_details.setVisibility(View.GONE);
                        loadingLatestHistoryImages();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    alert.ShowAlert(CameraActivity.this, "Check Your Internet Connection");
                }

            }
        });


        today_snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetworkValidation.checknetConnection(getApplicationContext())) {
                    try {
                        expand_list.setVisibility(View.GONE);
                        loadimage_grid.setVisibility(View.VISIBLE);
                        loadingTodayImageUrl();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    alert.ShowAlert(CameraActivity.this, "Check Your Internet Connection");
                }
            }
        });


    }

    private void loadingLatestHistoryImages() {
        try {
            new LatestHistoryofImages().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMyLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        try {
            if (isGPSEnabled) {

                myLatitude = gps.getLatitude();
                myLongitude = gps.getLongitude();
                System.err.println("<laylang>"+myLatitude+", "+myLongitude);

                addr=GetAddressfromLatLong(myLatitude,myLongitude);


               /* map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLatitude, myLongitude), 12));
                map.addMarker(new MarkerOptions().position(new LatLng(myLatitude, myLongitude))
                        .title(addr.toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.mosuqeperson)));*/

            } else {
                gps.showSettingsAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String GetAddressfromLatLong(double myLatitude, double myLongitude) {
        Geocoder geocoder;
        String addr="";
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(myLatitude, myLongitude, 1);

            String address = addresses.get(0).getAddressLine(1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();


            addr = address + ", " + city + ", " + state + ", " + country ;
        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("<<adddress>>"+addr);
        return addr;
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

    private void loadingHistoryImages() {

        history_snap.setBackgroundColor(Color.parseColor("#359EB9"));
        today_snap.setBackgroundColor(Color.parseColor("#257487"));
        try {
          /*  asyntaskMethod();*/



          //  new HistoryOfImages().execute();


            new LatestHistoryofImages().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadingTodayImageUrl() {
        history_snap.setBackgroundColor(Color.parseColor("#257487"));
        today_snap.setBackgroundColor(Color.parseColor("#359EB9"));
      //  expand_list.setVisibility(View.INVISIBLE);
        try {
            new TodayImageAsynTask().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void choosingimagedialog() {

        AlertDialog alertdialog = new AlertDialog.Builder(CameraActivity.this).create();
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

    @Override
    protected void onActivityResult(int Reguestcode, int resultcode, Intent data) {
        super.onActivityResult(Reguestcode, resultcode, data);
        System.out.print("requestcode" + "" + Reguestcode);
        if (Reguestcode == 0) {
            if (resultcode == RESULT_OK) {
                try {
                    captureimage = 0;
                    previewCapturedImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultcode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "User cancelled Image Capture", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to Capture Image", Toast.LENGTH_SHORT).show();
            }
        }
        if (Reguestcode == 1) {
            if (resultcode == RESULT_OK && null != data) {
                try {
                    captureimage = 1;
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imagepagepath = cursor.getString(columnIndex);

                    cursor.close();







                    Matrix m = new Matrix();
                    ExifInterface exif = null;
                    int orientation = 1;

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4;
                    final Bitmap bitmap = BitmapFactory.decodeFile(
                           imagepagepath, options);


                    if(imagepagepath!=null){
                        // Getting Exif information of the file
                        try {
                            exif = new ExifInterface(imagepagepath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(exif!=null){
                        orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                        switch(orientation){
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                m.preRotate(270);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                m.preRotate(90);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                m.preRotate(180);
                                break;
                        }


                        // bitmap.getWidth(),bitmap.getHeight()
                        // Rotates the image according to the orientation
                        resized_sdcard = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,true);
                        System.out.println("rotated image bit "+resized_sdcard);

                   /*     hgchjcsd*/
                    }

                    showdialogAct();


                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    resized_sdcard.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream .toByteArray();


                    encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);







                  /* // resized image to fix the image view
                   BitmapFactory.Options option = new BitmapFactory.Options();
                    option.inSampleSize = 4;
                    Bitmap bitmap = BitmapFactory.decodeFile(imagepagepath, option);
                    Matrix matrix = new Matrix();
                    Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    captureimage = 1;
                    resized_sdcard = Bitmap.createScaledBitmap(rotatedBitmap, 170, 170, true);*/

                  /*  showdialogAct();*/

                  /*  ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    resized_sdcard.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    bitmapBytes = stream.toByteArray();

                    // converting to base64
                    encodedImage = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
                    System.out.println("encodgfghcvsyghcvshcbshcs"+encodedImage);
*/
              //  }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }
    }



    private void showdialogAct() {
        // Create custom dialog object
        dialog = new Dialog(CameraActivity.this);
        // Set dialog title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Include dialog.xml file
      /*  dialog.setContentView(R.layout.camera_upload_dialog);*/
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);


        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.camera_upload_dialog, null, false);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));



        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        title = (EditText) dialog.findViewById(R.id.title_upload_edt);
        places_edt = (AutoCompleteTextView) dialog.findViewById(R.id.selectarea);
        description = (EditText) dialog.findViewById(R.id.shor_desctiption_edt);
        submit = (TextView) dialog.findViewById(R.id.upload_img_server_btn);
        mainimage = (ImageView) dialog.findViewById(R.id.vote_snap_img);
        location_image = (ImageView) dialog.findViewById(R.id.location_img);

        //Setting svg image for camera_btn
        location_image.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        camera_snap_pg_svg = SVGParser.getSVGFromResource(getResources(), R.raw.map);
        location_image.setImageDrawable(camera_snap_pg_svg.createPictureDrawable());

        places_edt.setText(addr);
        if (captureimage == 0) {


            mainimage.setImageBitmap(rotatedBit);
        }
        if (captureimage == 1) {
            System.out.println("resized_sdcardresized_sdcard" + resized_sdcard);
            mainimage.setImageBitmap(resized_sdcard);
        }



        places_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });






        // if decline button is clicked, close the custom dialog
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Close dialog
                    loadingimageurl();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }


    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type) {


        File mediastoredir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        if (!mediastoredir.exists()) {
            if (!mediastoredir.mkdir()) {
                return null;
            }
        }
        timestamp = new SimpleDateFormat("yyyy MMdd_HHmmss", Locale.getDefault()).format(new java.util.Date());
        File mediafile;

        if (type == MEDIA_TYPE_IMAGE) {
            mediafile = new File(mediastoredir.getPath() + File.separator + "IMG_" + timestamp + ".jpg");
            imagepagepath = mediastoredir.getPath() + File.separator + "IMG_" + ".jpg";
        } else {
            return null;
        }
        return mediafile;
    }


    private void previewCapturedImage() {
        try {


            String path=fileUri.getPath();
            Matrix m = new Matrix();
            ExifInterface exif = null;
            int orientation = 1;


           BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            final Bitmap bitmap = BitmapFactory.decodeFile(
                    fileUri.getPath(), options);







            if(path!=null){
                // Getting Exif information of the file
                try {
                    exif = new ExifInterface(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(exif!=null){
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                switch(orientation){
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        m.preRotate(270);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        m.preRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        m.preRotate(180);
                        break;
                }

               // bitmap.getWidth(),bitmap.getHeight()
                // Rotates the image according to the orientation

                rotatedBit = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,true);
                System.out.println("rotated image bit "+rotatedBit);
               /* gdscghsghs*/
            }


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            rotatedBit.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();


            encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);


      showdialogAct();

   }catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private float getImageOrientation(String imagepagepath2) {
        int rotate = 0;
        try {
            System.out.println("imageeeegegegge" + imagepagepath2);
            File imageFile = new File(imagepagepath2);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            System.out.println("orientation is" + orientation);
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
            System.out.println("rotate is" + rotate);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

    private String parsethejsonresponse(String response) {
        String msg = null;
        try {
            JSONObject jsonobj = new JSONObject(response);

           /* if (image_upload_check == true) {*/
                if (Boolean.valueOf(jsonobj.getString("status"))) {


                    Toast.makeText(getApplicationContext(),"Image Updated Successfully",Toast.LENGTH_SHORT).show();
                    clearAllTextValue();
                    dialog.dismiss();
                    try{
                        if (NetworkValidation.checknetConnection(getApplicationContext())) {
                            loadingTodayImageUrl();
                        }
                        else{
                            alert.ShowAlert(CameraActivity.this,"Check your Internet Connection");
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }

           // }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return msg;
    }

    private void clearAllTextValue() {
        description.setText("");
        title.setText("");
        places_edt.setText("");

    }


    private ArrayList<String> readandparsejson_imageurl(String response) {
        JSONObject todayimage = null;

        try {
            images_list = new ArrayList<String>();
            todayimage = new JSONObject(response);
            if (Boolean.valueOf(todayimage.getString("status")) == true) {
                JSONArray jsonarray = todayimage.getJSONArray("mytodayphotos");

                images_list.clear();
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject json_obj = jsonarray.getJSONObject(i);
                    imagepagepath = json_obj.getString("photo_path");
                    photomaster_id = json_obj.getString("photomaster_id");
                    rating_time = json_obj.getString("date_time");
                    images_list.add(imagepagepath);


                }
                if (images_list.size()>0) {
                    System.out.println("now i am here");
                    expand_list.setVisibility(View.GONE);
                    loadimage_grid.setVisibility(View.VISIBLE);
                    ImageLoadAdapter allimageadat = new ImageLoadAdapter(CameraActivity.this, images_list);
                    loadimage_grid.setAdapter(allimageadat);
                    allimageadat.notifyDataSetChanged();
                }
                }
            else{
                photos_details.setVisibility(View.VISIBLE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return images_list;
    }


    private String getRealPathFromURI(String imagepagepath) {
        Uri contentUri = Uri.parse(imagepagepath);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private void loadingimageurl() {
        try {
           /* image_upload_check = true;*/
            title_str = title.getText().toString();
            location = places_edt.getText().toString();
            description_str = description.getText().toString();
            validation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validation() {
        if (title.getText().toString().trim().equalsIgnoreCase("")) {
            alert.ShowAlert(CameraActivity.this, "Enter Title");
        } else if (places_edt.getText().toString().trim().equalsIgnoreCase("")) {
            alert.ShowAlert(CameraActivity.this, "Select Area");
        } else if (description.getText().toString().trim().equalsIgnoreCase("")) {
            alert.ShowAlert(CameraActivity.this, "Enter Description");
        } else if (NetworkValidation.checknetConnection(getApplicationContext())) {
            dialog.dismiss();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(description.getWindowToken(), 0);

            photos_details.setVisibility(View.GONE);
            new UpdateImageUri().execute();
        } else {
            dialog.dismiss();
            alert.ShowAlert(CameraActivity.this, "Check Your Internet Connection");
        }

    }



    private void SetAllSvgImages() {
        try {
            //Setting svg image for back_btn
            back_btn.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            camera_snap_pg_svg = SVGParser.getSVGFromResource(getResources(), R.raw.home);
            back_btn.setImageDrawable(camera_snap_pg_svg.createPictureDrawable());

            //Setting svg image for location
            camera_btn.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            camera_snap_pg_svg = SVGParser.getSVGFromResource(getResources(), R.raw.camera_active);
            camera_btn.setImageDrawable(camera_snap_pg_svg.createPictureDrawable());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Initialization() {
        today_snap = (TextView) findViewById(R.id.todays_snap);
        photos_details =(TextView)findViewById(R.id.photos_details);
        history_snap = (TextView) findViewById(R.id.history_snap);


        expand_list=(ExpandableListView)findViewById(R.id.history_ima);
        expand_list.setDivider(null);
        expand_list.setGroupIndicator(null);

        photos_details.setVisibility(View.GONE);

        back_btn = (ImageView) findViewById(R.id.back_btn);
        camera_btn = (ImageView) findViewById(R.id.choose_camera_btn);

        loadimage_grid = (GridView) findViewById(R.id.todays_img_grid);
        upload_img_layout = (RelativeLayout) findViewById(R.id.upload_image);
        main_layout_ = (RelativeLayout) findViewById(R.id.snap_main_layout);
        upload_dialog_layout = (RelativeLayout) findViewById(R.id.upload_image);


        SharedPreferences pref = getApplicationContext().getSharedPreferences("Options", MODE_PRIVATE);
        editor = pref.edit();
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
    public void onLocationChanged(Location location) {
        System.out.println("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }



    public class paserdata extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            // getting JSON string from URL
            json = jParser.getJSONFromUrl(url.toString());
            if (json != null) {
                try {
                    // Getting Array of Contacts
                    contacts = json.getJSONArray(TAG_RESULT);


                    // Extract the Place descriptions from the results

                    for (int i = 0; i < contacts.length(); i++) {


                        names.add(contacts.getJSONObject(i).getString("description"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {

            adp = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, names) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = null;
                    try {
                        view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                        text.setTextColor(Color.BLACK);
                        text.setBackgroundColor(Color.WHITE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return view;

                }
            };
            places_edt.setAdapter(adp);
        }
    }

    class TodayImageAsynTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

           /* pd = new ProgressDialog(CameraActivity.this);
            pd.setMessage("Loading...");
            pd.setCancelable(true);
            pd.setCanceledOnTouchOutside(false);
            pd.show();*/

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                today_img_final = ConnectApi.ResponseForTodayImage(user_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
           // pd.dismiss();
            try {
                history_snap.setBackgroundColor(Color.parseColor("#257487"));
                today_snap.setBackgroundColor(Color.parseColor("#359EB9"));
                readandparsejson_imageurl(today_img_final);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class HistoryOfImages extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(CameraActivity.this);
            pd.setMessage("Loading...");
            pd.setCanceledOnTouchOutside(false);
            pd.setCancelable(true);
            pd.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
              allimage_img_final = ConnectApi.ResponseForAllImages(user_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            pd.dismiss();
            try {
                readandparsejson_allhistoryimageurl(allimage_img_final);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void readandparsejson_allhistoryimageurl(String allimage_img_final) {
        JSONObject todayimage = null;
        try {

            history_model_arraylist=new ArrayList<>();

           todayimage = new JSONObject(allimage_img_final);

           JSONArray json_arr = todayimage.getJSONArray("myphotos");

            history_model_arraylist.clear();
        for (int i = 0; i < json_arr.length(); i++) {
            JSONObject json_obj = json_arr.getJSONObject(i);

            imagepagepath = json_obj.getString("photo_path");
            date=json_obj.getString("created_date");
            rankin=json_obj.getString("rank");
            noofvotes=json_obj.getString("no_votes");
            time=json_obj.getString("created_time");

           /* history_model=new HistoryImageModel();
            history_model.setTime(time);
            history_model.setCrateddate(date);
            history_model.setImage(imagepagepath);
            history_model.setNoofVotes(noofvotes);
            history_model.setRanking(rankin);

            history_model_arraylist.add(history_model);*/


        }

            if(history_model_arraylist.size()>0) {

                photos_details.setVisibility(View.GONE);
                loadimage_grid.setVisibility(View.VISIBLE);
                HistoryOfAllImagesAdapter adapter=new HistoryOfAllImagesAdapter(CameraActivity.this,history_model_arraylist);
                loadimage_grid.setAdapter(adapter);


            }
            else{
                photos_details.setVisibility(View.VISIBLE);
                loadimage_grid.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
        e.printStackTrace();
      }
      }


    class UpdateImageUri extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(CameraActivity.this);
            pd.setMessage("Loading...");
            pd.setCancelable(true);
            pd.setCanceledOnTouchOutside(false);
            pd.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                System.out.println("encodedImageencodedImage"+encodedImage);
                upload_img_final = ConnectApi.ResponseForUploadimage(user_id, title_str, location, encodedImage, description_str, catego, active, created_by);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            pd.dismiss();
            try{
                parsethejsonresponse(upload_img_final);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            dialog.dismiss();
            pd.dismiss();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }



    class LatestHistoryofImages extends AsyncTask<Void,Void,Void>{
       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           pd = new ProgressDialog(CameraActivity.this);
           pd.setMessage("Loading...");
           pd.setCanceledOnTouchOutside(false);
           pd.setCancelable(true);
           pd.show();
       }

       @Override
       protected Void doInBackground(Void... params) {
           try {
               System.out.println("useridcbsdfhjdfvjdfgh"+user_id);
               allimage_img_final = ConnectApi.ResponseForLatestHistory(user_id);
               System.out.println("alll latest images"+allimage_img_final);
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
               parsethelatesthistoryimage(allimage_img_final);
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
            if (Boolean.valueOf(json_obj1.getString("status")) == true) {
                JSONArray json_arra1 = json_obj1.getJSONArray("result");

                System.out.println("json array i lengthe is "+json_arra1.length());

                for (int i = 0; i < json_arra1.length(); i++) {

                    System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiii"+i);
                    JSONObject json_obj2 = json_arra1.getJSONObject(i);
                    System.out.println("json_obj2json_obj2"+json_obj2.toString());
                    create_date = json_obj2.getString("created_date");
                    System.out.println("craeted date" + create_date);


                    history_model = new ModelforHistory();

                    history_model.setDates(create_date);

                    photos = new ArrayList<String>();



                    JSONArray today_ph_arr=json_obj2.optJSONArray("today_photos");
                    if(today_ph_arr!=null) {
                        System.out.println("today photos arraylist" + today_ph_arr.length());
                        for (int k = 0; k < today_ph_arr.length(); k++) {
                            JSONObject today_ph_obj = today_ph_arr.getJSONObject(k);
                            photo_path = today_ph_obj.getString("photo_path");
                            String text = "(Today Photos)";
                            history_model.setDate_text(text);
                            photos.add(photo_path);
                        }
                    }



                    JSONArray yester_ph_arr=json_obj2.optJSONArray("yesterday_photos");
                    if(yester_ph_arr!=null){
                        System.out.println("yserterfay phtoh"+yester_ph_arr.length());
                        for(int h=0;h<yester_ph_arr.length();h++){
                            JSONObject yserte_ph_obj=yester_ph_arr.getJSONObject(h);
                            photo_path=yserte_ph_obj.getString("photo_path");
                            String text="(Yesterday Photos)";
                            history_model.setDate_text(text);
                            photos.add(photo_path);
                        }
                    }






                    JSONArray json_arra2 = json_obj2.optJSONArray("datewise_data");
                    if(json_arra2!=null){
                    System.out.println("json size ssssssssss" + json_arra2.length());



                    for (int j = 0; j < json_arra2.length(); j++) {

                        JSONObject json_obj3 = json_arra2.getJSONObject(j);
                        photo_path = json_obj3.getString("photo_path");
                        compare_date = json_obj3.getString("created_date");
                        photos.add(photo_path);


                    //    history_model.setUrls(photos);

                    }}

                    System.out.println("photos size ssssssssss" + photos.size());

              /*  images_child_list.add(photos);
                System.out.println("images arraylist"+images_child_list.size());*/
                    history_model.setUrls(photos);
                    hist_model_arralist.add(history_model);

                    System.out.println("size of tem arrraylist" + hist_model_arralist.size());
                }




                if((hist_model_arralist.size() >0)){
                    HistoryAdapter adapter=new HistoryAdapter(CameraActivity.this,hist_model_arralist) ;
                    adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);

                    expand_list.setAdapter(adapter);

                }
                else{
                    photos_details.setVisibility(View.VISIBLE);
                }

            }

        else{
                photos_details.setVisibility(View.VISIBLE);
            }





        }
        catch(Exception e){
            e.printStackTrace();
        }















        /*try{
            history_model_arraylist=new ArrayList<>();
            JSONObject json_obj1=new JSONObject(allimage_img_final);
            JSONArray json_arra1=json_obj1.getJSONArray("result");

            history_model_arraylist.clear();
            for(int i=0;i<json_arra1.length();i++){
               JSONObject json_obj2=json_arra1.getJSONObject(i);
                create_date=json_obj2.getString("created_date");
                System.out.println("createtetjcdjhvbdvbmndv"+create_date);

                JSONArray json_arra2=json_obj2.getJSONArray("datewise_data");
                for(int j=0;j<json_arra2.length();j++){
                    JSONObject json_obj3=json_arra2.getJSONObject(j);
                    photo_path=json_obj3.getString("photo_path");
                    System.out.println("pathhhhhhhhhhhhh"+photo_path);
                    String no_votes=json_obj3.getString("no_votes");
                    String rank=json_obj3.getString("rank");


                    history_model=new HistoryImageModel();

                    history_model.setCrateddate(create_date);
                    history_model.setImage(photo_path);
                    history_model_arraylist.add(history_model);

                   *//* history_model.setNoofVotes(no_votes);
                    history_model.setRanking(rank);
                   *//* // history_model.setTime(time);

                }
            }
        }
        catch(Exception e){
            e.printStackTrace();

        }*/
    }
}