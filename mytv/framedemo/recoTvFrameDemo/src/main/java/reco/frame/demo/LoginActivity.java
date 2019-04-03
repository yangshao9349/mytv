package reco.frame.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import reco.frame.demo.entity.Config;
import reco.frame.demo.entity.MySerialize;
import reco.frame.demo.entity.TianQi;
import reco.frame.tv.TvHttp;
import reco.frame.tv.http.AjaxCallBack;
import reco.frame.tv.http.AjaxParams;

/**
 * Created by Administrator on 2017/5/25.
 */

public class LoginActivity extends Activity {
    private Button btnlogin;
    private TvHttp tvHttp;
    private EditText etcode;
    private SharedPreferences sp;
    private EditText etip;
    private EditText etport1;


    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        init();
    }

    private void init() {
        tvHttp = new TvHttp(getApplicationContext());
        btnlogin = (Button) findViewById(R.id.btnlogin);
        etcode = (EditText) findViewById(R.id.etcode);
        sp = getSharedPreferences("db", Context.MODE_PRIVATE);
        etip = (EditText) findViewById(R.id.etip);
        etport1 = (EditText) findViewById(R.id.etport1);



        String ip = sp.getString("ip", "");
        String port1 = sp.getString("port1", "");
        String port2 = sp.getString("port2", "");
        etip.setText(ip);
        etport1.setText(port1);






        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = etcode.getText().toString().trim();
                String ip = etip.getText().toString();
                String port1 = etport1.getText().toString();

                if (ip.equals("")) {
                    Toast.makeText(context, "请输入IP", Toast.LENGTH_SHORT).show();
                } else if (port1.equals("")) {
                    Toast.makeText(context, "请输入WEB端口", Toast.LENGTH_SHORT).show();
                }  else if (code.equals("")) {
                    Toast.makeText(context, "请输入单位代码", Toast.LENGTH_SHORT).show();
                } else {
                    Config.init(ip, port1);
                    login(code);

                }

            }
        });

    }

    private void login(String code) {
        AjaxParams params = new AjaxParams();
        params.put("code", code);
        params.put("mac", getNewMac());
        tvHttp.post(Config.login, params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object t) {
                try {
                    String json = t.toString();
                    JSONObject ss = new JSONObject(json);
                    String state = ss.getString("state");
                    String message = ss.getString("message");
                    if (state.equals("400")) {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else if (state.equals("200")) {
                        JSONObject obj = ss.getJSONObject("obj");
                        String code = obj.getString("code");
                        String name = obj.getString("name");
                        String zuid = obj.getString("zuid");
                        String fenzuname = obj.getString("fenzuname");

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("code", code);
                        editor.putString("zuid", zuid);
                        editor.putString("name", name);
                        editor.putString("fenzuname", fenzuname);

                        editor.commit();

                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "企业代码不正确", Toast.LENGTH_SHORT).show();
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

    private static String getNewMac() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
