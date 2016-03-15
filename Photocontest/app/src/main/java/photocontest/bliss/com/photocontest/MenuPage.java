package photocontest.bliss.com.photocontest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allmenu.CameraActivity;
import com.allmenu.PhotoOfTheDayActivity;
import com.allmenu.ToptenimagesActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import com.allmenu.VotePhoto;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jenifa Mary.C on 4/24/2015.
 */
public class MenuPage extends Activity {
    LinearLayout myclick_btn, vote_phot_btn, top10_btn, photofday_btn, myranking_btn, myprofile_layout, settings_btn, about_btn, logut_btn;
    ImageView image_home, myclick_image, vote_photo_image, top10photo_img, photooftheday_img, myranking_img, myprofile_img, settings_img, about_img, logout_img, slide_showimg, dot_one, dot_two, dot_three, dot_four, dot_five;
    SVG create_account_svg;
    private int[] IMAGE_IDS = {R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four, R.drawable.five};
    EasyTracker easyTracker = null;
    Context context;
    Cursor cur;
    InterstitialAd interstitial;
    int index_no = 0, imagecount, currentimageindex = 0;
    ArrayList<String> imagepath;
    String userid;
    int value,newuservalue;
    Timer timer, timer_thr;
    SharedPreferences.Editor editor;

    boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.menuxml);
        context = this;
        easyTracker = EasyTracker.getInstance(context);
        Initialization();
        showAd();

        try {
            Intent getuserid = getIntent();
            userid = getuserid.getStringExtra("UserId");
            SharedPreferences pref = getApplicationContext().getSharedPreferences("Options", MODE_PRIVATE);
            editor = pref.edit();
            editor.putString("userid_key", userid);
            editor.commit();

            slideAllimages();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (value == 1) {
            timer.cancel();
        }
        if (value == 2) {
            timer_thr.cancel();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (value == 1) {
            timer.cancel();
        }
        if (value == 2) {
            timer_thr.cancel();
        }
    }

  /*  @Override
    protected void onResume() {
        super.onResume();

    }
*/
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


   /* private void slidingSmallImages() {
        final Handler mHandler = new Handler();
        // Create runnable for posting
        final Runnable mUpdateResults = new Runnable() {

            @Override
            public void run() {

                AnimateandSlideShow();
            }

        };
    }

    private void AnimateandSlideShow() {
        Animation slideOutRight = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_out_right);
        slideOutRight.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                slide_showimg.bringToFront();
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
*/


    private void slideAllimages() {
        try {
            String[] projection = new String[]{

                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_TAKEN,
                    MediaStore.Images.Media.DATA

            };


            // Get the base URI for the People table in the Contacts content provider.
            Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            // Make the query.
            cur = managedQuery(images,
                    projection, // Which columns to return
                    null,       // Which rows to return (all rows)
                    null,       // Selection arguments (none)
                    null        // Ordering
            );
            imagecount = cur.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cur.moveToFirst()) {
            String path;
            imagepath = new ArrayList<>();
            for (int i = 0; i < cur.getCount(); i++) {
                cur.moveToPosition(i);
                int column_index = cur.getColumnIndex(MediaStore.Images.Media.DATA);
                path = cur.getString(column_index);
                imagepath.add(path);
            }
        }



























        if (imagepath.size() != 0) {
            value=1;
           final Handler mHandler = new Handler();
            final Runnable mUpdateResults = new Runnable() {
                public void run() {

                    AnimateandSlideShow();

                }

                private void AnimateandSlideShow() {
                    Animation slideOutRight = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_out_right);
                    slideOutRight.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationEnd(Animation arg0) {
                            slide_showimg.bringToFront();

                        }

                        @Override
                        public void onAnimationRepeat(Animation arg0) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationStart(Animation arg0) {
                            // TODO Auto-generated method stub

                        }

                    });

                    String path = imagepath.get(index_no);
                   slide_showimg.setImageDrawable(new BitmapDrawable(path));
             //  ResizeimageMethod(path);

                    try {
                        if (currentimageindex == 0) {
                            //Setting svg image for Camera
                            dot_one.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_1);
                            dot_one.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_two.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_two.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_three.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_three.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_four.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_four.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_five.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_five.setImageDrawable(create_account_svg.createPictureDrawable());

                        }

                        if (currentimageindex == 1) {
                            //Setting svg image for Camera
                            dot_one.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_one.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_two.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_1);
                            dot_two.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_three.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_three.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_four.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_four.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_five.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_five.setImageDrawable(create_account_svg.createPictureDrawable());

                        }
                        if (currentimageindex == 2) {
                            //Setting svg image for Camera
                            dot_one.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_one.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_two.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_two.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_three.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_1);
                            dot_three.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_four.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_four.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_five.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_five.setImageDrawable(create_account_svg.createPictureDrawable());

                        }
                        if (currentimageindex == 3) {
                            //Setting svg image for Camera
                            dot_one.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_one.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_two.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_two.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_three.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_three.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_four.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_1);
                            dot_four.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_five.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_five.setImageDrawable(create_account_svg.createPictureDrawable());

                        }
                        if (currentimageindex == 4) {
                            //Setting svg image for Camera
                            dot_one.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_one.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_two.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_two.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_three.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_three.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_four.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_four.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_five.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_1);
                            dot_five.setImageDrawable(create_account_svg.createPictureDrawable());

                        }

                        currentimageindex++;
                        if (currentimageindex == 5) {
                            currentimageindex = 0;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    index_no++;
                    if (index_no == imagecount) {
                        index_no = 0;
                    }
                }
            };
            int delay = 500; // delay for 1 sec.
            int period = 4000; // repeat every 4 sec.
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {

                public void run() {

                    mHandler.post(mUpdateResults);

                }

            }, delay, period);

        } else {
            value=2;
            {
                final Handler mHandler = new Handler();
                // Create runnable for posting
                final Runnable mUpdateResults = new Runnable() {
                    public void run() {
                        try {
                            AnimateandSlide();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    private void AnimateandSlide() {
                        Animation slideOutRight = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_out_right);
                        slideOutRight.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationEnd(Animation arg0) {
                                slide_showimg.bringToFront();

                            }

                            @Override
                            public void onAnimationRepeat(Animation arg0) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onAnimationStart(Animation arg0) {
                                // TODO Auto-generated method stub

                            }

                        });
                        //    slide_showimg.setImageResource(IMAGE_IDS[currentimageindex]);
                        //   slide_showimg.startAnimation(slideOutRight);

                        if (currentimageindex == 0) {
                            //Setting svg image for Camera
                            dot_one.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_1);
                            dot_one.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_two.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_two.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_three.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_three.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_four.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_four.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_five.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_five.setImageDrawable(create_account_svg.createPictureDrawable());

                        }

                        if (currentimageindex == 1) {
                            //Setting svg image for Camera
                            dot_one.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_one.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_two.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_1);
                            dot_two.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_three.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_three.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_four.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_four.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_five.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_five.setImageDrawable(create_account_svg.createPictureDrawable());

                        }
                        if (currentimageindex == 2) {
                            //Setting svg image for Camera
                            dot_one.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_one.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_two.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_two.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_three.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_1);
                            dot_three.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_four.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_four.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_five.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_five.setImageDrawable(create_account_svg.createPictureDrawable());

                        }
                        if (currentimageindex == 3) {
                            //Setting svg image for Camera
                            dot_one.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_one.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_two.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_two.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_three.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_three.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_four.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_1);
                            dot_four.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_five.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_five.setImageDrawable(create_account_svg.createPictureDrawable());

                        }
                        if (currentimageindex == 4) {
                            //Setting svg image for Camera
                            dot_one.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_one.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_two.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_two.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_three.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_three.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_four.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_2);
                            dot_four.setImageDrawable(create_account_svg.createPictureDrawable());

                            //Setting svg image for Camera
                            dot_five.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.slide_1);
                            dot_five.setImageDrawable(create_account_svg.createPictureDrawable());

                        }

                        currentimageindex++;
                        if (currentimageindex == 5) {
                            currentimageindex = 0;
                        }
                    }
                };

                int delay = 1000; // delay for 1 sec.
                int period = 3000; // repeat every 4 sec.
                timer_thr = new Timer();
                timer_thr.scheduleAtFixedRate(new TimerTask() {

                    public void run() {

                        mHandler.post(mUpdateResults);

                    }

                }, delay, period);
            }
        }
    }

    private void ResizeimageMethod(String path) {
        try {
            File imgFile = new File(path);
            if (imgFile.exists()) {

                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int height = displaymetrics.heightPixels / 3;
                int width = displaymetrics.widthPixels;

                Bitmap bit = BitmapFactory.decodeFile(path);
                Bitmap bitmap_path = getResizedBitmap(bit, width, height);

                slide_showimg.setBackground(new BitmapDrawable(bitmap_path));
            } else {
                Drawable res = getResources().getDrawable(R.drawable.ic_launcher);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Initialization() {
        try {
            myclick_btn = (LinearLayout) findViewById(R.id.myclick_btn);
            vote_phot_btn = (LinearLayout) findViewById(R.id.vote_phot_btn);
            top10_btn = (LinearLayout) findViewById(R.id.top10_btn);
            photofday_btn = (LinearLayout) findViewById(R.id.photofday_btn);
            myranking_btn = (LinearLayout) findViewById(R.id.myranking_btn);
            myprofile_layout = (LinearLayout) findViewById(R.id.myprofile_layout);
            settings_btn = (LinearLayout) findViewById(R.id.settings_btn);
            about_btn = (LinearLayout) findViewById(R.id.about_btn);
            logut_btn = (LinearLayout) findViewById(R.id.logut_btn);
            image_home = (ImageView) findViewById(R.id.image);

            myclick_image = (ImageView) findViewById(R.id.myclick_img);
            vote_photo_image = (ImageView) findViewById(R.id.vote_photo_img);
            top10photo_img = (ImageView) findViewById(R.id.top10img_img);
            photooftheday_img = (ImageView) findViewById(R.id.photooftoday_img);
            myranking_img = (ImageView) findViewById(R.id.myranking_img);
            myprofile_img = (ImageView) findViewById(R.id.myprofile_img);
            settings_img = (ImageView) findViewById(R.id.setting_img);
            about_img = (ImageView) findViewById(R.id.about_img);
            logout_img = (ImageView) findViewById(R.id.logout_img);
            slide_showimg = (ImageView) findViewById(R.id.slidingimage);
            dot_one = (ImageView) findViewById(R.id.image_one);
            dot_two = (ImageView) findViewById(R.id.image_two);
            dot_three = (ImageView) findViewById(R.id.image_three);
            dot_four = (ImageView) findViewById(R.id.image_four);
            dot_five = (ImageView) findViewById(R.id.image_five);

            image_home.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.logo);
            image_home.setImageDrawable(create_account_svg.createPictureDrawable());

            //Setting svg image for my click
            myclick_image.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.myclick);
            myclick_image.setImageDrawable(create_account_svg.createPictureDrawable());

            //Setting svg image for vote photo
            vote_photo_image.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.vote);
            vote_photo_image.setImageDrawable(create_account_svg.createPictureDrawable());

            //Setting svg image for top 10 photos
            top10photo_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.top10images);
            top10photo_img.setImageDrawable(create_account_svg.createPictureDrawable());

            //Setting svg image for Photo of the day
            photooftheday_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.photos_of_d_day);
            photooftheday_img.setImageDrawable(create_account_svg.createPictureDrawable());

            //Setting svg image for Ranking
            myranking_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.ranking);
            myranking_img.setImageDrawable(create_account_svg.createPictureDrawable());

            //Setting svg image for Profile
            myprofile_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.top10images);
            myprofile_img.setImageDrawable(create_account_svg.createPictureDrawable());

            //Setting svg image for Settings
            settings_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.settings);
            settings_img.setImageDrawable(create_account_svg.createPictureDrawable());

            //Setting svg image for About
            about_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.about);
            about_img.setImageDrawable(create_account_svg.createPictureDrawable());

            //Setting svg image for Logout
            logout_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.logout);
            logout_img.setImageDrawable(create_account_svg.createPictureDrawable());
        } catch (Exception e) {
            e.printStackTrace();
        }


        myclick_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camaraact = new Intent(MenuPage.this, CameraActivity.class);
                camaraact.putExtra("UserId", userid);
                startActivity(camaraact);

            }
        });


        vote_phot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vote = new Intent(MenuPage.this, VotePhoto.class);
                vote.putExtra("userid", userid);
                vote.putExtra("votephotointent", 1);
                startActivity(vote);
            }
        });


        top10_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MenuPage.this, ToptenimagesActivity.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
            }
        });


        photofday_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photo_of_the_day = new Intent(MenuPage.this, PhotoOfTheDayActivity.class);
                photo_of_the_day.putExtra("photo_rank_day", 1);
                startActivity(photo_of_the_day);
            }
        });


        myranking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   /*  Intent rank = new Intent(MenuPage.this, PhotoOfTheDayActivity.class);
                     rank.putExtra("photo_rank_day", 2);
                     startActivity(rank);*/

                Intent rank = new Intent(MenuPage.this, MyRankingActivity.class);
                rank.putExtra("particular_userid", userid);
                rank.putExtra("activity_num", 1);
                startActivity(rank);
            }
        });


        myprofile_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(MenuPage.this, ProfileActivity.class);
                startActivity(profile);
            }
        });


        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings = new Intent(MenuPage.this, SettingActivity.class);
                startActivity(settings);
            }
        });


        about_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent web = new Intent(MenuPage.this, WebviewActivity.class);
                startActivity(web);
            }
        });


        logut_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logout = new Intent(MenuPage.this, LoginActivity.class);
                startActivity(logout);

                finish();
            }
        });
       /*  alertDialog();*/

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


    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        Bitmap resizedBitmap = null;
        try {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();

            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            // RECREATE THE NEW BITMAP
            resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

            return resizedBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resizedBitmap;
    }

    @Override
    public void onBackPressed() {

      /*  super.onBackPressed();*/
        /*private void alertDialog() {*/
        final Dialog spamdialog = new Dialog(context);
        spamdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        spamdialog.setContentView(R.layout.spam_alert);
        spamdialog.setCancelable(true);
        spamdialog.setCanceledOnTouchOutside(false);
        // set the custom dialog components - text, image and button
        TextView ok_butt = (TextView) spamdialog.findViewById(R.id.ok_butt);
        TextView cancel_butt = (TextView) spamdialog.findViewById(R.id.cancel_butt);
        TextView msg = (TextView) spamdialog.findViewById(R.id.msg);
        msg.setText("Are you sure,you want exist?");
        //    reason_des=(EditText)dialog.findViewById(R.id.reason_id);

        // if button is clicked, close the custom dialog
        ok_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              spamdialog.dismiss();
              finish();
              onBackPressed();
            }
        });
        cancel_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spamdialog.dismiss();
            }
        });

        spamdialog.show();
        //  }


    }

}
