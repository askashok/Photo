package com.allmenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.common.access.WatsAppImageCompression;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import photocontent.bliss.com.api.ConnectApi;
import photocontest.bliss.com.photocontest.R;


/**
 * Created by Jenifa Mary.C on 5/2/2015.
 */
public class TodayPhotosActivity extends Activity{

    ImageView back_btn,camera_btn,snap_img,location_img;
    AutoCompleteTextView selectarea;
    EditText shor_des_edt;
    TextView upload__btn;
    RelativeLayout upload_image;
    String timestamp,imagepagepath,encodedImage,area_str,description_str,today_image_response;
    SVG todayphotos_pg_svg;
    ProgressDialog pg;
    byte[] bitmapBytes;
    Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final String IMAGE_DIRECTORY_NAME = "Photo Contest";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today_snap_layout);
        Initialization();
        svgImageSettings();
        apiIntegrationForGetTodayImages();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosingimagedialog();
                loadingimageurl();
                apiIntegrationForGetTodayImages();
            }
        });
    }

    private void loadingimageurl() {
        try{

            area_str=selectarea.getText().toString();
            description_str=shor_des_edt.getText().toString();
        }
        catch (Exception e){

        }
    }

    private void choosingimagedialog() {

        AlertDialog alertdialog = new AlertDialog.Builder(TodayPhotosActivity.this).create();
        alertdialog.setTitle("Choose Image");
        alertdialog.setMessage("Choose from Gallary or Take Photo");

        alertdialog.setButton("Gallary", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                Intent i = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);

            }
        });
        alertdialog.setButton2("Take Photo",new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                        Log.d("file uri", "" + fileUri);
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
        if (Reguestcode == 0) {
            if (resultcode == RESULT_OK) {
                previewCapturedImage();
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
                   /* BitmapFactory.Options option = new BitmapFactory.Options();
                    option.inSampleSize = 4;
                    Bitmap bitmap = BitmapFactory.decodeFile(imagepagepath, option);
                    Matrix matrix = new Matrix();
                    Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                    Bitmap resized = Bitmap.createScaledBitmap(rotatedBitmap, 150, 150, true);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    resized.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                    bitmapBytes = stream.toByteArray();*/
                    String compressedPath = new WatsAppImageCompression(this).compressImage(imagepagepath);
                    Bitmap resized = null;
                    if (compressedPath != null) {
                        File imgFile = new File(compressedPath);
                        if (imgFile.exists()) {
                            resized = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        }
                        upload_image.setVisibility(LinearLayout.VISIBLE);

                        snap_img.setImageBitmap(resized);

                        // converting to base64
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        resized.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        bitmapBytes = stream.toByteArray();
                        encodedImage = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }
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
        }
        else {
            return null;
        }
        return mediafile;
    }


    private void previewCapturedImage() {
        try {

        /*    BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);

            Matrix matrix = new Matrix();
            matrix.postRotate(getImageOrientation(imagepagepath));
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            Bitmap resized = Bitmap.createScaledBitmap(rotatedBitmap, 150, 150, true);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            bitmapBytes = stream.toByteArray();
*/

            String compressedPath = new WatsAppImageCompression(this).compressImage(imagepagepath);
            Bitmap resized = null;
            if (compressedPath != null) {
                File imgFile = new File(compressedPath);
                if (imgFile.exists()) {
                    resized = BitmapFactory.decodeFile(imgFile.getAbsolutePath());


                }

                upload_image.setVisibility(LinearLayout.VISIBLE);
                snap_img.setImageBitmap(resized);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                resized.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                bitmapBytes = stream.toByteArray();

                // converting to base64
                encodedImage = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        }
            catch(NullPointerException e){
                e.printStackTrace();
            }

        }

    private float getImageOrientation(String imagepagepath2) {

        int rotate = 0;
        try {
            File imageFile = new File(imagepagepath2);
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

    private void apiIntegrationForGetTodayImages() {
     new behindclass().execute();
    }

    class behindclass extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg=new ProgressDialog(TodayPhotosActivity.this);
            pg.setMessage("Loading");
            pg.setCancelable(true);
            pg.setCanceledOnTouchOutside(false);
            pg.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            today_image_response=ConnectApi.ResponseForTodayAllPhotos();
            readandparsejson_imageurl(today_image_response);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private void readandparsejson_imageurl(String today_image_response) {
        String msg = null;
        try{
            JSONObject jsonobj=new JSONObject(today_image_response);

                if(Boolean.valueOf(jsonobj.getString("status"))){
                    msg=jsonobj.getString("msg");
                    boolean status = Boolean.valueOf(jsonobj.getString("status"));
                 }
            msg = jsonobj.getString("msg");
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){
            e.printStackTrace();
        }

     }

    private void svgImageSettings() {
        //Setting svg image for back_btn
        back_btn.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        todayphotos_pg_svg = SVGParser.getSVGFromResource(getResources(), R.raw.home);
        back_btn.setImageDrawable(todayphotos_pg_svg.createPictureDrawable());

        //Setting svg image for camera_btn
        location_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        todayphotos_pg_svg = SVGParser.getSVGFromResource(getResources(), R.raw.map);
        location_img.setImageDrawable(todayphotos_pg_svg.createPictureDrawable());

        //Setting svg image for location
        camera_btn.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        todayphotos_pg_svg = SVGParser.getSVGFromResource(getResources(), R.raw.camera_active);
        camera_btn.setImageDrawable(todayphotos_pg_svg.createPictureDrawable());

    }

    private void Initialization() {

        back_btn=(ImageView)findViewById(R.id.back_btn);
        camera_btn=(ImageView)findViewById(R.id.choose_camera_btn);
        snap_img=(ImageView)findViewById(R.id.snap_img);
        selectarea=(AutoCompleteTextView)findViewById(R.id.selectarea);
        shor_des_edt=(EditText)findViewById(R.id.shor_desctiption_edt);
        upload__btn=(TextView)findViewById(R.id.upload_img_server_btn);
        location_img=(ImageView)findViewById(R.id.location_img);
        upload_image=(RelativeLayout)findViewById(R.id.upload_image);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pg.dismiss();
    }
}
