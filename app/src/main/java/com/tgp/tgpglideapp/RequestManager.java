package com.tgp.tgpglideapp;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.tgp.tgpglideapp.fragment.ActivityFragmentManager;
import com.tgp.tgpglideapp.fragment.FragmentActivityFragmentManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

/**
 * 在activity附加上fragment以此去管理生命周期
 * @author 田高攀
 * @since 2020/4/3 2:21 PM
 */
public class RequestManager {
    private static final String TAG = "RequestManager";
    private static final String FRAGMENT_ACTIVITY_NAME = "fragment_activity_name";
    private static final String ACTIVITY_NAME = "activity_name";
    private Context requestManagerContext;
    private static final int SEND_MSG = 999;
    private RequestTargetEngine requestTargetEngine;

    /**
     * 可以管理生命周期 --fragment -- FragmentActivityFragmentManager
     * @param fragmentActivity
     */
    public RequestManager(FragmentActivity fragmentActivity) {
        requestManagerContext = fragmentActivity;
        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
        Fragment fragmentByTag = supportFragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
        if (fragmentByTag == null) {
            //关联生命周期
            fragmentByTag = new FragmentActivityFragmentManager(requestTargetEngine);
            supportFragmentManager.beginTransaction()
                    .add(fragmentByTag, FRAGMENT_ACTIVITY_NAME)
                    .commitAllowingStateLoss();
        }
        //发送一个handler，因为add,这种操作会存在等待情况，需要确保能拿到
        mHandler.sendEmptyMessage(SEND_MSG);
    }

    {
        //构造代码块中初始化，避免每个都需要初始化
        requestTargetEngine = new RequestTargetEngine();
    }

    /**
     * 可以管理生命周期 --activity -- ActivityFragmentManager
     * @param activity
     */
    public RequestManager(Activity activity) {
        requestManagerContext = activity;

        android.app.FragmentManager fragmentManager = activity.getFragmentManager();
        android.app.Fragment fragmentByTag = fragmentManager.findFragmentByTag(ACTIVITY_NAME);
        if (fragmentByTag == null) {
            //关联生命周期
            fragmentByTag = new ActivityFragmentManager(requestTargetEngine);
            fragmentManager.beginTransaction().add(fragmentByTag, ACTIVITY_NAME).commitAllowingStateLoss();
        }

        //发送一个handler，因为add,这种操作会存在等待情况，需要确保能拿到
        mHandler.sendEmptyMessage(SEND_MSG);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    /**
     * 无法管理生命周期--application
     * @param context
     */
    public RequestManager(Context context) {
        requestManagerContext = context;
    }

    /**
     * 拿到图片路径
     * @param path
     * @return
     */
    public RequestTargetEngine load(String path) {
        //移除handler
        mHandler.removeMessages(SEND_MSG);
        requestTargetEngine.loadValueInitAction(requestManagerContext, path);
        return requestTargetEngine;
    }
}
