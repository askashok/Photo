package com.allmenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.zip.Inflater;

import photocontest.bliss.com.photocontest.R;

/**
 * Created by BLT0059 on 6/5/2015.
 */
public class HistoryOfAllImagesAdapter extends BaseAdapter{
    Context cameraActivity;
    ArrayList<HistoryImageModel> hist_array;
    LayoutInflater inflater;
    public HistoryOfAllImagesAdapter(CameraActivity cameraActivity, ArrayList<HistoryImageModel> hist_array) {
        inflater = (LayoutInflater) cameraActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.cameraActivity=cameraActivity;
        this.hist_array=hist_array;
    }

    @Override
    public int getCount() {
        return hist_array.size();
    }

    @Override
    public Object getItem(int position) {
        return hist_array.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

   public class ViewHolder{
        ImageView mainimagwe;
        TextView date,time,ranking,no_of_vote;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.history_adapter,parent,false);
            holder.mainimagwe=(ImageView)convertView.findViewById(R.id.all_image_adapter);
            holder.date=(TextView)convertView.findViewById(R.id.date_id);
            holder.time=(TextView)convertView.findViewById(R.id.time_id);
            holder.ranking=(TextView)convertView.findViewById(R.id.rating_text);
            holder.no_of_vote=(TextView)convertView.findViewById(R.id.no_vote);
            convertView.setTag(holder);

        }
        else{
            holder=(ViewHolder)convertView.getTag();
        }
        if(hist_array.get(position).getImage().length()>0){
          if(!hist_array.get(position).getImage().equalsIgnoreCase("")){
              try{
                 Picasso.with(cameraActivity)
                        .load(hist_array.get(position).getImage())
                         .placeholder(R.drawable.animation_loding)
                         .into(holder.mainimagwe);
                  holder.date.setText(""+hist_array.get(position).getCrateddate());
                  holder.no_of_vote.setText(""+hist_array.get(position).getNoofVotes());
                  holder.ranking.setText(""+hist_array.get(position).getRanking());
                  holder.time.setText(""+hist_array.get(position).getTime());
              }
              catch (Exception e){
                  e.printStackTrace();
              }
          }
        }

        return convertView;
    }
}
