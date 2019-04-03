package reco.frame.demo;


import broadcast.ReceiverMsg;
import comment.CrashHandler;
import comment.UpdateManager;
import io.socket.client.IO;
import model.Menu;
import model.ServerBean;
import reco.frame.demo.entity.AlwaysMarqueeTextView;
import reco.frame.demo.entity.Config;
import reco.frame.demo.entity.HttpClient;
import reco.frame.demo.entity.LocationUtils;
import reco.frame.demo.entity.MySerialize;
import reco.frame.demo.entity.SysApplication;
import reco.frame.demo.entity.TianQi;
import reco.frame.tv.TvHttp;
import reco.frame.tv.http.AjaxCallBack;
import reco.frame.tv.http.AjaxParams;
import reco.frame.tv.view.TvButton;
import reco.frame.tv.view.TvImageView;
import reco.frame.tv.view.TvMarqueeText;
import reco.frame.tv.view.TvProgressBar;
import reco.frame.tv.view.TvTabHost;
import service.LocationService;
import socket.MyServer;
import socket.ServerInterface;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.KeyEvent;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity implements OnClickListener {


    private TvButton btn_huiyi;
    private TvButton btn_xuexi;
    private TvButton btn_cunwu;
    private TvButton btn_jiceng;
    private TvButton btn_set, btn_dianbo, btn_bm;
    private AlwaysMarqueeTextView txtshow;
    private TvHttp tvHttp;
    private TextView txtdate;
    private TextView txthuanying;
    private Context mContext;
    private SharedPreferences sp;
    private View parent;
    private TextView list_content;
    private PopupWindow mPopWindow;
    private RelativeLayout rrmain, rl_bottom;
    private UpdateManager manager;
    private TextView mTitle;
    List<Menu> menus;

    private io.socket.client.Socket mSocket;

    @Override
    public void onDestroy() {
        if (server != null) {
            server.close();
        }

        super.onDestroy();
    }

    public io.socket.client.Socket getSocket() {
        return mSocket;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SysApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_main);
        mContext = this;
        sp = getSharedPreferences("db", Context.MODE_PRIVATE);
        tvHttp = new TvHttp(getApplicationContext());
        //  CrashHandler.getInstance().init(this);//初始化全局异常管理
        try {
            manager = new UpdateManager(MainActivity.this);
            // 检查软件更新
            manager.checkUpdate();

            if (isNetworkConnected(this) == false) {
                Toast.makeText(mContext, "当前网络无连接", Toast.LENGTH_SHORT).show();
            }

            initView();


            initSocket();
            initPopView(); //弹出通知
            MenuLoad();  //下载菜单信息


            kaihuicheck();
            GetNetIp();


        } catch (Exception e) {
            //Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
        }


    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    private void kaihuicheck() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                LinkedList params = new LinkedList<BasicNameValuePair>();
                String result = HttpClient.doPost(Config.kaihuicheck, params);
                try {
                    JSONObject obj = new JSONObject(result);
                    String state = obj.getString("state");
                    if (state.equals("1")) {
                        String url = obj.getJSONObject("obj").getString("url");
                        int tvid = obj.getJSONObject("obj").getInt("id");
                        String imgurl = obj.getJSONObject("obj").getString("logoimg");
                        String nosignalimg = obj.getJSONObject("obj").getString("nosignalimg");
                        String desc = obj.getJSONObject("obj").getString("desc");
                        String logotitle = obj.getJSONObject("obj").getString("logotitle");
                        Intent kaihuiintent = new Intent();
                        kaihuiintent.setClass(mContext, HuiYiActivity.class);
                        kaihuiintent.putExtra("tvid", tvid);
                        kaihuiintent.putExtra("url", url);  //流地址
                        kaihuiintent.putExtra("imgurl", imgurl); //logo图片
                        kaihuiintent.putExtra("nosignalimg", nosignalimg); //无信号图片
                        kaihuiintent.putExtra("desc", desc);  //无信号秒速
                        kaihuiintent.putExtra("logotitle", logotitle);  //Logo 文字
                        startActivity(kaihuiintent);
                    }


                } catch (Exception e) {

                }
            }
        }.start();
    }

    private void MenuLoad() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                LinkedList params = new LinkedList<BasicNameValuePair>();
                String result = HttpClient.doPost(Config.getmenu, params);
                try {
                    menus = new ArrayList<Menu>();

                    JSONObject jsonarr = new JSONObject(result);
                    JSONArray arr = jsonarr.getJSONArray("menu");

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        Menu menu = new Menu();
                        menu.code = obj.getString("code");
                        menu.name = obj.getString("name");
                        menu.imgurl = Config.imgurl + obj.getString("imgurl");

                        //  menu.bitmap = ImageLoader.getInstance().loadImageSync(Config.imgurl + menu.imgurl);
                        menus.add(menu);
                    }

                    JSONArray tongzhiarr = jsonarr.getJSONArray("tongzhi");
                    String str = "";
                    for (int i = 0; i < tongzhiarr.length(); i++) {
                        JSONObject obj = tongzhiarr.getJSONObject(i);
                        str = obj.getString("title");
                    }
                    Message msg = new Message();
                    msg.what = 4;
                    msg.obj = str;

                    handler.sendMessage(msg);

                } catch (Exception e) {

                }
            }
        }.start();


    }


    public byte[] getImage(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return readStream(inStream);
        }
        return null;
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    private void initPopView() {
        try {
            parent = this.getWindow().getDecorView();
            View popView = View.inflate(this, R.layout.view_select_province_list, null);
            mTitle = (TextView) popView.findViewById(R.id.list_title);
            list_content = (TextView) popView.findViewById(R.id.list_content);


            int width = getResources().getDisplayMetrics().widthPixels * 3 / 4;
            int height = getResources().getDisplayMetrics().heightPixels * 3 / 5;
            mPopWindow = new PopupWindow(popView, width, height);

            //  mPopWindow.setBackgroundDrawable(dw);
            mPopWindow.setFocusable(true);
            mPopWindow.setTouchable(true);
            mPopWindow.setOutsideTouchable(true);//允许在外侧点击取消

        } catch (Exception e) {

        }

        // mPopWindow.setOnDismissListener(listener);

    }

    private void initSocket() {
        try {
            if (Config.Socket_URL != "") {
                IO.Options option = new IO.Options();
                option.forceNew = true;
                option.reconnection = true;
                option.reconnectionDelay = 2000;
                mSocket = IO.socket(Config.Socket_URL, option);
            }
            String code = sp.getString("code", "");
            server = new MyServer(new ChatListenUIRefresh(), mContext);
            server.init(code);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取ip地址
     *
     * @return
     */
    public void GetNetIp() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                URL infoUrl = null;
                InputStream inStream = null;
                String line = "";
                try {
                    infoUrl = new URL("http://pv.sohu.com/cityjson?ie=utf-8");
                    URLConnection connection = infoUrl.openConnection();
                    HttpURLConnection httpConnection = (HttpURLConnection) connection;
                    int responseCode = httpConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        inStream = httpConnection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
                        StringBuilder strber = new StringBuilder();
                        while ((line = reader.readLine()) != null)
                            strber.append(line + "\n");
                        inStream.close();
                        // 从反馈的结果中提取出IP地址
                        int start = strber.indexOf("{");
                        int end = strber.indexOf("}");
                        String json = strber.substring(start, end + 1);
                        if (json != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                line = jsonObject.optString("cip");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Message msg = new Message();
                        msg.what = 5;
                        msg.obj = line;
                        handler.sendMessage(msg);


                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

    private String intToIp(int i) {

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }


    private void initmenu() {
        //菜单初始化

        try {

            for (int i = 0; i < menus.size(); i++) {
                final Menu menu = menus.get(i);
                new Thread() {
                    @Override
                    public void run() {
                        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(menu.imgurl);
                        Message msg = new Message();
                        msg.what = 7;
                        msg.arg1 = Integer.parseInt(menu.code);
                        msg.obj = bitmap;
                        handler.sendMessage(msg);

                        super.run();
                    }
                }.start();

            }
        } catch (Exception e) {

        }


    }

    Drawable bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    //socket客户端事件监听处理
    private class ChatListenUIRefresh implements ServerInterface {


        @Override
        public void onMessageListen(ServerBean chatBean) {


            if (chatBean.type.equals("tongzhi")) {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = chatBean;
                handler.sendMessage(msg);

            } else if (chatBean.type.equals("tanping")) {

                Message msg = new Message();
                msg.what = 2;
                msg.obj = chatBean;
                handler.sendMessage(msg);
                try {
                    Thread.sleep(1000 * 10);
                    handler.sendEmptyMessage(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else if (chatBean.type.equals("videotongzhi")) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(ReceiverMsg.MSG);
                    intent.putExtra("chatBean", MySerialize.serialize(chatBean));
                    sendBroadcast(intent);
                } catch (Exception e) {

                }

            } else if (chatBean.type.equals("kaihui")) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(ReceiverMsg.kaihui);
                    sendBroadcast(intent);
                    String[] items = chatBean.content.split("\\|");
                    Intent kaihuiintent = new Intent();
                    kaihuiintent.setClass(mContext, HuiYiActivity.class);
                    kaihuiintent.putExtra("tvid", chatBean.tvid);
                    kaihuiintent.putExtra("url", items[0]);
                    if (items.length > 1) {
                        kaihuiintent.putExtra("imgurl", items[1]);
                        kaihuiintent.putExtra("nosignalimg", items[2]);
                        kaihuiintent.putExtra("desc", items[3]);
                        kaihuiintent.putExtra("logotitle", items[4]);

                    } else {
                        kaihuiintent.putExtra("imgurl", "");
                    }

                    startActivity(kaihuiintent);

                } catch (Exception e) {

                }
            } else if (chatBean.type.equals("endkaihui")) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(ReceiverMsg.endkaihui);
                    sendBroadcast(intent);
                } catch (Exception e) {

                }

            } else if (chatBean.type.equals("updatetv")) {

            } else if (chatBean.type.equals("updateimg")) {
                MenuLoad();
            } else if (chatBean.type.equals("update")) {

                // 检查软件更新
                manager.checkUpdate();
            }
        }

        @Override
        public void onConnect(boolean res) {
            if (res) {
                handler.sendEmptyMessage(6);
            }
        }

        @Override
        public void onError() {

        }

        @Override
        public void onDisconnect() {

        }

    }


    private boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;
    }


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(msg.obj.toString());

                        txtshow.setText(obj.getString("title"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case 1:
                    try {
                        ServerBean bean = (ServerBean) msg.obj;
                        String zuid = sp.getString("zuid", "");
                        if (bean.zuid == 0) {
                            txtshow.setText(bean.content);

                        } else if (zuid.equals(String.valueOf(bean.zuid))) {
                            txtshow.setText(bean.content);

                        }


                    } catch (Exception e) {

                    }
                    break;
                case 2:
                    try {
                        ServerBean bean = (ServerBean) msg.obj;
                        String zuid = sp.getString("zuid", "");
                        if (bean.zuid == 0) {
                            list_content.setText(bean.content);
                            mPopWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
                        } else if (zuid.equals(String.valueOf(bean.zuid))) {
                            list_content.setText(bean.content);
                            mPopWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
                        }

                    } catch (Exception e) {

                    }

                    break;
                case 3:
                    if (mPopWindow != null) {
                        mPopWindow.dismiss();
                    }
                    break;
                case 4:
                    String str = msg.obj.toString();
                    txtshow.setText(str);
                    initmenu(); //更新菜单
                    break;
                case 5:
                    addlog(msg.obj.toString());
                    break;
                case 6:
                    // Toast.makeText(mContext, "已连接", Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    if (msg.arg1 == 0) {
                        rrmain.setBackground(bitmap2Drawable(bitmap));
                    } else if (msg.arg1 == 1) {
                        btn_huiyi.setBackground(bitmap2Drawable(bitmap));
                    } else if (msg.arg1 == 2) {
                        btn_jiceng.setBackground(bitmap2Drawable(bitmap));
                    } else if (msg.arg1 == 3) {
                        btn_xuexi.setBackground(bitmap2Drawable(bitmap));
                    } else if (msg.arg1 == 4) {
                        btn_set.setBackground(bitmap2Drawable(bitmap));
                    } else if (msg.arg1 == 5) {
                        btn_cunwu.setBackground(bitmap2Drawable(bitmap));
                    } else if (msg.arg1 == 6) {
                        btn_dianbo.setBackground(bitmap2Drawable(bitmap));
                    } else if (msg.arg1 == 7) {
                        btn_bm.setBackground(bitmap2Drawable(bitmap));
                    } else if (msg.arg1 == 9) {

                        saveBitmap(bitmap, "home.jpg");
                    }
                    break;


            }


        }
    };

    public void saveBitmap(final Bitmap bm, final String picName) {


        new Thread() {
            @Override
            public void run() {
                File f = new File(Environment.getExternalStorageDirectory(), picName);
                if (f.exists()) {
                    f.delete();
                }
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(f);
                    bm.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                super.run();
            }
        }.start();


    }


    @Override
    protected void onResume() {


        super.onResume();
    }


    @Override
    protected void onPause() {

        super.onPause();
    }

    private void initView() {
        btn_huiyi = (TvButton) findViewById(R.id.btn_huiyi);
        btn_huiyi.setOnClickListener(this);
        btn_xuexi = (TvButton) findViewById(R.id.btn_xuexi);
        btn_xuexi.setOnClickListener(this);
        btn_cunwu = (TvButton) findViewById(R.id.btn_cunwu);
        btn_cunwu.setOnClickListener(this);
        btn_jiceng = (TvButton) findViewById(R.id.btn_jiceng);
        btn_jiceng.setOnClickListener(this);

        btn_dianbo = (TvButton) findViewById(R.id.btn_dianbo);
        btn_dianbo.setOnClickListener(this);

        btn_bm = (TvButton) findViewById(R.id.btn_bm);
        btn_bm.setOnClickListener(this);

        rrmain = (RelativeLayout) findViewById(R.id.rrmain);

        btn_set = (TvButton) findViewById(R.id.btn_set);
        btn_set.setOnClickListener(this);

        txthuanying = (TextView) findViewById(R.id.txthuanying);

        txthuanying.setText("欢迎您：" + sp.getString("name", ""));

        txtshow = (AlwaysMarqueeTextView) findViewById(R.id.txtshow);


        txtdate = (TextView) findViewById(R.id.txtdate);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        txtdate.setText(str);

        rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);


    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_huiyi:
                String rtmp = sp.getString("rtmp", "") + sp.getString("code", "");
                intent.putExtra("rtmp", rtmp);
                intent.setClass(MainActivity.this, PlayActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_xuexi:
                intent.setClass(MainActivity.this, XueXiActivity.class);
                intent.putExtra("action", "xuexi");
                intent.putExtra("title", "学习动态");
                startActivity(intent);

                break;
            case R.id.btn_cunwu:
                intent.setClass(MainActivity.this, DJnewsActivity.class);
                intent.putExtra("action", "cunwu");
                intent.putExtra("title", "村务公开");
                startActivity(intent);
                break;
            case R.id.btn_jiceng:
                intent.setClass(MainActivity.this, DJnewsActivity.class);
                intent.putExtra("action", "jiceng");
                intent.putExtra("title", "基层动态");
                startActivity(intent);

                break;
            case R.id.btn_bm: //便民服务
                intent.setClass(MainActivity.this, BMActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_set:
                intent.setClass(MainActivity.this, SystemActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_dianbo:
                intent.setClass(MainActivity.this, VideosActivity.class);
                intent.putExtra("action", "cunwu");
                intent.putExtra("title", "村务公开");
                startActivity(intent);
                break;


        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;

    }


    private void addlog(String ip) {
        String code = sp.getString("code", "");
        AjaxParams params = new AjaxParams();
        params.put("code", code);
        params.put("typeid", "0");
        params.put("content", ip);
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