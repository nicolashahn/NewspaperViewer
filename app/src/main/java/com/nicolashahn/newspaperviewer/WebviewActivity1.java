package com.nicolashahn.newspaperviewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class WebviewActivity1 extends Activity {

    static final public String MYPREFS = "myprefs";
    static final public String PREF_URL = "restore_url";
    static final public String WEBPAGE_NOTHING = "about:blank";
    static final public String MY_WEBPAGE = "http://users.soe.ucsc.edu/~luca/android.html";
    public String NEWS_URL = "";
    public String HOST_URL = "";

    static final public String LOG_TAG = "WebviewActivity";

    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_activity1);

        myWebView = (WebView) findViewById(R.id.webView1);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Binds the Javascript interface
        myWebView.addJavascriptInterface(new JavaScriptInterface(this), "Android");

        // get the URL from MainActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            NEWS_URL = extras.getString("url");
            Log.i(LOG_TAG, "url = " + NEWS_URL);
        }
        myWebView.loadUrl(NEWS_URL);
        myWebView.loadUrl("javascript:alert(\"Hello\")");
        // stay inside the webview, don't prompt external browser
        myWebView.setWebViewClient(new MyWebViewClient());
        // save the host (chop off http://)
        HOST_URL = NEWS_URL.substring("http://".length(),NEWS_URL.length());
        Log.i(LOG_TAG, "url = "+HOST_URL);
    }

    // from webview slides
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals(HOST_URL)) {
                // This is my web site, so do not override; let my WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

    }

    // The back button should go back in page history, not in app history.
    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            // If it wasn't the Back key or there's no web page history,
            // bubble up to the default
            // system behavior (probably exit the activity)
            super.onBackPressed();
        }
    }

    public class JavaScriptInterface {
        Context mContext; // Having the context is useful for lots of things,
        // like accessing preferences.

        /** Instantiate the interface and set the context */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void myFunction(String args) {
            final String myArgs = args;
            Log.i(LOG_TAG, "I am in the javascript call.");
            runOnUiThread(new Runnable() {
                public void run() {
                    Button v = (Button) findViewById(R.id.button1);
                    v.setText(myArgs);
                }
            });

        }
    }
    @Override
    public void onPause() {

        Method pause = null;
        try {
            pause = WebView.class.getMethod("onPause");
        } catch (SecurityException e) {
            // Nothing
        } catch (NoSuchMethodException e) {
            // Nothing
        }
        if (pause != null) {
            try {
                pause.invoke(myWebView);
            } catch (InvocationTargetException e) {
            } catch (IllegalAccessException e) {
            }
        } else {
            // No such method.  Stores the current URL.
            String suspendUrl = myWebView.getUrl();
            SharedPreferences settings = getSharedPreferences(MYPREFS, 0);
            SharedPreferences.Editor ed = settings.edit();
            ed.putString(PREF_URL, suspendUrl);
            ed.commit();
            // And loads a URL without any processing.
            myWebView.clearView();
            myWebView.loadUrl(WEBPAGE_NOTHING);
        }
        super.onPause();
    }

    // on clicking the share button:
    // get the current URL, send it to messenger application
    public void shareButton(View v){
        String shareUrl = myWebView.getUrl();
        Log.i(LOG_TAG, shareUrl);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT,shareUrl);
        intent.setType("text/plain");
        startActivity(intent);
    }
    // kill this activity if we hit the "return to newspapers" button
    public void returnButton(View v){
        Log.i(LOG_TAG,"killing webview activity");
        finish();
    }
}
