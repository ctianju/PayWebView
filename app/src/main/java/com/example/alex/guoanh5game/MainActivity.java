package com.example.alex.guoanh5game;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private WebView gameWebView;
    private static MainActivity instance;

    private static void showView(String wxString) {
        if (instance != null) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(wxString));
            instance.startActivity(intent);
        }
    }

    public void setWebSettings(WebSettings webSettings) {
        //可以使用javaScript
        webSettings.setJavaScriptEnabled(true);
        //支持js可以自动打开动态页面
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true); //允许dom存储，必须添加
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    public void findViews() {
        gameWebView = (WebView) findViewById(R.id.game_webView);
//		String urlString = "file:///android_asset/index.html";
        String urlString = "http://h5.wuzhiyou.com/game/gameStart?id=RNCePJyUbAo22arsC4XClmy7x5FaJln1iMetvTpFD5Jg";
//        urlString = "http://m.y.qq.com";
        Log.e(TAG, "yx访问的url地址：" + urlString);
        gameWebView.loadUrl(urlString);
        WebSettings webSettings = gameWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);  //支持js

        webSettings.setDomStorageEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);  //提高渲染的优先级

//        设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true);  //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        webSettings.setSupportZoom(true);  //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。
//若上面是false，则该WebView不可缩放，这个不管设置什么都不能缩放。

        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        webSettings.supportMultipleWindows();  //多窗口
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);  //关闭webview中缓存
        webSettings.setAllowFileAccess(true);  //设置可以访问文件
        webSettings.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true);  //支持自动加载图片


        gameWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Log.e(TAG, "shouldOverrideUrlLoading: " + url);
                boolean isGameUrl = url.indexOf("http://h5.wuzhiyou.com/game/api")!=-1;
                boolean isWxpay = url.indexOf("type=wxpay")!=-1;
                boolean isAlipay = url.indexOf("type=alipay")!=-1;
                if (isGameUrl&&isAlipay) {
                    Log.i("GALog-Loading","the url ->"+url);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent .setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                if (isGameUrl&&isWxpay) {
                    String pramSDK = "&sdk=1";
                    final String urlPostString = url+pramSDK;
                    Thread firstThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String httpReturn = Http_Client.httpClientGet(urlPostString);
                            try {
                                JSONObject json = new JSONObject(httpReturn);
                                JSONObject pay_info = (JSONObject) json.get("pay_info");
                                 final String wxString = pay_info.optString("tn", null);
                                Log.i(TAG, "wxString:"+wxString);
                                MainActivity.showView(wxString);
                                return;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            return;
                        }
                    });
                    firstThread.start();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // 这个方法有可能会多次执行
                super.onPageFinished(view, url);
            }


            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // TODO Auto-generated method stub
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });
        gameWebView.loadUrl(urlString);
        gameWebView.requestFocus();
    }



    @Override
    protected void onDestroy() {
        gameWebView.removeAllViews();
        gameWebView.destroy();
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        findViews();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && gameWebView.canGoBack()) {
            gameWebView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
