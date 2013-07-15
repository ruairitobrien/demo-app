package com.bmac.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;
import android.webkit.*;
import android.widget.Toast;

import java.net.URL;

public class MainActivity extends Activity {

    private static final int RESULT_SETTINGS = 1;

    private static final String DEFAULT_URL = "http://anychanceofarub.com/";

    private static WebView demoWebView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.main);
        demoWebView = (WebView) findViewById(R.id.webview);
        demoWebView.setWebViewClient(new CustomWebViewClient());
        demoWebView.addJavascriptInterface(new WebAppInterface(this), "bmac");
        WebSettings webSettings = demoWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        demoWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                MainActivity.this.setProgress(progress * 1000);
            }
        });

        demoWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        loadSettingsUrl(this, demoWebView);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SETTINGS:
                demoWebView.clearHistory();
                loadSettingsUrl(this, demoWebView);
                break;

        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && demoWebView.canGoBack()) {
            demoWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    /**
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu) Menu button has been pressed
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    /**
     * (non-Javadoc)
     *
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.menuItemSettings:
                    Intent settingsActivity = new Intent(this, SettingsActivity.class);
                    startActivityForResult(settingsActivity, RESULT_SETTINGS);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } catch (Exception ignore) {
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Simple helper method to load the URL saved in the settings or a default URL.
     * Prepends the http protocol if it wasn't added to the URL.
     * Doesn't bother checking if the URL is valid.
     *
     * @param context - Current Context (Activity in this case)
     * @param webView - The web view ot load the URL in to
     */
    private static void loadSettingsUrl(Context context, WebView webView){
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String url = sharedPrefs.getString("prefUrl", DEFAULT_URL);
        if(!url.startsWith("http://")){
            url = "http://" + url;
        }
        webView.loadUrl(url);
    }

    /**
     * Inner class extending WebViewClient. Used to handle events from the WebView.
     */
    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Just returning false here ensures everything is handled within thew WebView.
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(MainActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
        }
    }
}
