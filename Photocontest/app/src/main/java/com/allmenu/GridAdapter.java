package com.allmenu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

import photocontest.bliss.com.photocontest.R;

/**
 * Created by BLT0059 on 6/16/2015.
 */
public class GridAdapter extends BaseAdapter /*implements Serializable*/{

     // private static final long serialVersionUID = 1L;

        Context context;
    ArrayList<String> paths;
    LayoutInflater inflater;
    public int gridimageheight;
    GridView gridView;


    public GridAdapter(Context context, ArrayList<String> paths,GridView gridView) {
            this.gridView=gridView;
            this.context=context;
            this.paths=paths;
        //System.out.println("pathhhhhhhhhhhhh"+paths.size());
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
 class ViewHolder{
    ImageView images;
}
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
       final ViewHolder viewholder;
        if(convertView==null){
            viewholder=new ViewHolder();
            convertView=inflater.inflate( R.layout.listadapter,parent,false);

            viewholder.images=(ImageView)convertView.findViewById(R.id.images);


            convertView.setTag(viewholder);
        }
        else{
            viewholder=(ViewHolder)convertView.getTag();
        }
       // parent.setMinimumHeight(1500);

       ViewTreeObserver vto = viewholder.images.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                viewholder.images.getViewTreeObserver().removeOnPreDrawListener(this);
                gridimageheight = viewholder.images.getMeasuredHeight();
                //finalWidth = iv.getMeasuredWidth();
                System.out.println("Height: " + gridimageheight + " Width: " );

                int gridSize=0;

                if(paths.size()>0){

                    if(paths.size()==1){
                        gridSize=paths.size()+1;
                        gridSize=gridSize/2;
                    }
                    else if(paths.size()%2==0){
                        gridSize=paths.size();
                        gridSize=gridSize/2;
                    }
                    else if(paths.size()%2!=0){
                        gridSize=paths.size()+1;
                        gridSize=gridSize/2;
                    }
                    ViewGroup.LayoutParams layoutParams = gridView.getLayoutParams();
                    layoutParams.height = gridimageheight *gridSize;
                    gridView.setLayoutParams(layoutParams);


                }


                return true;
            }
        });
        System.out.println("gridimageheightgridimageheight"+gridimageheight);


        viewholder.images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.image_dialog);
                ImageView image = (ImageView) dialog.findViewById(R.id.image_dia);
                Picasso.with(context).load(paths.get(position)).into(image);

                dialog.show();

                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                share.putExtra(Intent.EXTRA_SUBJECT, "Photo Contest Application");
                share.putExtra(Intent.EXTRA_TEXT, paths.get(position));

                context.startActivity(Intent.createChooser(share, "Share text to..."));

            }
        });
        if(paths.size()>0){
            if(!paths.get(position).equalsIgnoreCase("")){



                String  path=paths.get(position);
                System.out.println("hgxcfhgsvchgbcbcshgbdfshj"+paths);

                Picasso.with(context).load(path).placeholder(R.drawable.animation_loding) .into(viewholder.images);
            }
            else{

            }
        }

        return convertView;
    }
}
