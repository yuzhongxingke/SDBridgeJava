package com.housenkui.sdbridgejava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TAG1000";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupView();
    }
    private void setupView() {
        WebView webView = findViewById(R.id.webView);
        setAllowUniversalAccessFromFileURLs(webView);
        Button buttonSync = findViewById(R.id.buttonSync);
        Button buttonAsync = findViewById(R.id.buttonAsync);
        buttonSync.setOnClickListener(this);
        buttonAsync.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonSync) {
            Log.d(TAG,"buttonSync onClick");
        }else if(view.getId() == R.id.buttonAsync){
            Log.d(TAG,"buttonAsync onClick");
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