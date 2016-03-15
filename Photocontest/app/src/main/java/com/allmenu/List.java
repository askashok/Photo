package com.allmenu;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import photocontest.bliss.com.photocontest.R;

/**
 * Created by BLT0059 on 6/13/2015.
 */
public class List extends Activity {
    ListView list;
    ArrayList<String> lisdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        list = (ListView) findViewById(R.id.list);
        lisdata=new ArrayList<>();
        lisdata.add("jenifa");
        lisdata.add("jenifa");
        lisdata.add("jenifa");
        lisdata.add("jenifa");
        lisdata.add("jenifa");
      Myadapter myadapter=new Myadapter(this,lisdata);
    list.setAdapter(myadapter);
    }

    public class Myadapter extends BaseAdapter {
        Context nycon;
        LayoutInflater inflater;
        ImageAdapter imageAdapter;
        ArrayList<String> lisdata;

        public Myadapter(Context con,ArrayList<String> lisdata) {
            nycon = con;
            this.lisdata=lisdata;
            imageAdapter = new ImageAdapter(nycon);
            inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return lisdata.size();
        }

        @Override
        public Object getItem(int position) {
            return lisdata.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.row, parent, false);
            TextView title_id=(TextView)convertView.findViewById(R.id.title_id);
            title_id.setText(lisdata.get(position));
            GridView grid = (GridView)convertView.findViewById(R.id.row);
            grid.setAdapter(imageAdapter);
            return convertView;
        }
    }
}
