package reco.frame.demo;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import adapter.TvListAdapter;
import broadcast.ReceiverMsg;
import model.ServerBean;
import reco.frame.demo.entity.AlwaysMarqueeTextView;
import reco.frame.demo.entity.AppInfo;
import reco.frame.demo.entity.Config;
import reco.frame.demo.entity.MySerialize;
import reco.frame.tv.TvHttp;
import reco.frame.tv.http.AjaxCallBack;
import reco.frame.tv.http.AjaxParams;
import reco.frame.tv.view.TvListView;
import view.VideoView;


/**
 * Created by Administrator on 2017/5/20.
 */

public class Play2Activity extends BaseActivity {


    private Context MContent;
    private RelativeLayout rrpindao;


    private VideoView myVideoView;
    private int position = 0;
    private MediaController mediaControls;
    private LinearLayout LoadingView;
    private SharedPreferences sharedPreferences;
    private TvHttp tvHttp;
    private TextView txt_play_logo;
    private ImageView img_play_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_play2);

        MContent = this;
        tvHttp = new TvHttp(getApplicationContext());

        initview();
        loadlogo();
        log();

    }

    private void loadlogo() {
        AjaxParams params = new AjaxParams();
        tvHttp.post(Config.getapp, params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object t) {

                super.onSuccess(t);
                String str = t.toString();
                try {
                    JSONObject obj = new JSONObject(str);
                    String title = obj.getString("dianbotitle");
                    String imgurl = obj.getString("dianboimg");
                    if (imgurl != null && !imgurl.equals("")) {
                        txt_play_logo.setVisibility(View.GONE);
                        img_play_logo.setVisibility(View.VISIBLE);

                        ImageLoader.getInstance().displayImage(Config.imgurl + imgurl, img_play_logo);
                    } else {
                        txt_play_logo.setVisibility(View.VISIBLE);
                        img_play_logo.setVisibility(View.GONE);
                        txt_play_logo.setText(title);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {

                super.onFailure(t, errorNo, strMsg);
            }
        });
    }

    private void log() {

        sharedPreferences = getSharedPreferences("db", Context.MODE_PRIVATE); //私有数据
        String code = sharedPreferences.getString("code", "");
        String title = getIntent().getStringExtra("title");
        AjaxParams params = new AjaxParams();
        params.put("code", code);
        params.put("typeid", "1");
        params.put("content", "查看" + title);
        tvHttp.post(Config.addlog, params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object t) {

                super.onSuccess(t);
                String str = t.toString();

            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {

                super.onFailure(t, errorNo, strMsg);
            }
        });
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initview() {
        LoadingView = (LinearLayout) findViewById(R.id.LoadingView);
        rrpindao = (RelativeLayout) findViewById(R.id.rrpindao);
        txt_play_logo = (TextView) findViewById(R.id.txt_play_logo);
        img_play_logo = (ImageView) findViewById(R.id.img_play_logo);


        if (mediaControls == null) {
            mediaControls = new MediaController(MContent);
        }

        // Find your VideoView in your video_main.xml layout
        myVideoView = (VideoView) findViewById(R.id.VideoView);


        try {
            myVideoView.setMediaController(mediaControls);
            String url = getIntent().getStringExtra("url");

            myVideoView.setVideoURI(Uri.parse(url));

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        myVideoView.requestFocus();

        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                myVideoView.seekTo(position);
                if (position == 0) {
                    myVideoView.setBackgroundColor(Color.argb(0, 79, 79, 79));
                    LoadingView.setVisibility(View.GONE);
                    myVideoView.start();
                } else {
                    myVideoView.pause();
                }
            }
        });
        myVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                    //媒体服务器挂掉了。此时，程序必须释放MediaPlayer 对象，并重新new 一个新的。
                    Toast.makeText(MContent,
                            "网络服务错误",
                            Toast.LENGTH_LONG).show();
                } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
                    if (extra == MediaPlayer.MEDIA_ERROR_IO) {
                        //文件不存在或错误，或网络不可访问错误
                        Toast.makeText(MContent,
                                "网络文件错误",
                                Toast.LENGTH_LONG).show();
                    } else if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
                        //超时
                        Toast.makeText(MContent,
                                "网络超时",
                                Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });


    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                //  Toast("你按下中间键");
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                // Toast("你按下下方向键");
                break;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                //  Toast("你按下左方向键");
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                // Toast("你按下右方向键");
                break;

            case KeyEvent.KEYCODE_DPAD_UP:
                // Toast("你按下上方向键");
                break;
            case KeyEvent.KEYCODE_MENU:

                if (rrpindao.getVisibility() == View.VISIBLE) {
                    rrpindao.setVisibility(View.GONE);
                } else {

                    rrpindao.setVisibility(View.VISIBLE);
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
        }
        return false;
    }

    private static boolean isExit = false;


}