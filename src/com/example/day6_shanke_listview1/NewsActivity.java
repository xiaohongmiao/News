package com.example.day6_shanke_listview1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NewsActivity extends Activity {
    
    private WebView mWebView;
    
    //进度条弹窗
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        
        mDialog = new ProgressDialog(NewsActivity.this);
        mDialog.setTitle("科大头条");
        mDialog.setMessage("正在加载中");
        
        mWebView = (WebView) findViewById(R.id.web_view);
        //1.设置使用自己的WebView作为主网页加载控件
        //2.获得加载的不同状态的回调方法
        mWebView.setWebViewClient(new WebViewClient() {
            
            //网络页面开始加载的回调
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mDialog.show(); //显示进度条
            }
            
            //网络页面加载结束的回调
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mDialog.dismiss(); //关闭进度条
            }
        });
        
        //开启JS
        mWebView.getSettings().setJavaScriptEnabled(true);
        //通过上个Activity跳转来的Intent对象获得要加载的连接
        String url = getIntent().getStringExtra("url");
        
        //加载网页
        mWebView.loadUrl(url);
    }

}
