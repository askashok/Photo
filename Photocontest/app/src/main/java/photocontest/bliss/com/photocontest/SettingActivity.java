package photocontest.bliss.com.photocontest;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import com.photocontest.billing.IabHelper;
import com.photocontest.billing.IabResult;
import com.photocontest.billing.Inventory;
import com.photocontest.billing.Purchase;

/**
 * Created by Jenifa Mary.C on 5/20/2015.
 */
public class SettingActivity extends Activity implements View.OnClickListener{
    ImageView remove_add,buy_id,restore_id,rate_id,feedback_id;
    SVG create_account_svg;
    TextView remove_ad,apprate_id,feeedback_id;
    Dialog dialog;
    EditText subject,content;
    String subjectNote,contentNote,feed_back_response;
    CustomalertDialog alert = new CustomalertDialog();
    int value;

    String base64EncodedPublicKey;
    public static boolean removeadd = false;
    static final String ST_INAPPITEM = "removeadd";
    IabHelper mHelper;
    private SharedPreferences mPrefs;
    String restoredText = "";
    SharedPreferences prefs;
    private static SharedPreferences mSharedPreferences;
    public static String MY_PREFS_NAME = "success";
    static final String TAG="In App";
    InterstitialAd interstitial;
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        InitializeAllValues();
        setSvgImage();
        showAd();
    }

    private void showAd() {
        interstitial = new InterstitialAd(this);
        // Insert the Ad Unit ID
        interstitial.setAdUnitId(getResources().getString(R.string.adUnitId));
        // Locate the Banner Ad in activity_main.xml
        adView = (AdView) this.findViewById(R.id.adView);
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
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }

    private void setSvgImage() {
        try {
            //Setting svg image for user_first_name
            remove_add.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.remove);
            remove_add.setImageDrawable(create_account_svg.createPictureDrawable());

            buy_id.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.buy);
            buy_id.setImageDrawable(create_account_svg.createPictureDrawable());

            restore_id.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.restore);
            restore_id.setImageDrawable(create_account_svg.createPictureDrawable());

            rate_id.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.rateapp);
            rate_id.setImageDrawable(create_account_svg.createPictureDrawable());

            feedback_id.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            create_account_svg = SVGParser.getSVGFromResource(getResources(), R.raw.feedback);
            feedback_id.setImageDrawable(create_account_svg.createPictureDrawable());
        }
    catch(Exception e){
    e.printStackTrace();}
    }

    private void InitializeAllValues() {

        remove_add=(ImageView)findViewById(R.id.remove_add_image);
        buy_id=(ImageView)findViewById(R.id.buy_id);
        restore_id=(ImageView)findViewById(R.id.restore_id);
        rate_id=(ImageView)findViewById(R.id.rate_id);
        feedback_id=(ImageView)findViewById(R.id.feedback_id);
        remove_ad=(TextView)findViewById(R.id.remove_ad);
        apprate_id=(TextView)findViewById(R.id.apprate_id);
        feeedback_id=(TextView)findViewById(R.id.feeedback_id);

        feeedback_id.setOnClickListener(this);
        apprate_id.setOnClickListener(this);
        remove_ad.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.remove_ad:
                try{
                    onremoveads_rlClicked();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.apprate_id:
                try{
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run()
                        {
                            AppRater.showRateDialog(SettingActivity.this, null);
                        }
                    });
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.feeedback_id:
                try{
                    feed_back_method();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                break;

        }
    }

    private void onremoveads_rlClicked() {
        Log.d(TAG, "Remove Ads clicked.");
        AccountManager manager = AccountManager.get(this);
        Account[] acc = manager.getAccountsByType("com.google");
        if (acc.length > 0) {

            String payload = "";

            mHelper.launchPurchaseFlow(this, ST_INAPPITEM, 10000,
                    mPurchaseFinishedListener, payload);
        }
        else {
            AlertDialog.Builder accountDialogBuilder = new AlertDialog.Builder(this);

            accountDialogBuilder.setMessage("");

            // set dialog message
            accountDialogBuilder.setMessage("Please Login Google Account").setCancelable(false).setPositiveButton("cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {

                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    startActivityForResult(new Intent(android.provider.Settings.ACTION_ADD_ACCOUNT),4);

                                }
                            });

            AlertDialog alertDialog = accountDialogBuilder.create();

            alertDialog.show();
        }

        // Toast.makeText(getApplicationContext(),
        // "Please Sign in your Gmail Account",Toast.LENGTH_SHORT).show();

    }
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: "
                    + purchase);

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                return;
            }

            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(ST_INAPPITEM)) {
                removeadd = true;
                // bought 1/4 tank of gas. So consume it.
                Log.d(TAG,"removeAdsPurchase was succesful.. starting consumption.");

                SharedPreferences.Editor editor = getSharedPreferences(
                        MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("name", "paid");

                editor.commit();

                restoredText = "paid";
                adView.setVisibility(View.GONE);

            }
        }
    };

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        /**
         * Called to notify that an inventory query operation completed.
         *
         * @param result The result of the operation.
         * @param
         */


        @SuppressWarnings("unused")
        public void onQueryInventoryFinished1(IabResult result,
                                              Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Check for gas delivery -- if we own gas, we should fill up the
            // tank immediately
            Purchase removeAdsPurchase = inventory.getPurchase(ST_INAPPITEM);

            if (removeAdsPurchase != null
                    && verifyDeveloperPayload(removeAdsPurchase)) {
                Log.d(TAG,"User has already purchased this item for removing ads. Write the Logic for removign Ads.");

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("name", "paid");

                editor.commit();

                restoredText = "paid";

                adView.setVisibility(View.GONE);

                return;
            }
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }

        @Override
        public void onQueryInventoryFinished(IabResult result,
                                             com.photocontest.billing.Inventory inv) {

        }
    };

    public interface QueryInventoryFinishedListener {
        /**
         * Called to notify that an inventory query operation completed.
         *
         * @param result
         *            The result of the operation.
         * @param inv
         *            The inventory.
         */

        public void onQueryInventoryFinished(IabResult result, Inventory inv);
    }

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase
                    + ", result: " + result);

            // We know this is the "gas" sku because it's the only one we
            // consume,
            // so we don't check which sku was consumed. If you have more than
            // one
            // sku, you probably should check...

            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in
                // our
                // game world's logic, which in our case means filling the gas
                // tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");
                alert("You have purchased for removing ads from your app.");
            } else {
                complain("Error while consuming: " + result);
            }
            Log.d(TAG, "End consumption flow.");
        }
    };

    void complain(String message) {
        Log.e(TAG, "**** IN APP Purchase Error: " + message);
        alert(message);
    }

    void alert(String message) {
        Log.d(TAG, "Showing alert dialog: " + message);
    }

    @SuppressWarnings("unused")
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        /*
         *
         * It will be the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase
         * and verifying it here might seem like a good approach, but this will
         * fail in the case where the user purchases an item on one device and
         * then uses your app on a different device, because on the other device
         * you will not have access to the random string you originally
         * generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different
         * between them, so that one user's purchase can't be replayed to
         * another user.
         *
         * 2. The payload must be such that you can verify it even when the app
         * wasn't the one who initiated the purchase flow (so that items
         * purchased by the user on one device work on other devices owned by
         * the user).
         *
         * Using your own server to store and verify developer payloads across
         * app installations is recommended.
         */
        return true;
    }


    private void feed_back_method() {
        try {
            value=1;
            dialog = new Dialog(SettingActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);

            dialog.setContentView(R.layout.feedback_dialog);
           /*  dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));*/
            content=(EditText)dialog.findViewById(R.id.feedback_edt);
           // subject = (EditText) dialog.findViewById(R.id.subject_id);
            TextView yes = (TextView) dialog.findViewById(R.id.yes);
            final TextView no = (TextView) dialog.findViewById(R.id.no);

            yes.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    contentNote=content.getText().toString();

                   if(content.getText().toString().trim().equalsIgnoreCase("")){
                       alert.ShowAlert(SettingActivity.this,"Enter Description");
                    }

                    if(!content.getText().toString().trim().equalsIgnoreCase("")){
                        dialog.dismiss();
                        final Intent _Intent = new Intent(android.content.Intent.ACTION_SEND);
                        _Intent.setType("text/html");
                        _Intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.mail_feedback_email)});
                        _Intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.subjectNote));
                        _Intent.putExtra(android.content.Intent.EXTRA_TEXT,contentNote);
                      /*  getString(R.string.mail_feedback_message)*/
                        startActivity(Intent.createChooser(_Intent, getString(R.string.title_send_feedback)));
                      }
                    else {
                    }

                }
            });
            no.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub


                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(content.getWindowToken(), 0);
                    dialog.dismiss();

                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
if(value==1) {
    dialog.dismiss();
}
    }
}

