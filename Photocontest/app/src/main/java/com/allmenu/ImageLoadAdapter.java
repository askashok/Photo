package com.allmenu;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import photocontest.bliss.com.photocontest.R;

/**
 * Created by Jenifa Mary.C on 4/30/2015.
 */
public class ImageLoadAdapter extends BaseAdapter {
     Context context;
    ArrayList<String>  images_list=new ArrayList<>();
    photocontest.bliss.com.imageloader.ImageLoader imageloader;
    LayoutInflater inflater;


    public ImageLoadAdapter(Context context, ArrayList<String> images_list) {
        this.context=context;
        this.images_list=images_list;

        imageloader=new photocontest.bliss.com.imageloader.ImageLoader(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images_list.size();
    }

    @Override
    public Object getItem(int position) {
        return images_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Viewholder {
        public ImageView image_all;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final  Viewholder holder ;
       if(convertView==null) {
           holder = new Viewholder();
           convertView = inflater.inflate(R.layout.all_image_adapter, null);
           holder.image_all = (ImageView) convertView.findViewById(R.id.all_image_adapter);

           convertView.setTag(holder);
       }
        else{
           holder = (Viewholder) convertView.getTag();
        }



/*
holder.image_all.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

            Intent swipeintent=new Intent(context,SwipeImage.class);
                swipeintent.putExtra("images_position",position);
                context.startActivity(swipeintent);
    }
});
*/




            if(!images_list.get(position).equalsIgnoreCase("null")){
                try {
                    Picasso.with(context)
                            .load(images_list.get(position))
                            .placeholder(R.drawable.animation_loding)
                            .into(holder.image_all);
                }
            catch(Exception e){
            e.printStackTrace();}
            }
        return convertView;
    }


    class GetXMLTask extends AsyncTask<Void,Void,Void>{

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }
}

}
