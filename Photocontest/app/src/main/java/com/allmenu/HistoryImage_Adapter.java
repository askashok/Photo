package com.allmenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import photocontest.bliss.com.photocontest.R;

/**
 * Created by BLT0059 on 6/15/2015.
 */
public class HistoryImage_Adapter extends BaseAdapter{
    Context context;
    ArrayList<String> paths;
    LayoutInflater inflater;
    String path;
    public HistoryImage_Adapter(Context context, ArrayList<String> paths) {
        this.context=context;
        this.paths=paths;
        System.out.println("pathhhhhhh length"+paths.size());
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public Object getItem(int position) {
        return paths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class Viewholder{
        ImageView image;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Viewholder viewholder;
        if(convertView==null){
            viewholder=new Viewholder();
            convertView=inflater.inflate( R.layout.grid_layout,parent,false);
            viewholder.image=(ImageView)convertView.findViewById(R.id.history_image);
            convertView.setTag(viewholder);
        }
        else{
            viewholder=(Viewholder)convertView.getTag();
        }
        if(paths.size()>0){
            if(!paths.get(position).equalsIgnoreCase("")) {
                path=paths.get(position);
                System.out.println("path is "+path);
                 Picasso.with(context).load(path).placeholder(R.drawable.animation_loding) .into(viewholder.image);
            }}
                return convertView;
    }
}
