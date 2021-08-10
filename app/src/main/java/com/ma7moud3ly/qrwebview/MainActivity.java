package com.ma7moud3ly.qrwebview;

import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import io.github.g00fy2.quickie.QRResult;
import io.github.g00fy2.quickie.ScanQRCode;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<QRResult> scanner;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * init the QR scanner object
         * @scannerResult parameter, is a method defined below and receives the QR scanner result
         * remember to add the Camera permission in the manifest
         * <uses-permission android:name="android.permission.CAMERA" />
         */
        scanner = registerForActivityResult(new ScanQRCode(), this::scannerResult);

        //init the web view
        webView = findViewById(R.id.web_view);
        //here is the test web page loaded to the webView {assets/index.html}
        webView.loadUrl("file:///android_asset/index.html");
        //enable JavaScript in the webView
        webView.getSettings().setJavaScriptEnabled(true);

        /**
         * init the javascript interface
         * this class is defined below and considered as the bridge between the {Javascript}
         * code and the native {Java} code
         */
        JavaScriptInterface jsInterface = new JavaScriptInterface();
        /**
         * add the javascript interface to the webView
         * remember this name (JSInterface) , it will be invoked from javascript
         */
        webView.addJavascriptInterface(jsInterface, "JSInterface");

    }

    /**
     * this method receives the QR scanner result and forward it
     * to the javascript function in the webView
     */

    private void scannerResult(QRResult.QRSuccess result) {
        //read the string result
        String value = result.getContent().getRawValue();
        //pass this result to a javascript function called {scannerResult}
        //as you invoke -> scannerResult(value)
        webView.loadUrl("javascript:scannerResult('" + value + "')");
    }

    /**
     * this javascript interface class listens for events coming from the webView javascript
     */
    private class JavaScriptInterface {
        /**
         * this function is invoked from the javascript code
         * with the namespace we defined previously {JSInterface}
         * so, the user click on a {Scan Qr Code} button in the webView,
         * and executes this line of code -> window.JSInterface.scan()
         * this invokes the method scan() below...
         */
        @JavascriptInterface
        public void scan() {
            //start the QR scanner
            scanner.launch(null);
        }
    }

}