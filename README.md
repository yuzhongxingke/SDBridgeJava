![](Resource/SDBridgeJava.png)
![language](https://img.shields.io/badge/Language-Java-green)
![language](https://img.shields.io/badge/support-Javascript/Async/Await-green)


### Manual installation
1.Drag the `example` folder into your project where in `src/main/java/com`.

2.Drag the `bridge.js` `hookConsole` `Demo2.html` files into your project where in `src/main/assets`.

Usage
-----

1) Instantiate bridge with a WebView:
```Java
  private void initView() {
        WebView webview = findViewById(R.id.activity_main_webview);
        setAllowUniversalAccessFromFileURLs(webview);
        Button button = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        button.setOnClickListener(this);
        button3.setOnClickListener(this);
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
        webview.loadUrl("file:///android_asset/Demo2.html");
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
            window.iOSLoadJSSuccess = true;
           } else if (result === "Android") {
            console.log("Javascript was loaded by Android and successfully loaded.");
            window.AndroidLoadJSSuccess = true;
           }
        });
        // JS register method is called by native
        bridge.registerHandler('GetToken', function(data, responseCallback) {
            console.log(data);
            let result = {token: "I am javascript's token"}
            //JS gets the data and returns it to the native
            responseCallback(result)
        });
        bridge.registerHandler('AsyncCall', function(data, responseCallback) {
            console.log(data);
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
# Contact

- Email: housenkui@gmail.com

## License

SDBridgeSwift is released under the MIT license. [See LICENSE](https://github.com/SDBridge/SDBridgeSwift/blob/main/JavascriptBridgeSwift/LICENSE) for details.