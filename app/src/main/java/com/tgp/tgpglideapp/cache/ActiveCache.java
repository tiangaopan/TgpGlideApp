package com.tgp.tgpglideapp.cache;

import android.text.TextUtils;

import com.tgp.tgpglideapp.resource.Value;
import com.tgp.tgpglideapp.resource.ValueCallback;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 活动缓存-正在被使用的资源
 * 因为是GC回收机制，所以需要处理成弱引用
 * 这里只有添加移除和获取三种操作
 * @author 田高攀
 * @since 2020/4/2 5:32 PM
 */
public class ActiveCache {

    private Map<String, WeakReference<Value>> mMap = new HashMap<>();
    /**
     * 为了监听弱引用是否被回收，CustomWeakReference中需要ReferenceQueue，所以我们需要获取这个queue
     */
    private ReferenceQueue<Value> mQueue;
    private boolean isCloseThread;
    private Thread mThread;
    private boolean isShutDownRemove;
    private ValueCallback valueCallback;

    public ActiveCache(ValueCallback valueCallback) {
        this.valueCallback = valueCallback;
    }

    public void put(String key, Value value) {
        if (TextUtils.isEmpty(key) || value == null) {
            throw new IllegalStateException("传递的数据为空");
        }
        //绑定value的监听
        value.setCallback(valueCallback);
        mMap.put(key, new CustomWeakReference(value, getQueue(), key));
    }

    /**
     * 获取资源
     * @param key
     * @return
     */
    public Value get(String key) {
        WeakReference<Value> valueWeakReference = mMap.get(key);
        if (valueWeakReference != null) {
            return valueWeakReference.get();
        }
        return null;
    }

    /**
     * 手动移除
     * @param key
     * @return
     */
    public Value remove(String key) {
        isShutDownRemove = true;
        WeakReference<Value> valueWeakReference = mMap.remove(key);
        isShutDownRemove = false;
        if (valueWeakReference != null) {
            return valueWeakReference.get();
        }
        return null;
    }

    /**
     * 释放关闭线程
     */
    public void closeThread() {
        isCloseThread = true;
        if (mThread != null) {
            mThread.interrupt();
            try {
                //线程稳定安全的停止
                mThread.join(TimeUnit.SECONDS.toMillis(5));
                //如果还是活跃状态
                if (mThread.isAlive()) {
                    throw new IllegalStateException("线程关闭失败了");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 为了监听弱引用被回收,被动移除的
     */
    private ReferenceQueue<Value> getQueue() {
        if (mQueue == null) {
            mQueue = new ReferenceQueue<>();
            //监听这个弱引用，是否被回收了
            mThread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (!isCloseThread) {
                        try {
                            //如果被回收了，就会执行到这个方法
                            // mQueue.remove() 是阻塞式的方法，如果没有移除不会往下执行，如果移除了，下面的才会执行
                            Reference<? extends Value> remove = mQueue.remove();
                            CustomWeakReference weakReference = (CustomWeakReference) remove;
                            //移除容器中存储的,需要区分是主动移除还是被动移除的
                            if (mMap.isEmpty() && !isShutDownRemove) {
                                mMap.remove(weakReference.key);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            };
            mThread.start();
        }
        return mQueue;
    }

    /**
     * 需要监听弱引用
     */
    public static class CustomWeakReference extends WeakReference<Value> {

        private String key;

        /**
         * 该方法能知道是否被回收掉
         */
        public CustomWeakReference(Value referent, ReferenceQueue<? super Value> q, String key) {
            super(referent, q);
            this.key = key;
        }
    }

}
