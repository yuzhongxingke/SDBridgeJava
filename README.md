![](Resource/SDBridgeJava.png)
![language](https://img.shields.io/badge/Language-Java-green)
![language](https://img.shields.io/badge/support-Javascript/Async/Await-green)
![language](https://img.shields.io/badge/support-Jitpack-green)

[SDBridgeKotlin](https://github.com/SDBridge/SDBridgeKotlin) is [here](https://github.com/SDBridge/SDBridgeKotlin).

If your h5 partner confused about how to deal with iOS and Android.
[This Demo maybe help](https://github.com/SDBridge/TypeScriptDemo).

[接入教程和常见问题都在这里](https://search.bilibili.com/all?vt=10558286&keyword=SDBridgeJava&from_source=webtop_search&spm_id_from=333.788).

[YouTube video is here](https://www.youtube.com/channel/UCejg0KqpAvoEem4v5y1QbpA/videos).

Usage
-----

## JitPack.io

I strongly recommend https://jitpack.io
```groovy
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.SDBridge:SDBridgeJava:1.0.4'
}
```

1) Instantiate bridge with a WebView in Java:
```Java
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
```
2) In Java, and call a Javascript Sync/Async function:
```Java
 @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button2) {
            HashMap<String, String> data = new HashMap<>();
            data.put("AndroidKey","AndroidValue");
            //Call Javascript Sync function 
            bridge.call("GetToken", data, map -> {
                System.out.println("Next line is javascript data->>>");
                System.out.println(map);
            });
        }else if(view.getId() == R.id.button3){
            HashMap<String, String> data = new HashMap<>();
            data.put("AndroidKey","AndroidValue");
            ///Call Javascript Async function 
            bridge.call("AsyncCall", data, map -> {
                System.out.println("Next line is javascript data->>>");
                System.out.println(map);
            });
        }
    }
```
3) In javascript file or typescript and html file like :
	
```javascript
<div id="SDBridge"> web content </div>
<script>
    // Give webview 1.5s to load other javascript files.
    setTimeout(()=>{
        console.log("Demo222222222222222222");
        const bridge = window.WebViewJavascriptBridge;
        // JS tries to call the native method to judge whether it has been loaded successfully and let itself know whether its user is in android app or IOS app
        bridge.callHandler('DeviceLoadJavascriptSuccess', {key: 'JSValue'}, function(response) {
            let result = response.result
            if (result === "iOS") {
            console.log("Javascript was loaded by IOS and successfully loaded.");
            document.getElementById("SDBridge").innerText = "Javascript was loaded by IOS and successfully loaded.";
            window.iOSLoadJSSuccess = true;
           } else if (result === "Android") {
            console.log("Javascript was loaded by Android and successfully loaded.");
            document.getElementById("SDBridge").innerText = "Javascript was loaded by Android and successfully loaded.";
            window.AndroidLoadJSSuccess = true;
           }
        });
        // JS register method is called by native
        bridge.registerHandler('GetToken', function(data, responseCallback) {
            console.log(data);
            document.getElementById("SDBridge").innerText = "JS get native data:" + JSON.stringify(data);
            let result = {token: "I am javascript's token"}
            //JS gets the data and returns it to the native
            responseCallback(result)
        });
        bridge.registerHandler('AsyncCall', function(data, responseCallback) {
            console.log(data);
            document.getElementById("SDBridge").innerText = "JS get native data:" + JSON.stringify(data);
            //Call await function must with  (async () => {})();
            (async () => {
            const callback = await generatorLogNumber(1);
            let result = {token: callback};
            responseCallback(result);
            })();
         });
        function generatorLogNumber(n){
            return new Promise(res => {
            setTimeout(() => {
             res("Javascript async/await callback Ok");
                 }, 1000);
            })
       }
},1500);

</script>
```
# Global support for free
WhatsApp:
[SDBridgeJava Support](https://chat.whatsapp.com/ETPoddqgo3K8185st4asCc)

Telegram:
[SDBridgeJava Support](https://t.me/+gfnIcLrYbYoxNWM1)

WeChat Group:

![](Resource/SDBridgeJavaSupport.JPG)

Email:
housenkui@gmail.com

# History version update ?
[v1.0.3](https://github.com/SDBridge/SDBridgeJava)

1.Java can get console.log [Multi parameter](https://github.com/SDBridge/SDBridgeJava/blob/master/SDBridgeJava/src/main/assets/hookConsole.js#L33).

[v1.0.4](https://github.com/SDBridge/SDBridgeJava)

1.Optimized coding.

## License

SDBridgeSwift is released under the MIT license. [See LICENSE](https://github.com/SDBridge/SDBridgeJava/blob/master/LICENSE) for details.
