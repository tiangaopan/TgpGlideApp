package com.tgp.tgpglideapp;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

/**
 * 管理RequestManager
 * @author 田高攀
 * @since 2020/4/3 2:24 PM
 */
class RequestManagerRetriever {

    public RequestManager get(FragmentActivity fragmentActivity) {
        return new RequestManager(fragmentActivity);
    }

    public RequestManager get(Activity activity) {
        return new RequestManager(activity);
    }

    public RequestManager get(Context context) {
        return new RequestManager(context);
    }
}
