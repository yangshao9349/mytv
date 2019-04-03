package reco.frame.demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapter.TvGridAdapter;
import reco.frame.demo.entity.AppInfo;
import reco.frame.demo.entity.Config;
import reco.frame.tv.TvHttp;
import reco.frame.tv.http.AjaxCallBack;
import reco.frame.tv.http.AjaxParams;
import reco.frame.tv.view.TvGridView;


/**
 * Created by Administrator on 2017-08-28.
 */

public class VideosActivity extends BaseActivity implements View.OnClickListener {
    private TvGridView tgv_imagelist;
    private TvGridAdapter adapter;
    private TvHttp tvHttp;
    List<AppInfo> appList = new ArrayList<AppInfo>();
    private Context context;
    private EditText etvideoname;
    private Button btnsearch, btnall;
    private String filename = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        setContentView(R.layout.activity_videos);
        initView();
        load();
    }

    private void initView() {
        tgv_imagelist = (TvGridView) findViewById(R.id.tgv_imagelist);
        tvHttp = new TvHttp(getApplicationContext());
        etvideoname = (EditText) findViewById(R.id.etvideoname);
        btnsearch = (Button) findViewById(R.id.btnsearch);
        btnsearch.setOnClickListener(this);
        btnall = (Button) findViewById(R.id.btnall);
        btnall.setOnClickListener(this);


    }

    private void load() {
        AjaxParams params = new AjaxParams();
        params.put("filename", filename);
        tvHttp.get(Config.getvideos, params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object t) {
                try {
                    String json = t.toString();
                    appList = new ArrayList<AppInfo>();
                    JSONArray arr = new JSONArray(json);


                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        String name = obj.getString("name");
                        String type = obj.getString("type");
                        AppInfo app = new AppInfo();
                        app.type = type;
                        //文件夹=1 文件=0
                        if (type.equals("0")) {
                            name = name.substring(0, name.indexOf("."));
                            app.title = name;
                            if (filename.equals("")) {
                                app.imageUrl = Config.video + obj.getString("name");
                                app.logoimg = Config.video + name + ".jpg";
                            } else {
                                app.imageUrl = Config.video + filename + "/" + obj.getString("name");
                                app.logoimg = Config.video + filename + "/" + name + ".jpg";
                            }

                        } else if (type.equals("1")) {
                            app.title = name;
                            app.imageUrl = name;
                            app.logoimg = Config.file;

                        }


                        appList.add(app);


                    }
                    adapter = new TvGridAdapter(getApplicationContext(), appList);
                    tgv_imagelist.setAdapter(adapter);
                    tgv_imagelist.setOnItemClickListener(new TvGridView.OnItemClickListener() {
                        @Override
                        public void onItemClick(View item, int position) {
                            try {
                                AppInfo app = appList.get(position);
                                if (app.type.equals("0")) {
                                    Intent intent = new Intent();
                                    intent.setClass(context, Play2Activity.class);
                                    intent.putExtra("title", filename + "/" + appList.get(position).title);
                                    intent.putExtra("url", appList.get(position).imageUrl);

                                    startActivity(intent);
                                } else {
                                    filename = filename + "/" + app.title;
                                    load();
                                }

                            } catch (Exception e) {

                            }

                        }


                    });
                } catch (Exception e) {

                }
                // 获取文件下载路径


                super.onSuccess(t);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {

                super.onFailure(t, errorNo, strMsg);
            }
        });
        // 初始加载模拟数据


    }

    private void Search(String title) {
        if (appList != null && appList.size() > 0) {
            List<AppInfo> list = new ArrayList<>();
            for (int i = 0; i < appList.size(); i++) {
                if (appList.get(i).title.indexOf(title) != -1) {
                    list.add(appList.get(i));
                }
            }
            adapter = new TvGridAdapter(getApplicationContext(), list);
            tgv_imagelist.setAdapter(adapter);


        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnsearch:
                Search(etvideoname.getText().toString());
                break;
            case R.id.btnall:
                Search("");
                break;
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                if (!filename.equals("")) {
                    filename = filename.substring(0, filename.lastIndexOf("/"));
                    load();
                } else {
                    finish();
                }
            } catch (Exception e) {

            }


        }

        return false;

    }
}
