package com.allmenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import photocontest.bliss.com.photocontest.MyRankingActivity;
import photocontest.bliss.com.photocontest.R;

/**
 * Created by Jenifa Mary.C on 5/23/2015.
 */
public class AdapterMyRanking extends BaseAdapter{
     Context context;
     ArrayList<Vote_model> model_arraylist;
     SVG rank_photos;
    LayoutInflater inflater;

    public AdapterMyRanking(MyRankingActivity myRankingActivity, ArrayList<Vote_model> model_arraylist) {
        this.context=myRankingActivity;
        this.model_arraylist=model_arraylist;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return model_arraylist.size();
    }

    @Override
    public Object getItem(int position) {
        return model_arraylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder{
        ImageView image,comment_img;
        TextView text_name,date_text,noofvoteedt,vote_text,comment_edit;
        RatingBar rating_rate;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder holder=null;
try{
if(convertView==null){
    holder=new ViewHolder();

    convertView=inflater.inflate(R.layout.rankingadapter,parent,false);
    holder.image=(ImageView)convertView.findViewById(R.id.image);
    holder.comment_img=(ImageView)convertView.findViewById(R.id.comment_img);
    holder.text_name=(TextView)convertView.findViewById(R.id.text_name);
    holder.date_text=(TextView)convertView.findViewById(R.id.date_text);
    holder.noofvoteedt=(TextView)convertView.findViewById(R.id.noofvoteedt);
    holder.vote_text=(TextView)convertView.findViewById(R.id.vote_text);
    holder.comment_edit=(TextView)convertView.findViewById(R.id.comment_edit);
    holder.rating_rate=(RatingBar)convertView.findViewById(R.id.rating_rate_adapter);
    convertView.setTag(holder);
}
    else{
    holder=(ViewHolder)convertView.getTag();
    }

    if(model_arraylist.get(position).getImagepath().length()>0){
    if(!model_arraylist.get(position).getImagepath().equalsIgnoreCase("null")){
        try {

            holder.comment_img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            rank_photos= SVGParser.getSVGFromResource(context.getResources(), R.raw.comments);
            holder.comment_img.setImageDrawable(rank_photos.createPictureDrawable());

            Picasso.with(context).load(model_arraylist.get(position).getImagepath()).placeholder(R.drawable.animation_loding).resize(300, 300).into(holder.image);
            holder.text_name.setText("" + model_arraylist.get(position).getCreatedby());
            holder.date_text.setText("" + model_arraylist.get(position).getDate());
            holder.noofvoteedt.setText("" + model_arraylist.get(position).getNovotes());
            holder.vote_text.setText("" + model_arraylist.get(position).getRanking());
            holder.comment_edit.setText("" + model_arraylist.get(position).getDescription());
            holder.rating_rate.setRating(Float.parseFloat(model_arraylist.get(position).getRating()));
        }
        catch(Exception e){
            e.printStackTrace();
        }
}
    }
}
catch(Exception e){
    e.printStackTrace();
}
        return convertView;
    }
}
