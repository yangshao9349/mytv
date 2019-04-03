package reco.frame.demo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;

import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;


import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import adapter.TvListAdapter;

import broadcast.ReceiverMsg;
import io.socket.client.Url;
import model.ServerBean;
import reco.frame.demo.entity.AlwaysMarqueeTextView;
import reco.frame.demo.entity.AppInfo;
import reco.frame.demo.entity.Config;
import reco.frame.demo.entity.HttpClient;
import reco.frame.demo.entity.MySerialize;
import reco.frame.demo.entity.Person;
import reco.frame.demo.entity.Utils;
import reco.frame.tv.TvHttp;
import reco.frame.tv.http.AjaxCallBack;
import reco.frame.tv.http.AjaxParams;
import reco.frame.tv.view.TvButton;
import reco.frame.tv.view.TvListView;
import view.VideoView;


/**
 * Created by Administrator on 2017/5/20.
 */

public class PlayActivity extends BaseActivity {


    private Context MContent;
    private SharedPreferences sharedPreferences;
    private List<AppInfo> appList = new ArrayList<AppInfo>();
    private TvListAdapter adapter;
    private TvListView lvtv;
    private RelativeLayout rrpindao;
    private AlwaysMarqueeTextView videoshowtxt;
    private ReceiverMsg receiver;
    private TvHttp tvHttp;
    private VideoView myVideoView;
    private int position = 0;
    private MediaController mediaControls;
    private LinearLayout LoadingView;
    private SharedPreferences sp;
    private int tvid = 0;
    private TextView txtlogotitleright;
    private ImageView imglogo;

    private ImageView img_nosignal;
    private RelativeLayout rrvideo;
    private boolean isloading = false;
    private Timer timer;
    private AppInfo app;//当前电视
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_play);
        sp = getSharedPreferences("db", Context.MODE_PRIVATE);
        MContent = this;

        tvHttp = new TvHttp(getApplicationContext());
        tvload();

        try {
            IntentFilter filter1 = new IntentFilter();
            filter1.addAction(ReceiverMsg.MSG);
            filter1.setPriority(Integer.MAX_VALUE);
            MContent.registerReceiver(myReceiver1, filter1);

            IntentFilter filter2 = new IntentFilter();
            filter2.addAction(ReceiverMsg.kaihui);
            filter2.setPriority(Integer.MAX_VALUE);

            MContent.registerReceiver(myReceiver2, filter2);
        } catch (Exception e) {

        }


    }

    private void tvload() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                LinkedList params = new LinkedList<BasicNameValuePair>();
                String result = HttpClient.doPost(Config.gettv, params);
                MySerialize.saveObject("tv", MContent, result);
                handler.sendEmptyMessage(0);

            }
        }.start();


    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    initListView();
                    initview();
                    isloading();
                    break;
            }
        }
    };

    private BroadcastReceiver myReceiver1 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String str = intent.getStringExtra("chatBean");
                ServerBean bean = (ServerBean) MySerialize.deSerialization(str);

                String zuid = sp.getString("zuid", "");
                if (bean.zuid == 0) {
                    videoshowtxt.setText(bean.content);
                } else if (zuid.equals(String.valueOf(bean.zuid))) {
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

    private void initListView() {
        lvtv = (TvListView) findViewById(R.id.lvtv);
        sharedPreferences = getSharedPreferences("db", Context.MODE_PRIVATE); //私有数据
        String tv = sharedPreferences.getString("tv", "");
        try {
            JSONArray arr = new JSONArray(tv);
            for (int i = 0; i < arr.length(); i++) {
                int id = arr.getJSONObject(i).getInt("id");
                String name = arr.getJSONObject(i).getString("name");
                String url = arr.getJSONObject(i).getString("url");
                String logotitle = arr.getJSONObject(i).getString("logotitle");
                String logoimg = arr.getJSONObject(i).getString("logoimg");
                String desc = arr.getJSONObject(i).getString("desc");
                String nosignalimg = arr.getJSONObject(i).getString("nosignalimg");
                AppInfo app1 = new AppInfo();

                app1.urls = url.split(",");
                app1.id = id;
                app1.title = name;
                app1.logotitle = logotitle;
                app1.logoimg = logoimg;
                app1.desc = desc;
                app1.nosignalimg = nosignalimg;

                if (i == 0) {
                    app1.state = 1;
                } else {
                    app1.state = 0;
                }
                appList.add(app1);
            }
        } catch (Exception e) {

        }


        adapter = new TvListAdapter(getApplicationContext(), appList);
        lvtv.setAdapter(adapter);


        lvtv.setOnItemClickListener(new TvListView.OnItemClickListener() {


            @Override
            public void onItemClick(View item, int position) {
                isloading = false;
                index = 0;
                app = appList.get(position);


                if (!app.logoimg.equals("")) {
                    ImageLoader.getInstance().displayImage(Config.imgurl + app.logoimg, imglogo);
                    imglogo.setVisibility(View.VISIBLE);
                } else {
                    imglogo.setVisibility(View.GONE);
                }
                if (!app.logotitle.equals("")) {
                    txtlogotitleright.setVisibility(View.VISIBLE);
                    txtlogotitleright.setText(app.logotitle);
                } else {
                    txtlogotitleright.setVisibility(View.GONE);
                }
                txtlogotitleright.setGravity(Gravity.RIGHT);


                LoadingView.setVisibility(View.VISIBLE);
                myVideoView.pause();
                myVideoView.setBackgroundColor(0xff000000);
                myVideoView.setVideoURI(Uri.parse(app.urls[index]));
                myVideoView.start();


                tvid = app.id;

                String code = sharedPreferences.getString("code", "");
                AjaxParams params = new AjaxParams();
                params.put("code", code);
                params.put("typeid", "1");
                params.put("content", "查看" + String.valueOf(app.title) + "频道");
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
                isloading();
            }
        });


    }

    private void isloading() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isloading == false) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            error();
                        }
                    });
                }

            }

        }, 1000 * 10);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initview() {
        LoadingView = (LinearLayout) findViewById(R.id.LoadingView);
        rrpindao = (RelativeLayout) findViewById(R.id.rrpindao);
        videoshowtxt = (AlwaysMarqueeTextView) findViewById(R.id.videoshowtxt);

        txtlogotitleright = (TextView) findViewById(R.id.txtlogotitleright);
        txtlogotitleright.setGravity(Gravity.RIGHT);
        imglogo = (ImageView) findViewById(R.id.imglogo);

        img_nosignal = (ImageView) findViewById(R.id.img_nosignal);
        rrvideo = (RelativeLayout) findViewById(R.id.rrvideo);

        if (mediaControls == null) {
            mediaControls = new MediaController(MContent);
        }

        // Find your VideoView in your video_main.xml layout
        myVideoView = (VideoView) findViewById(R.id.VideoView);


        try {
            mediaControls.setVisibility(View.INVISIBLE);
            myVideoView.setMediaController(mediaControls);
            if (appList.size() > 0) {
                app = appList.get(0);
                tvid = app.id;

                if (!app.logoimg.equals("")) {
                    ImageLoader.getInstance().displayImage(Config.imgurl + app.logoimg, imglogo);
                    txtlogotitleright.setVisibility(View.GONE);
                } else if (!app.logotitle.equals("")) {
                    imglogo.setVisibility(View.GONE);
                    txtlogotitleright.setVisibility(View.VISIBLE);
                    txtlogotitleright.setText(app.logotitle);
                }

                myVideoView.setVideoURI(Uri.parse(app.urls[index]));
            }

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        // myVideoView.requestFocus();

        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                myVideoView.seekTo(position);
                rrvideo.setVisibility(View.VISIBLE);
                img_nosignal.setVisibility(View.GONE);
                isloading = true;
                if (position == 0) {
                    myVideoView.setBackgroundColor(Color.argb(0, 79, 79, 79));
                    LoadingView.setVisibility(View.GONE);
                    myVideoView.start();
                } else {
                    myVideoView.pause();
                }
            }
        });

        myVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //播放结束后的动作

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

        if (app.urls.length > (index + 1)) {
            index++;
            Toast.makeText(MContent, "正在切换源" + (index), Toast.LENGTH_SHORT).show();
            myVideoView.setVideoURI(Uri.parse(app.urls[index]));
            isloading();
        } else {
            rrvideo.setVisibility(View.GONE);
            if (!app.nosignalimg.equals("")) {
                ImageLoader.getInstance().displayImage(Config.imgurl + app.nosignalimg, img_nosignal);
                img_nosignal.setVisibility(View.VISIBLE);
                txtlogotitleright.setText("");
            } else {
                String desc = app.desc;
                if (desc.equals("null")) {
                    desc = "没有找到视频数据，请切换其他频道！";
                }
                txtlogotitleright.setText(desc);
                txtlogotitleright.setGravity(Gravity.CENTER);
                img_nosignal.setVisibility(View.GONE);
            }
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

                if (rrpindao.getAlpha() == 1) {
                    rrpindao.setAlpha(0);
                } else {
                    rrpindao.setAlpha(1);

                }
                break;
            case KeyEvent.KEYCODE_BACK:
                if (rrpindao.getAlpha() == 1) {
                    rrpindao.setAlpha(0);
                } else {
                    finish();

                }

                break;
        }
        return false;
    }

    private static boolean isExit = false;

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }
}