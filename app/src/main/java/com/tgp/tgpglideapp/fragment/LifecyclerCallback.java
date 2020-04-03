package com.tgp.tgpglideapp.fragment;

/**
 * 类的大体描述放在这里
 * @author 田高攀
 * @since 2020/4/3 3:17 PM
 */
public interface LifecyclerCallback {

    /**
     * 初始化
     */
    void glideInitAction();

    /**
     * 停止
     */
    void glideStopAction();

    /**
     * 回收
     */
    void glideRecycleAction();
}
