package net.iclassmate.bxyd.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import net.iclassmate.bxyd.bean.message.SpaceMessage;

import io.rong.imlib.AnnotationNotFoundException;
import io.rong.imlib.RongIMClient;


/**
 * Created by xyd on 2016/5/12.
 */
public class MyApplication extends Application {
    // 获取到主线程的上下文
    private static MyApplication mContext;
    // 获取到主线程的hander;
    private static Handler mMainThreadHander;
    // 获取到主线程的looper
    private static Looper mMainThreadLooper;
    // 获取到主线程
    private static Thread mMainThead;
    // 获取到主线程的id
    private static int mMainTheadId;

    @Override
    public void onCreate() {
        super.onCreate();

        ZXingLibrary.initDisplayOpinion(this);

        mContext = this;
        mMainThreadHander = new Handler();
        mMainThreadLooper = getMainLooper();
        mMainThead = Thread.currentThread();
        mMainTheadId = android.os.Process.myTid();
        /**
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIMClient 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
//        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
//                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {
        try {
            RongIMClient.init(this);
            RongIMClient.registerMessageType(SpaceMessage.class);
        } catch (AnnotationNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // }

    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }


    public static MyApplication getApplication() {
        return mContext;
    }

    public static Handler getMainThreadHandler() {
        return mMainThreadHander;
    }

    public static Looper getMainThreadLooper() {
        return mMainThreadLooper;
    }

    public static Thread getMainThread() {
        return mMainThead;
    }

    public static int getMainThreadId() {
        return mMainTheadId;
    }

}
