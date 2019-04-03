package reco.frame.demo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.Window;
import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Exception;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Handler;

import reco.frame.demo.entity.Config;
import reco.frame.tv.TvHttp;
import reco.frame.tv.http.AjaxCallBack;
import reco.frame.tv.http.AjaxParams;


public class WebActivity extends Activity {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private long mExitTime;


    private WebView myWebView;

    private ProgressDialog progressDialog;
    private SharedPreferences sp;

    private Context context;
    private String runUrl;
    private TvHttp tvHttp;

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled", "addJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.webview);
        tvHttp = new TvHttp(getApplicationContext());
        sp = getSharedPreferences("db", Context.MODE_PRIVATE);
        setTitle(getIntent().getStringExtra("title"));


        myWebView = (WebView) findViewById(R.id.webview);

        WebSettings webSetings = myWebView.getSettings();
        webSetings.setJavaScriptEnabled(true);

        myWebView.setWebViewClient(new WebViewClient());
        myWebView.setWebChromeClient(new WebChromeClient());


        context = this;
        String url = getIntent().getStringExtra("url");
        myWebView.loadUrl(url);

        String lanmu = getIntent().getStringExtra("title");
        String showtitle = getIntent().getStringExtra("titleshow");
        String code = sp.getString("code", "");
        AjaxParams params = new AjaxParams();
        params.put("code", code);
        params.put("typeid", "3");
        params.put("content", "查看栏目->" + lanmu + "->" + showtitle);
        tvHttp.post(Config.addlog, params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {

                super.onFailure(t, errorNo, strMsg);
            }
        });
    }


}


//以R文件的方式，假设 res/drawable下有 test.jpg文件
//        Bitmap  bitmap = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.test);