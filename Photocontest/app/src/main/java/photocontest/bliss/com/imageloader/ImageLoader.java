package photocontest.bliss.com.imageloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import photocontest.bliss.com.photocontest.R;


public class ImageLoader {
    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    Context mContext;

    Bitmap bitmap;
    AnimationDrawable frameAnimation;
    private int i_size = 320;

    public ImageLoader(Context context) {
        fileCache = new FileCache(context);
        mContext = context;
        executorService = Executors.newFixedThreadPool(5);
    }

    private int mTag = 0;


    /**
     * LoadImage method for load image from web
     *
     * @param url
     * @param imageView
     * @param tag       pass int value (1,2,3) for<br/>
     *                  1 = thumbImage (ScaleType.FIT_XY),<br/>
     *                  2 = fullImage (ScaleType.FIT_XY),<br/>
     *                  3 = fullImage (ScaleType.FIT_START)
     */
    public synchronized void DisplayImage(String url, ImageView imageView, int tag) {
        Log.v("", "mContext : " + mContext.toString());

        this.mTag = tag;
        if (mTag == 1 || mTag == 4) {
            i_size = Constant.MAX_ThumbImage_SIZE;
        } else {
            i_size = Constant.MAX_Image_SIZE;
        }
        imageViews.put(imageView, url);
        bitmap = memoryCache.get(url);
//System.out.println("urlis"+url);
        //System.out.println("bitmap bitmap"+bitmap);

        if (bitmap != null) {
            //Log.i("memoryCache", "bitmap");
            imageView.setImageBitmap(bitmap);
        } else {
            //Log.i("memoryCache_touch", "bitmap null");
            queuePhoto(url, imageView,0);
          //  imageView.setBackgroundResource(R.drawable.ic_launcher);
           // frameAnimation = (AnimationDrawable) mContext.getResources().getDrawable(R.anim.animation_loding);
            imageView.setImageResource(R.drawable.animation_loding);
            frameAnimation = (AnimationDrawable) imageView.getBackground();
            // imageView.setScaleType(ScaleType.CENTER);
            // imageView.setImageDrawable(frameAnimation);

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    frameAnimation.start();
                }
            }, 0);
        }
    }


    /**
     * LoadImage method for load image from web
     *
     * @param url
     * @param imageView
     * @param tag       pass int value (1,2,3) for<br/>
     *                  1 = thumbImage (ScaleType.FIT_XY),<br/>
     *                  2 = fullImage (ScaleType.FIT_XY),<br/>
     *                  3 = fullImage (ScaleType.FIT_START)
     */
    public synchronized void DisplayImage(String url, ImageView imageView, int tag,int position) {
        Log.v("", "mContext : " + mContext.toString());

        this.mTag = tag;
        if (mTag == 1 || mTag == 4) {
            i_size = Constant.MAX_ThumbImage_SIZE;
        } else {
            i_size = Constant.MAX_Image_SIZE;
        }
        imageViews.put(imageView, url);
        bitmap = memoryCache.get(url);
//System.out.println("urlis"+url);
        //System.out.println("bitmap bitmap"+bitmap);

        if (bitmap != null) {
            //Log.i("memoryCache", "bitmap");
            imageView.setImageBitmap(bitmap);
        } else {
            //Log.i("memoryCache_touch", "bitmap null");
            queuePhoto(url, imageView,position);
            //  imageView.setBackgroundResource(R.drawable.ic_launcher);
            // frameAnimation = (AnimationDrawable) mContext.getResources().getDrawable(R.anim.animation_loding);
            imageView.setBackgroundResource(R.drawable.animation_loding);
            frameAnimation = (AnimationDrawable) imageView.getBackground();
            // imageView.setScaleType(ScaleType.CENTER);
            // imageView.setImageDrawable(frameAnimation);

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    frameAnimation.start();
                }
            }, 0);
        }
    }


    private void queuePhoto(String url, ImageView imageView,int pos) {
        PhotoToLoad p = new PhotoToLoad(url, imageView,pos);
        //executor.execute(new PhotosLoader(p));
        executorService.submit(new PhotosLoader(p,pos));
    }

    public Bitmap getBitmap(String url,int pos) {
        Log.v("", "....." + url);
        File f = fileCache.getFile(url);
        // from SD cache
        Bitmap b = decodeFile(f,pos);
        if (b != null)
            return b;

        // from web
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl
                    .openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f,pos);
            Log.d("Download", ""+pos);

            return bitmap;
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }

    }

    // decodes image and scales it to reduce memory consumption
    public Bitmap decodeFile(File f,int pos) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();

            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = i_size;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (Throwable e) {
            // TODO: handle exception
            e.printStackTrace();
            if (e instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }
    }

// Task for the queue
private class PhotoToLoad {
    public String url;
    public ImageView imageView;

    public PhotoToLoad(String u, ImageView i,int pos) {
        url = u;
        imageView = i;
    }
}

class PhotosLoader implements Runnable {
    PhotoToLoad photoToLoad;
    int pos;

    PhotosLoader(PhotoToLoad photoToLoad,int pos) {
        this.photoToLoad = photoToLoad;
        this.pos=pos;
    }

    @Override
    public void run() {
        if (imageViewReused(photoToLoad)) {
            Log.d("PhotosLoader", ""+pos);

            return;
        }

        Bitmap bmp = getBitmap(photoToLoad.url,pos);

        Log.d("Bitmap", ""+bmp);
        if (bmp != null) {
            memoryCache.put(photoToLoad.url, bmp);
            if (imageViewReused(photoToLoad)) {
                Log.d("PhotosLoader1", ""+pos);

                return;
            }
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad,pos);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }
}

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

// Used to display bitmap in the UI thread
class BitmapDisplayer implements Runnable {
    Bitmap bitmap;
    PhotoToLoad photoToLoad;
    int pos;

    public BitmapDisplayer(Bitmap b, PhotoToLoad p,int pos) {
        bitmap = b;
        photoToLoad = p;
        this.pos=pos;
    }

    public void run() {
        if (imageViewReused(photoToLoad)) {
            Log.d("BitmapDisplayer", ""+pos);

            return;
        }

        Log.v("Bitmap", "" + bitmap);
        if (bitmap != null) {
            //photoToLoad.imageView.setBackgroundResource(0);
            photoToLoad.imageView.setImageBitmap(bitmap);
            if (mTag == 1 || mTag == 2) {
                photoToLoad.imageView.setScaleType(ScaleType.FIT_XY);
            } else if (mTag == 4) {
                photoToLoad.imageView.setScaleType(ScaleType.FIT_CENTER);
            } else if (mTag == 5) {
                photoToLoad.imageView.setScaleType(ScaleType.CENTER_CROP);
            }

            else {
                photoToLoad.imageView.setScaleType(ScaleType.FIT_START);
            }
        } else {
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_launcher);
            photoToLoad.imageView.setImageDrawable(drawable);
        }
    }

}

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

}
