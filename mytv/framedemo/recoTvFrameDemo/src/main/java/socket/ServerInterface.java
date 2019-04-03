package socket;

import model.ServerBean;

/**
 * Created by Administrator on 2017-08-07.
 */

public interface ServerInterface {
    void onMessageListen(ServerBean chatBean);

    void onConnect(boolean res);

    void onError();

    void onDisconnect();
}
