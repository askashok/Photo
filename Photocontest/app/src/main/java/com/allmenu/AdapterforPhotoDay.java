package com.allmenu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import java.util.ArrayList;

import photocontent.bliss.com.api.ConnectApi;
import photocontest.bliss.com.photocontest.CustomalertDialog;
import photocontest.bliss.com.photocontest.MyRankingActivity;
import photocontest.bliss.com.photocontest.NetworkValidation;
import photocontest.bliss.com.photocontest.R;

/**
 * Created by Jenifa Mary.C on 5/16/2015.
 */
public class AdapterforPhotoDay extends BaseAdapter{
    private Context context;
    String name_str,comment_str,myrating_str,user_id,today_img_final,userid_position;
    Float myrating_float;
    ArrayList<PhotoDayModel> photoofday_arraylist;
    SVG photooftheday_pg_svg;
    LayoutInflater inflater;


    SharedPreferences pref;
    public AdapterforPhotoDay(Context context, ArrayList<PhotoDayModel> photoofday_arraylist) {

        this.context=context;
        this.photoofday_arraylist=photoofday_arraylist;
        pref=context.getSharedPreferences("Options",context.MODE_PRIVATE);
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return photoofday_arraylist.size();
    }

    @Override
    public Object getItem(int position) {
        return photoofday_arraylist.get(position);
        }

    @Override
    public long getItemId(int position) {
        return position;
    }

   class ViewHolder{
       RatingBar ratingBar_id;
       ImageView namea_imag,ratinga_image,commenta_imag;
       TextView photos_layout,name_text,edit_comment;
   }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
    final ViewHolder holder;
        if(convertView==null){
            holder=new ViewHolder();

            convertView=inflater.inflate(R.layout.photo_of_day_adapter,parent,false);

            holder.ratingBar_id=(RatingBar)convertView.findViewById(R.id.ratingBar_id);
            holder.namea_imag=(ImageView)convertView.findViewById(R.id.namea_imag);
            holder.ratinga_image=(ImageView)convertView.findViewById(R.id.ratinga_image);
            holder.commenta_imag=(ImageView)convertView.findViewById(R.id.commenta);
            holder.photos_layout=(TextView)convertView.findViewById(R.id.photos_layout);
            holder.name_text=(TextView)convertView.findViewById(R.id.name_text);
            holder.edit_comment=(TextView)convertView.findViewById(R.id.edit_comment);
try {
    //Setting svg image for back_btn
    holder.namea_imag.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    photooftheday_pg_svg = SVGParser.getSVGFromResource(context.getResources(), R.raw.user);
    holder.namea_imag.setImageDrawable(photooftheday_pg_svg.createPictureDrawable());

    //Setting svg image for back_btn
    holder.ratinga_image.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    photooftheday_pg_svg = SVGParser.getSVGFromResource(context.getResources(), R.raw.ratings);
    holder.ratinga_image.setImageDrawable(photooftheday_pg_svg.createPictureDrawable());

    //Setting svg image for back_btn
    holder.commenta_imag.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    photooftheday_pg_svg = SVGParser.getSVGFromResource(context.getResources(), R.raw.comments);
    holder.commenta_imag.setImageDrawable(photooftheday_pg_svg.createPictureDrawable());
}
catch(Exception e){
    e.printStackTrace();
}
            convertView.setTag(holder);


        }
        else{
            holder=(ViewHolder)convertView.getTag();
        }


        if(photoofday_arraylist.get(position).getName().length()>0){
            if(!photoofday_arraylist.get(position).getName().equalsIgnoreCase(null)){

                try {
    name_str = photoofday_arraylist.get(position).getName();
    comment_str = photoofday_arraylist.get(position).getComments();
    myrating_str = photoofday_arraylist.get(position).getYour_vote();

    myrating_float = Float.parseFloat(myrating_str);


    holder.name_text.setText(name_str);
    holder.edit_comment.setText(comment_str);
    holder.ratingBar_id.setRating(myrating_float);
}
catch(Exception e){
    e.printStackTrace();
}

                holder.photos_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userid_position=photoofday_arraylist.get(position).getUserid();
                        Intent inte=new Intent(context, MyRankingActivity.class);

                        inte.putExtra("particular_userid",userid_position);
                        inte.putExtra("activity_num",2);
                        context.startActivity(inte);
                    }
                });
            }
        }

        return convertView;
    }

    private void loadingTodayImageUrl() {
        new BackGroundActivity().execute();
    }

    class BackGroundActivity extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                user_id = pref.getString("userid_key", "");
                today_img_final = ConnectApi.ResponseForTodayImage(user_id);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
