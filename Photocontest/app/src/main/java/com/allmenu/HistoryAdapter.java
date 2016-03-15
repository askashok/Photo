package com.allmenu;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import photocontest.bliss.com.photocontest.ModelforHistory;
import photocontest.bliss.com.photocontest.R;

/**
 * Created by BLT0059 on 6/12/2015.
 */
public class HistoryAdapter extends BaseExpandableListAdapter /*implements Serializable*/{

    //private static final long serialVersionUID=1L;
    HistoryActivity h_activity;
    ArrayList<ModelforHistory> hist_model_arralist;
    ArrayList<String> dates;
    private LayoutInflater inflater;
    ArrayList<String> paths=new ArrayList<String>();
    GridAdapter adapter;
    private Activity activity;

    public HistoryAdapter(CameraActivity historyActivity ,ArrayList<ModelforHistory> hist_model_arralist) {
        this.hist_model_arralist=hist_model_arralist;
        //inflater = (LayoutInflater) historyActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        System.out.println("hist_model_arralisthist_model_arralisthist_model_arralist"+hist_model_arralist.size());
    }

    @Override
    public int getGroupCount() {
        return hist_model_arralist.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return hist_model_arralist.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return hist_model_arralist;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
       final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_main, null);
            holder.textView = (TextView) convertView.findViewById(R.id.text);
            holder.date=(TextView)convertView.findViewById(R.id.date);

            convertView.setTag(holder);
        }
            else{
            holder=(ViewHolder)convertView.getTag();
        }
        System.out.println("dates inadapter"+hist_model_arralist.get(groupPosition).getDates());
        holder.textView.setText(hist_model_arralist.get(groupPosition).getDates());
       holder.date.setText(hist_model_arralist.get(groupPosition).getDate_text());

        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);
        return convertView;
    }

    class ViewHolder{
        GridView grid;
        TextView textView,date;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final  ViewHolder holder ;


        paths = hist_model_arralist.get(groupPosition).getUrls();
     //   System.out.println("size of child arraylist "+paths.size());


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.gridview, null);

            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.grid = (GridView) convertView.findViewById(R.id.grid);
        holder.grid.setNumColumns(2);
        adapter = new GridAdapter(activity, paths,holder.grid);
        holder.grid.setAdapter(adapter);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    public void setInflater(LayoutInflater inflater, CameraActivity activity) {
        this.inflater = inflater;
        this.activity = activity;
    }















































   /* Context context;
    ArrayList<ModelforHistory> hist_model_arralist;
    LayoutInflater inflater;
    String date_string;
    ArrayList<String> paths;
    HistoryImage_Adapter image_adapt;

    public HistoryAdapter( Context context, ArrayList<ModelforHistory> hist_model_arralist) {
    this.hist_model_arralist=hist_model_arralist;
      *//*  paths=new ArrayList<>();
*//*
        this.context=context;
        System.out.println("jsonlint length"+hist_model_arralist.size());

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return hist_model_arralist.size();
    }

    @Override
    public Object getItem(int position) {
        return hist_model_arralist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder{
        TextView date_text;
      //  GridView hist_grid;
ImageView image;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       final ViewHolder viewholder;
        if(convertView==null){
            viewholder=new ViewHolder();
            convertView=inflater.inflate( R.layout.history_list_adapter,parent,false);
            viewholder.date_text=(TextView)convertView.findViewById(R.id.dates);
            viewholder.image=(ImageView)convertView.findViewById(R.id.history_image);
          //  viewholder.hist_grid=(GridView)convertView.findViewById(R.id.history_grid);


            convertView.setTag(viewholder);
        }
        else{
            viewholder=(ViewHolder)convertView.getTag();
        }
           if(hist_model_arralist.size()>0){
               if(!hist_model_arralist.get(position).getDates().equalsIgnoreCase("")){



                   date_string=hist_model_arralist.get(position).getDates();
                   System.out.println("hgxcfhgsvchgbcbcshgbdfshj"+date_string);

                  *//* paths=hist_model_arralist.get(position).getUrls();*//*
                   paths=hist_model_arralist.get(position).getUrls();
                   System.out.println("paths sizeeeeeeeeeeeeeeee"+paths.size());

                   String photopath=paths.get(position);
                   System.out.println("path size alllchbch"+photopath);

                   System.out.println("hxvsdghfgdgdffrfgjhfgurn"+paths);

                   viewholder.date_text.setText(date_string);

                   Picasso.with(context).load(photopath).placeholder(R.drawable.animation_loding) .into(viewholder.image);


               //   image_adapt=new HistoryImage_Adapter(context,paths);
                  // viewholder.hist_grid.setAdapter(image_adapt);

               }
                else{
                   Toast.makeText(context,"Null Values are there",Toast.LENGTH_SHORT).show();
               }
               }

        return convertView;
    }*/
}
