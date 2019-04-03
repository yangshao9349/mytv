package broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2017-08-08.
 */

public class ReceiverMsg extends BroadcastReceiver {

    //定义两个自定义的消息
    public static final String MSG = "com.yang.msg";
    public static final String kaihui = "com.yang.kaihui";
    public static final String endkaihui = "com.yang.endkaihui";

    /**
     * 接收消息处理
     */
    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // TODO Auto-generated method stub
        if (arg1.getAction().equals(MSG)) {
            System.out.println(MSG);
        }
    }


}
