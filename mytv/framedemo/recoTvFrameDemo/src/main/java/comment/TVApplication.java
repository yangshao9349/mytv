package comment;

import java.io.File;
import java.util.LinkedList;
import java.util.List;



import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class TVApplication extends Application {

    private Context mContext;
    private static TVApplication mTVApplication;
    private static List<Activity> activityList = new LinkedList<Activity>();

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        mContext = this;

    }

    /**
     * ����ģʽ�л�ȡΨһ��Applicationʵ��
     *
     * @return
     */
    public static TVApplication getInstance() {
        if (null == mTVApplication) {
            mTVApplication = new TVApplication();
        }
        return mTVApplication;
    }

    /**
     * ���Activity��������,�Ӷ�ʵ������ϵͳ����ȫ�˳�����
     * �˳��ǵ���exit();
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    /**
     * ��������Activity��finish
     */
    public static void finishAll() {
        for (Activity activity : activityList) {
            activity.finish();
        }
    }

    /**
     * �˳�����
     **/
    public static void exitApplication() {
        finishAll();
        System.exit(0);
    }


}
