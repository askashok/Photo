package com.allmenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;


import java.util.ArrayList;

import photocontest.bliss.com.imageloader.Utils;
import photocontest.bliss.com.photocontest.R;

/**
 * Created by BLT0059 on 6/19/2015.
 */
public class SwipeImage extends Activity {

    private Utils utils;
  ///  private FullScreenImageAdapter adapter;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipepager);

        viewPager = (ViewPager) findViewById(R.id.pager);

        utils = new Utils();

        Intent i = getIntent();
        int position = i.getIntExtra("images_position", 0);

        /*adapter = new FullScreenImageAdapter(SwipeImage.this,
                utils.getFilePaths());

        viewPager.setAdapter(adapter);*/

        // displaying selected image first
        viewPager.setCurrentItem(position);


    }

}