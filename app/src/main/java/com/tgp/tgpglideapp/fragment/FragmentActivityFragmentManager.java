package com.tgp.tgpglideapp.fragment;


import androidx.fragment.app.Fragment;

/**
 * 类的大体描述放在这里
 * @author 田高攀
 * @since 2020/4/3 2:41 PM
 */
public class FragmentActivityFragmentManager extends Fragment {

    public FragmentActivityFragmentManager() {

    }

    private LifecyclerCallback lifecyclerCallback;
    public FragmentActivityFragmentManager(LifecyclerCallback callback) {
        lifecyclerCallback = callback;
    }


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
