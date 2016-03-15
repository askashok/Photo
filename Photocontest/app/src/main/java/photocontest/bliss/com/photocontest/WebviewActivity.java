package photocontest.bliss.com.photocontest;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by Jenifa Mary.C on 5/20/2015.
 */
public class WebviewActivity extends Activity {

    ProgressDialog pd = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        WebView webView = new WebView(this);
        webView.setClickable(true);
        webView.setFocusableInTouchMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://www.blisslogix.com/");
        WebClientClass webViewClient = new WebClientClass();
        webView.setWebViewClient(webViewClient);
        WebChromeClient webChromeClient = new WebChromeClient();
        webView.setWebChromeClient(webChromeClient);
        setContentView(webView);

    }
    public class WebClientClass extends WebViewClient {


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            pd = new ProgressDialog(WebviewActivity.this);
            pd.setTitle("Please wait");
            pd.setMessage("Page is loading..");
            pd.setCanceledOnTouchOutside(false);
            pd.setCancelable(true);
            pd.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            pd.dismiss();
        }
    }

    public class WebChromeClass extends WebChromeClient {
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pd.dismiss();
    }

}