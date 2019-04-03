package reco.frame.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapter.NewsAdapter;
import model.News;
import reco.frame.demo.entity.Config;
import reco.frame.tv.TvHttp;
import reco.frame.tv.http.AjaxCallBack;
import reco.frame.tv.http.AjaxParams;

/**
 * Created by Administrator on 2017-08-13.
 */

public class XueXiActivity extends BaseActivity {
    private ListView lv_linye_type;
    private List<News> list;
    private NewsAdapter adapter;
    private Context mContext;
    private TvHttp tvHttp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_linye_type);
        initView();
        initData();


    }

    private void initView() {
        tvHttp = new TvHttp(getApplicationContext());
        lv_linye_type = (ListView) findViewById(R.id.lv_linye_type);
        setTitle(getIntent().getStringExtra("title"));


        lv_linye_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.setClass(mContext, DJnewsActivity.class);
                intent.putExtra("action", "xuexi2");
                intent.putExtra("title", list.get(i).title);
                intent.putExtra("group_code", list.get(i).func_id);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        String action = getIntent().getStringExtra("action");

        AjaxParams params = new AjaxParams();
        params.put("action", action);

        tvHttp.get(Config.news, params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object t) {
                try {
                    String json = t.toString();
                    list = new ArrayList<News>();
                    JSONArray arr = new JSONArray(json);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        String func_id = obj.getString("func_id");
                        String name = obj.getString("name");

                        News news = new News();
                        news.func_id = func_id;
                        news.title = name;

                        list.add(news);
                    }
                    adapter = new NewsAdapter(mContext, list);
                    lv_linye_type.setAdapter(adapter);
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


    }
}
