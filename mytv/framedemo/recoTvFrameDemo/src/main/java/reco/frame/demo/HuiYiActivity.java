package reco.frame.demo;

import android.annotation.TargetApi;
import android.app.AlertDialog;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;

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

public class HuiYiActivity extends BaseActivity {


    private Context MContent;
    private SharedPreferences sharedPreferences;

    private AlwaysMarqueeTextView videoshowtxt;
    private ReceiverMsg receiver;
    private VideoView myVideoView;
    private int position = 0;
    private MediaController mediaControls;
    private LinearLayout LoadingView;
    private SharedPreferences sp;
    private int tvid = 0;
    private ImageView imglogo;
    private String nosignalimg;
    private String desc;
    private TextView txtlogotitleright;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_huiyi);
        sp = getSharedPreferences("db", Context.MODE_PRIVATE);
        MContent = this;

        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(ReceiverMsg.MSG);
        filter1.setPriority(Integer.MAX_VALUE);
        MContent.registerReceiver(myReceiver1, filter1);

        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(ReceiverMsg.endkaihui);
        filter2.setPriority(Integer.MAX_VALUE);

        MContent.registerReceiver(myReceiver2, filter2);

        initview();


    }

    private BroadcastReceiver myReceiver1 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String str = intent.getStringExtra("chatBean");
                ServerBean bean = (ServerBean) MySerialize.deSerialization(str);

                String zuid = sp.getString("zuid", "");
                if (bean.zuid == 0 && tvid == bean.tvid) {
                    videoshowtxt.setText(bean.content);
                } else if (zuid.equals(String.valueOf(bean.zuid)) && tvid == bean.tvid) {
                    videoshowtxt.setText(bean.content);
                }

            } catch (Exception e) {

            }

        }

    };
    private BroadcastReceiver myReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }

    };


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initview() {

        tvid = getIntent().getIntExtra("tvid", 0);
        LoadingView = (LinearLayout) findViewById(R.id.LoadingView);
        imglogo = (ImageView) findViewById(R.id.imglogo);
        txtlogotitleright = (TextView) findViewById(R.id.txtlogotitleright);

        videoshowtxt = (AlwaysMarqueeTextView) findViewById(R.id.videoshowtxt);


        if (mediaControls == null) {
            mediaControls = new MediaController(MContent);
        }

        // Find your VideoView in your video_main.xml layout
        myVideoView = (VideoView) findViewById(R.id.VideoView);


        try {
            myVideoView.setMediaController(mediaControls);
            String imgurl = getIntent().getStringExtra("imgurl");
            String logotitle = getIntent().getStringExtra("logotitle");

            if (imgurl != null && !imgurl.equals("")) {
                ImageLoader.getInstance().displayImage(Config.imgurl + imgurl, imglogo);
                imglogo.setVisibility(View.VISIBLE);
                txtlogotitleright.setVisibility(View.GONE);
            } else if (logotitle != null && !logotitle.equals("")) {
                txtlogotitleright.setText(logotitle);
                txtlogotitleright.setVisibility(View.VISIBLE);
                imglogo.setVisibility(View.GONE);
            }

            String url = getIntent().getStringExtra("url");
            myVideoView.setVideoURI(Uri.parse(url));

            nosignalimg = getIntent().getStringExtra("nosignalimg");
            desc = getIntent().getStringExtra("desc");


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
                    error();
                } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
                    if (extra == MediaPlayer.MEDIA_ERROR_IO) {
                        //文件不存在或错误，或网络不可访问错误
                        error();
                    } else if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
                        //超时
                        error();
                    }
                }
                return false;
            }
        });


    }

    private void error() {
        LoadingView.setVisibility(View.GONE);
        if (nosignalimg != null && !nosignalimg.equals("")) {
            ImageLoader.getInstance().displayImage(Config.imgurl + nosignalimg, imglogo);
            imglogo.setVisibility(View.VISIBLE);
            txtlogotitleright.setText("");
        } else {

            txtlogotitleright.setText(desc);
            txtlogotitleright.setGravity(Gravity.CENTER);
            txtlogotitleright.setVisibility(View.VISIBLE);
            imglogo.setVisibility(View.GONE);
        }


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
                break;
            case KeyEvent.KEYCODE_BACK:
                AlertDialog isExit = new AlertDialog.Builder(this).create();
                // 设置对话框标题
                isExit.setTitle("系统提示");
                // 设置对话框消息
                isExit.setMessage("开会中禁止退出");
                // 添加选择按钮并注册监听
                // 显示对话框
                isExit.show();
                break;

        }
        return super.onKeyDown(keyCode, event);
    }


}