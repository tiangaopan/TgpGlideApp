package com.tgp.tgpglideapp.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;

/**
 * 类的大体描述放在这里
 * @author 田高攀
 * @since 2020/4/3 2:42 PM
 */
public class ActivityFragmentManager extends Fragment {

    private LifecyclerCallback lifecyclerCallback;

    @SuppressLint("ValidFragment")
    public ActivityFragmentManager(LifecyclerCallback lifecyclerCallback) {
        this.lifecyclerCallback = lifecyclerCallback;
    }

    public ActivityFragmentManager(){}

    @Override
    public void onStart() {
        super.onStart();
        if (lifecyclerCallback != null) {
            lifecyclerCallback.glideInitAction();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (lifecyclerCallback != null) {
            lifecyclerCallback.glideStopAction();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lifecyclerCallback != null) {
            lifecyclerCallback.glideRecycleAction();
        }
    }
}
