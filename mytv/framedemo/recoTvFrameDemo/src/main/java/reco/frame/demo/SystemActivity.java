package reco.frame.demo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import comment.TVApplication;
import reco.frame.demo.entity.Config;
import reco.frame.demo.entity.SysApplication;

/**
 * Created by Administrator on 2017/5/22.
 */

public class SystemActivity extends BaseActivity implements View.OnClickListener {
    private Button btnexit, btn_zhuxiao, btn_myinfo, btn_about;
    private TextView txtcode, txtversion, txtzuname, txtipshow, txtport2;
    private TextView txtdanweiname;
    private SharedPreferences sp;
    private Dialog dialog;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);

        mContext = this;
        init();


    }

    private void init() {

        txtcode = (TextView) findViewById(R.id.txtcode);
        txtdanweiname = (TextView) findViewById(R.id.txtdanweiname);
        btnexit = (Button) findViewById(R.id.btnexit);
        btnexit.setOnClickListener(this);

        sp = getSharedPreferences("db", Context.MODE_PRIVATE);

        btn_zhuxiao = (Button) findViewById(R.id.btn_zhuxiao);
        btn_zhuxiao.setOnClickListener(this);

        txtversion = (TextView) findViewById(R.id.txtversion);
        txtversion.setText(getVersion());
        txtzuname = (TextView) findViewById(R.id.txtzuname);
        txtipshow = (TextView) findViewById(R.id.txtipshow);
        txtport2 = (TextView) findViewById(R.id.txtport2);

        String code = sp.getString("code", "");
        String name = sp.getString("name", "");
        String ip = sp.getString("ip", "");
        String port1 = sp.getString("port1", "");
        String port2 = sp.getString("port2", "");

        txtipshow.setText("http://" + ip + ":" + port1);
        txtport2.setText(port2);


        txtcode.setText(code);
        txtdanweiname.setText(name);
        txtzuname.setText(sp.getString("fenzuname", ""));

        btn_myinfo = (Button) findViewById(R.id.btn_myinfo);
        btn_about = (Button) findViewById(R.id.btn_about);

        btn_myinfo.setOnClickListener(this);
        btn_about.setOnClickListener(this);

    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    private void exit() {
        SharedPreferences userSettings = getSharedPreferences("db", Context.MODE_PRIVATE); //私有数据
        SharedPreferences.Editor editor = userSettings.edit();
        editor.clear();
        editor.commit();
        SysApplication.getInstance().exit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnexit:
                AlertDialog isExit = new AlertDialog.Builder(this).create();
                // 设置对话框标题
                isExit.setTitle("系统提示");
                // 设置对话框消息
                isExit.setMessage("确定要退出吗");
                // 添加选择按钮并注册监听
                isExit.setButton("确定", listener);
                isExit.setButton2("取消", listener);
                // 显示对话框
                isExit.show();
                break;
            case R.id.btn_myinfo:


                String content = "";
                content += " 型号 : " + android.os.Build.MODEL + "\n";
                content += " SDK版本 :" + android.os.Build.VERSION.SDK + "\n";
                content += " 安卓版本： " + android.os.Build.VERSION.RELEASE + "\n";
                content += " 内存总大小:" + getRomTotalSize() + "\n 可用内存:" + getRomAvailableSize() + "\n";
                content += " 存储空间：" + getAllSize();

                TextView txt = new TextView(mContext);
                txt.setText(content);


                dialog = new AlertDialog.Builder(this).setTitle("关于本机").setIcon(
                        android.R.drawable.ic_dialog_info).setView(
                        txt).setPositiveButton("确定", null).show();
                break;
            case R.id.btn_about:


                String ss = " 版权所有  山东德成智能技术有限公司  \n 电话：0537-2082222";
                TextView txt2 = new TextView(mContext);
                txt2.setText(ss);


                dialog = new AlertDialog.Builder(this).setTitle("关于我们").setIcon(
                        android.R.drawable.ic_dialog_info).setView(
                        txt2).setPositiveButton("确定", null).show();
                break;
            case R.id.btn_zhuxiao:
                exit();
                break;
        }
    }

    public String getAllSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(mContext, blockSize * totalBlocks);

    }

    /**
     * 获得机身内存总大小
     *
     * @return
     */
    private String getRomTotalSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(mContext, blockSize * totalBlocks);
    }

    /**
     * 获得机身可用内存
     *
     * @return
     */
    private String getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(mContext, blockSize * availableBlocks);
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    TVApplication.getInstance().exitApplication();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };

}
