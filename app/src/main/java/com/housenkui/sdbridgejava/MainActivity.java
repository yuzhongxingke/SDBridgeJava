package com.housenkui.sdbridgejava;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private WebViewJavascriptBridge bridge;
    private static final String TAG = "TAG1000";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupView();
    }
    private void setupView() {
        WebView webview = findViewById(R.id.webView);
        setAllowUniversalAccessFromFileURLs(webview);
        Button buttonSync = findViewById(R.id.buttonSync);
        Button buttonAsync = findViewById(R.id.buttonAsync);
        buttonSync.setOnClickListener(this);
        buttonAsync.setOnClickListener(this);

        bridge = new WebViewJavascriptBridge(this,webview);
        bridge.consolePipe(string -> {
            System.out.println("333333333");
            System.out.println(string);
        });
        bridge.register("DeviceLoadJavascriptSuccess", (map, callback) -> {
            System.out.println("Next line is javascript data->>>");
            System.out.println(map);
            HashMap<String,String> result = new HashMap<>();
            result.put("result","Android");
            callback.call(result);
        });
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG,"shouldOverrideUrlLoading");
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG,"onPageStarted");
                bridge.injectJavascript();
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG,"onPageFinished");
            }
        });
        // Loading html in local ，This way maybe meet cross domain. So You should not forget to set
        // /*...setAllowUniversalAccessFromFileURLs... */
        // If you loading remote web server,That can be ignored.
        webview.loadUrl("file:///android_asset/Demo.html");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonSync) {
            Log.d(TAG,"buttonSync onClick");
            HashMap<String, String> data = new HashMap<>();
            data.put("AndroidKey","AndroidValue");
            //call js sync function
            bridge.call("GetToken", data, map -> {
                System.out.println("Next line is javascript data->>>");
                System.out.println(map);
            });
        }else if(view.getId() == R.id.buttonAsync){
            Log.d(TAG,"buttonAsync onClick");
            HashMap<String, String> data = new HashMap<>();
            data.put("AndroidKey","AndroidValue");
            //call js Async function
            bridge.call("AsyncCall", data, map -> {
                System.out.println("Next line is javascript data->>>");
                System.out.println(map);
            });
        }
    }

    //Allow Cross Domain
    private void setAllowUniversalAccessFromFileURLs (WebView webView){
        try {
            Class<?> clazz = webView.getSettings().getClass();
            Method method = clazz.getMethod(
                    "setAllowUniversalAccessFromFileURLs", boolean.class);
            method.invoke(webView.getSettings(), true);
        } catch (IllegalArgumentException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}