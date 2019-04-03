package reco.frame.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import reco.frame.demo.entity.Config;


/**
 * Created by Administrator on 2017/5/22.
 */

public class AppStart extends Activity {
    private SharedPreferences sp;
    private LinearLayout app_start_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = View.inflate(this, R.layout.app_start, null);
        setContentView(view);
        app_start_view = (LinearLayout) findViewById(R.id.app_start_view);
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/home.jpg", null);
            if (bitmap != null) {
                app_start_view.setBackground(bitmap2Drawable(bitmap));
            }
        } catch (Exception e) {

        }


        // 渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
        aa.setDuration(3000);
        view.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                redirectTo();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
    }

    Drawable bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    private void redirectTo() {
        sp = getSharedPreferences("db", Context.MODE_PRIVATE);
        String code = sp.getString("code", "");
        Intent intent = null;
        if (code.equals("")) {
            intent = new Intent(this, LoginActivity.class);
        } else {
            String ip = sp.getString("ip", "");
            String port1 = sp.getString("port1", "");
            Config.init(ip, port1);


            intent = new Intent(this, MainActivity.class);
        }

        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {

        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        super.onResume();
    }
}
