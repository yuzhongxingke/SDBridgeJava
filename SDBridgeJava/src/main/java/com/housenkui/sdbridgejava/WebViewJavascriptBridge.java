package com.housenkui.sdbridgejava;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
public class WebViewJavascriptBridge {
    private final WebView webView;
    public ConsolePipe consolePipe;
    public HashMap<String,Callback> responseCallbacks;
    public HashMap<String,Handler> messageHandlers;
    private Integer uniqueId = 0;
    Context context;
    @SuppressLint("SetJavaScriptEnabled")
    public WebViewJavascriptBridge(Context c,WebView v) {
        context = c;
        webView = v;
        responseCallbacks = new HashMap<>();
        messageHandlers = new HashMap<>();
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // 开启js支持
        webView.addJavascriptInterface(this, "normalPipe");
        webView.addJavascriptInterface(this, "consolePipe");
    }
    @JavascriptInterface
    public void postMessage(String data) {
        System.out.println("222222222222");
        System.out.println(data);
        flush(data);
    }
    @JavascriptInterface
    public void receiveConsole(String data) {
        if (consolePipe != null) {
            consolePipe.post(data);
        }
    }
    public void consolePipe(ConsolePipe c) {
        consolePipe = c;
    }
    public void injectJavascript(){
        String script = getFromAssets(context,"bridge.js");
        webView.loadUrl("javascript:" + script);
        String script1 = getFromAssets(context,"hookConsole.js");
        webView.loadUrl("javascript:" + script1);
    }
    public void register(String handlerName,Handler handler) {
        messageHandlers.put(handlerName,handler);
    }
    public void remove(String handlerName) {
        messageHandlers.remove(handlerName);
    }
    public void call(String handlerName, HashMap<String,String> data,Callback callback) {
        HashMap<String,Object> message = new HashMap<>();
        message.put("handlerName",handlerName);
        if (data != null) {
            message.put("data",data);
        }
        if (callback != null){
            uniqueId += 1;
            String callbackId = "native_cb_" + uniqueId;
            responseCallbacks.put(callbackId,callback);
            message.put("callbackId",callbackId);
        }
        dispatch(message);
    }
    private void flush(String messageString) {
        if (messageString == null) {
            System.out.println("Javascript give data is null");
            return;
        }
        Gson gson = new Gson();
        HashMap message = gson.fromJson(messageString,HashMap.class);
        String responseId = (String)message.get("responseId");
        if (responseId != null) {
            Callback callback = responseCallbacks.get(responseId);
            LinkedTreeMap responseData = (LinkedTreeMap)message.get("responseData");
            callback.call(responseData);
            responseCallbacks.remove(responseId);
        } else {
            Callback callback;
            String callbackID = (String)message.get("callbackId");
            if (callbackID!=null) {
                callback = map -> {
                    HashMap<String,Object> msg = new HashMap();
                    msg.put("responseId",callbackID);
                    msg.put("responseData",map);
                    dispatch(msg);
                };
            } else {
                callback = map -> System.out.println("no logic");
            }
            String handlerName = (String) message.get("handlerName");
            Handler handler = messageHandlers.get(handlerName);
            if (handler == null) {
                String error = String.format("NoHandlerException, No handler for message from JS:%s",handlerName);
                System.out.println(error);
                return;
            }
            LinkedTreeMap treeMap = (LinkedTreeMap)message.get("data");
            handler.handler(treeMap,callback);
        }
    }
    private void dispatch(HashMap<String,Object> message) {
        JSONObject jsonObject = new JSONObject(message);
        String messageString = jsonObject.toString();
        messageString = messageString.replace("\\", "\\\\");
        messageString = messageString.replace("\"", "\\\"");
        messageString = messageString.replace("\'", "\\\'");
        messageString = messageString.replace("\n", "\\n");
        messageString = messageString.replace("\r", "\\r");
        messageString = messageString.replace("\f", "\\f");
        messageString = messageString.replace("\u2028", "\\u2028");
        messageString = messageString.replace("\u2029", "\\u2029");
        String javascriptCommand = String.format("WebViewJavascriptBridge.handleMessageFromNative('%s');",messageString);
        webView.post(() -> webView.evaluateJavascript(javascriptCommand,null));
    }
    private String getFromAssets(Context context,String fileName){
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line;
            String result = "";
            while((line = bufReader.readLine()) != null)
                result += line;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

