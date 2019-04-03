package socket;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import model.ServerBean;
import okhttp3.Call;
import reco.frame.demo.LocationApplication;
import reco.frame.demo.MainActivity;

/**
 * 直播间业务逻辑处理
 */
public class MyServer {


    private Socket mSocket;
    private Context context;
    private ServerInterface server;


    public MyServer(ServerInterface server, Context context) throws URISyntaxException {

        this.context = context;
        this.server = server;

        mSocket =  ((MainActivity)context).getSocket();


    }

    //服务器连接关闭监听
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            server.onDisconnect();
            Log.d("msg", "socket断开连接");
        }
    };
    //服务器连接失败监听
    private Emitter.Listener onError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            server.onError();
            Log.d("msg", "socket连接Error");
        }
    };
    //服务器消息监听
    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                String res = args[0].toString();

                JSONObject resJson = new JSONObject(res);
                ServerBean bean = new ServerBean();
                bean.type = resJson.getString("type");
                bean.content = resJson.getString("content");
                bean.zuid = resJson.getInt("zuid");
                bean.tvid = resJson.getInt("tvid");
                server.onMessageListen(bean);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    private void onMessage(JSONObject contentJson) throws JSONException {
        mSocket.emit("conn", contentJson);

    }

    /**
     * @param handler 定时发送心跳包,在连接成功后调用
     */
    public void heartbeat(final Handler handler) {
        if (handler != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //TLog.log("心跳包发送....");
                    if (mSocket == null) return;
                    //TLog.log("心跳包发送....");
                    mSocket.emit("tongzhi", "heartbeat");
                    handler.postDelayed(this, 4000);
                }
            }, 4000);

        }
    }

    private Emitter.Listener onConn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args[0].toString().equals("ok")) {
                server.onConnect(true);

            } else {
                server.onConnect(false);
            }
        }
    };

    public void init(String code) {
        mSocket.connect();
        mSocket.emit("conn", code);
        mSocket.on("conn", onConn);
        mSocket.on("broadcast", onMessage);
        mSocket.on(mSocket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(mSocket.EVENT_ERROR, onError);

    }


    //释放资源
    public void close() {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off();
            mSocket.close();
            mSocket = null;
        }

    }

}
