package reco.frame.demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import adapter.NewsAdapter;
import comment.HtmlEncode;
import model.News;
import reco.frame.demo.entity.Config;
import reco.frame.tv.TvHttp;
import reco.frame.tv.http.AjaxCallBack;
import reco.frame.tv.http.AjaxParams;
import view.VideoView;

/**
 * Created by Administrator on 2017-08-13.
 */

public class XueXiPlayer extends BaseActivity {
    private TextView txt_xuexi_title, txt_xuexi_content;
    private TvHttp tvHttp;
    private VideoView myVideoView;
    private MediaController mediaControls;
    private Context MContent;
    private int position = 0;
    private LinearLayout LoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xuexi_play);

        MContent = this;
        initView();
        initData();
    }

    private void initView() {
        tvHttp = new TvHttp(getApplicationContext());
        setTitle(getIntent().getStringExtra("title"));


        txt_xuexi_title = (TextView) findViewById(R.id.txt_xuexi_title);
        txt_xuexi_content = (TextView) findViewById(R.id.txt_xuexi_content);

        LoadingView = (LinearLayout) findViewById(R.id.LoadingView);
    }

    private void initData() {
        final int id = getIntent().getIntExtra("id", 0);
        AjaxParams params = new AjaxParams();
        params.put("action", "xuexiview");
        params.put("id", String.valueOf(id));
        tvHttp.get(Config.news, params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object t) {
                String title = "";
                try {
                    String json = t.toString();

                    JSONArray arr = new JSONArray(json);
                    title = arr.getJSONObject(0).getString("title");
                    String content = arr.getJSONObject(0).getString("contents");
                    String rec_date = arr.getJSONObject(0).getString("rec_date");

                    txt_xuexi_title.setText(title);
                    txt_xuexi_content.setText("发布时间：" + rec_date);
                    content = content.substring(0, content.indexOf(".mp4") + 4);
                    content = content.substring(content.lastIndexOf("quot;") + 5);
                    String video = "http://222.43.239.226:7001/" + content;
                    play(video);

                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setClass(MContent, WebActivity.class);
                    String url = Config.newsview + "?id=" + String.valueOf(id) + "&action=xuexi";

                    intent.putExtra("title", getIntent().getStringExtra("title"));
                    intent.putExtra("titleshow", title);
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
                // 获取文件下载路径


                super.onSuccess(t);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {

                super.onFailure(t, errorNo, strMsg);
            }
        });

    }

    private void play(String url) {

        if (mediaControls == null) {
            mediaControls = new MediaController(MContent);
        }

        // Find your VideoView in your video_main.xml layout
        myVideoView = (VideoView) findViewById(R.id.VideoView);


        try {
            myVideoView.setMediaController(mediaControls);

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

    }


}
