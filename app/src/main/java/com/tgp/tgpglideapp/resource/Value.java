package com.tgp.tgpglideapp.resource;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Bitmap的封装
 * @author 田高攀
 * @since 2020/4/2 5:04 PM
 */
public class Value {

    private static final String TAG = "Value";

    private Bitmap mBitmap;

    private ValueCallback callback;

    /**
     * 使用计数
     */
    private int count;

    private String key;

    private static Value value;

    public static Value getInstance() {
        if (value == null) {
            synchronized (Value.class) {
                if (value == null) {
                    value = new Value();
                }
            }
        }
        return value;
    }

    private Value() {

    }

    /**
     * 使用一次就进行 +1
     */
    public void useAction() {
        if (mBitmap == null) {
            Log.i(TAG, "图片资源为null");
            return;
        }
        if (mBitmap.isRecycled()) {
            Log.i(TAG, "图片资源已经被回收");
            return;
        }
        ++count;
    }

    /**
     * 使用一次后 -1
     */
    public void nonUseAction() {
        count--;
        if (count <= 0 && callback != null) {
            //不再使用了,需要告诉外界不再使用
            callback.valueNonUseListener(key, this);
        }
    }

    /**
     * 释放资源
     */
    public void recycleBitmap() {
        if (count > 0) {
            Log.i(TAG, "图片资源仍然在使用，不能将其释放");
            return;
        }
        //表示被回收了，不用再回收
        if (mBitmap.isRecycled()) {
            Log.i(TAG, "图片资源已经被释放");
            return;
        }
        mBitmap.recycle();
        value = null;
        System.gc();
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public ValueCallback getCallback() {
        return callback;
    }

    public void setCallback(ValueCallback callback) {
        this.callback = callback;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
