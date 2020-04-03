package com.tgp.tgpglideapp;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

/**
 * 类的大体描述放在这里
 * @author 田高攀
 * @since 2020/4/3 2:19 PM
 */
public class Glide {

    RequestManagerRetriever retriver;

    public Glide(RequestManagerRetriever retriver) {
        this.retriver = retriver;
    }

    public static RequestManager with(FragmentActivity fragmentActivity) {
        return getRetriever(fragmentActivity).get(fragmentActivity);
    }

    public static RequestManager with(Activity activity) {
        return getRetriever(activity).get(activity);
    }

    public static RequestManager with(Context context) {
        return getRetriever(context).get(context);
    }

    /**
     * 生成RequestManager
     * @return
     */
    public static RequestManagerRetriever getRetriever(Context context) {
        return Glide.get(context).getRetriever();

    }

    public static Glide get(Context context) {
        return new GlideBuilder().build();
    }

    public RequestManagerRetriever getRetriever() {
        return retriver;
    }

}
