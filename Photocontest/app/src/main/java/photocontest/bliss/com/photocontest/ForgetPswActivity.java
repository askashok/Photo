package photocontest.bliss.com.photocontest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import org.json.JSONObject;
import org.w3c.dom.Text;

import photocontent.bliss.com.api.ServiceHandler;
import photocontent.bliss.com.api.ServiceHandlerUrl;

/**
 * Created by Jenifa Mary.C on 4/24/2015.
 */


//This Activity is used to get the password
public class ForgetPswActivity extends Activity {
    Context context;
    LinearLayout submit;
    EditText mailid;
    String name_str, result;
    ProgressDialog pDialog;
    ServiceHandler sh;
    InterstitialAd interstitial;
    SVG camera_snap_pg_svg;
    ImageView close_image,logo;
    Dialog dialog;
    CustomalertDialog alert = new CustomalertDialog();
    EasyTracker easyTracker = null;
    TextView forg_id,con,addre,submit_text;
    int value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.forgetpassword);
        try {
            submit_text=(TextView)findViewById(R.id.submit_text);
            forg_id=(TextView)findViewById(R.id.forg_id);
            con=(TextView)findViewById(R.id.content_id);
            addre=(TextView)findViewById(R.id.email_id);
            mailid = (EditText) findViewById(R.id.forget_mailid);
            submit = (LinearLayout) findViewById(R.id.submit_email);
            sh = new ServiceHandler();
            logo = (ImageView) findViewById(R.id.logo_id);
            context = this;
            easyTracker = EasyTracker.getInstance(context);

            Typeface custom_font = Typeface.createFromAsset(getAssets(), "font/HelveticaNeueLTStd-MdCn.otf");
            forg_id.setTypeface(custom_font);

            Typeface conte = Typeface.createFromAsset(getAssets(), "font/HelveticaNeueLTStd-LtCn.otf");
            con.setTypeface(conte);

            Typeface adda = Typeface.createFromAsset(getAssets(), "font/HelveticaNeueLTStd-MdCn.otf");
            addre.setTypeface(adda);

            Typeface submit = Typeface.createFromAsset(getAssets(), "font/HelveticaNeueLTStd-MdCn.otf");
            submit_text.setTypeface(submit);


            //Setting svg image for back_btn
            logo.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            camera_snap_pg_svg = SVGParser.getSVGFromResource(getResources(), R.raw.logo);
            logo.setImageDrawable(camera_snap_pg_svg.createPictureDrawable());
           // showAd();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    name_str = mailid.getText().toString();
                    validationForRemberMe();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

    }
    //Validate all fields
    private void validationForRemberMe() {
        if (mailid.getText().toString().trim().equalsIgnoreCase("")) {
            alert.ShowAlert(ForgetPswActivity.this, "Enter Email ID");
        }
        else if (!mailid.getText().toString().matches("[a-zA-Z0-9+._%-+]{1,256}" + "@" + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."+ "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+")) {
            alert.ShowAlert(ForgetPswActivity.this, "Invalid Email ID");
        }

        else if(NetworkValidation.checknetConnection(getApplicationContext())){
              new forgetpsw().execute();
          }
        else {
            alert.ShowAlert(ForgetPswActivity.this, "Check your internet connection");
        }
    }

    //This asyntask is used to get the response and parse the response
    class forgetpsw extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ForgetPswActivity.this);
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
                    result = sh.makeServiceCall(ServiceHandlerUrl.getRemberMe(name_str), ServiceHandler.GET);
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
                parsejsonDetail(result);
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    private void parsejsonDetail(String result) {

        try {
            JSONObject reader = new JSONObject(result);
            if (Boolean.valueOf(reader.getString("status")) == true) {
                mailid.setText("");
                value=2;
                dialog= new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.forget_alertdialog);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                TextView requs=(TextView)dialog.findViewById(R.id.requs);
                TextView send=(TextView)dialog.findViewById(R.id.send);


                Typeface ref = Typeface.createFromAsset(getAssets(), "font/HelveticaNeueLTStd-LtCn.otf");
                requs.setTypeface(ref);

                Typeface send_text = Typeface.createFromAsset(getAssets(), "font/HelveticaNeueLTStd-LtCn.otf");
                send.setTypeface(send_text);
                // set the custom dialog components - text, image and button
                TextView done_txt = (TextView) dialog.findViewById(R.id.done_id);

                Typeface done = Typeface.createFromAsset(getAssets(), "font/HelveticaNeueLTStd-LtCn.otf");
                done_txt.setTypeface(done);

                RelativeLayout close_img = (RelativeLayout) dialog.findViewById(R.id.close_btn);

                done_txt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });
                close_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
            else{
                alert.ShowAlert(ForgetPswActivity.this,reader.getString("msg"));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
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
            pDialog.dismiss();
        }
        if(value==2) {
            dialog.dismiss();
        }
    }
}

