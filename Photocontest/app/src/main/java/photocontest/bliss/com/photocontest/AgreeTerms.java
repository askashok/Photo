package photocontest.bliss.com.photocontest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

/**
 * Created by Jenifa Mary.C on 4/29/2015.
 */

public class AgreeTerms extends Activity{
    CheckBox check;
    TextView ok_butt,cancel_butt;
    String user_id;
    ImageView svg_logo;
    SVG svgparser;
    EasyTracker easyTracker = null;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.agreeterms);
        check=(CheckBox)findViewById(R.id.checkbox);
        ok_butt=(TextView)findViewById(R.id.ok_butt);
        cancel_butt=(TextView)findViewById(R.id.cancel);
        svg_logo=(ImageView)findViewById(R.id.svg_image_id);
        context=this;
        easyTracker = EasyTracker.getInstance(context);

        try {
    //setting svg image for logo
    svg_logo.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    svgparser = SVGParser.getSVGFromResource(getResources(), R.raw.logo);
    svg_logo.setImageDrawable(svgparser.createPictureDrawable());

    Intent userid_intent = getIntent();
    user_id = userid_intent.getStringExtra("UserId");
    System.out.println("userrrrrrrrid"+user_id);

      }
      catch(Exception e){
         e.printStackTrace();
      }



        ok_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check.isChecked()) {
                    Intent menu=new Intent(AgreeTerms.this,MenuPage.class);
                    menu.putExtra("NewUserInt",1);
                    menu.putExtra("UserId",user_id);
                    startActivity(menu);
                    finish();
                }
                else{

                    Toast.makeText(getApplicationContext(),"Accept the above Conditions",Toast.LENGTH_SHORT).show();
                }
            }
        });


        cancel_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cancel=new Intent(AgreeTerms.this,LoginActivity.class);
                startActivity(cancel);
            }
        });
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
}
