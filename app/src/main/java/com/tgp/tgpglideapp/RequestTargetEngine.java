package com.tgp.tgpglideapp;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.tgp.tgpglideapp.cache.ActiveCache;
import com.tgp.tgpglideapp.cache.MemoryCache;
import com.tgp.tgpglideapp.cache.MemoryCacheCallback;
import com.tgp.tgpglideapp.cache.disklrucache.DiskLruCacheImpl;
import com.tgp.tgpglideapp.fragment.LifecyclerCallback;
import com.tgp.tgpglideapp.load.LoadDataManager;
import com.tgp.tgpglideapp.load.ResponseListener;
import com.tgp.tgpglideapp.resource.Key;
import com.tgp.tgpglideapp.resource.Value;
import com.tgp.tgpglideapp.resource.ValueCallback;

/**
 * 真正加载资源以及处理生命周期
 * @author 田高攀
 * @since 2020/4/3 3:22 PM
 */
public class RequestTargetEngine implements LifecyclerCallback, ValueCallback, MemoryCacheCallback, ResponseListener {

    private static final String TAG = "RequestTargetEngine";
    private static final int MEMORY_MAX_SIZE = 1024 * 1024 * 60;

    @Override
    public void glideInitAction() {
        Log.i(TAG, "glideInitAction");
    }

    @Override
    public void glideStopAction() {
        Log.i(TAG, "glideStopAction");
    }

    @Override
    public void glideRecycleAction() {
        Log.i(TAG, "glideRecycleAction");
        if (activeCache != null) {
            //释放掉活动缓存
            activeCache.closeThread();
        }
    }


    private ActiveCache activeCache;
    private MemoryCache memoryCache;
    private DiskLruCacheImpl diskLruCache;
    private Context context;
    private String path;
    private String key;
    private ImageView imageView;

    public void into(ImageView imageView) {
        if (imageView == null) {
            throw new IllegalStateException("对象不能为空");
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("需要在主线程中操作");
        }
        this.imageView = imageView;
        //开始去加载资源 缓存-》网络 加载后放入缓存中
        Value value = cacheAction();
        if (value != null) {
            imageView.setImageBitmap(value.getmBitmap());
            //使用后-1
            value.nonUseAction();
        }
    }

    /**
     * 活动缓存-内存缓存-磁盘缓存-网络
     */
    private Value cacheAction() {
        //1判断活动缓存中是否有对象资源，有就返回，没有就继续查找
        Value value = activeCache.get(key);
        if (value != null) {
            Log.i(TAG, "在活动缓存中获取到数据");
            //使用了一次，需要+1
            value.useAction();
            return value;
        }
        //2内存缓存中查找
        value = memoryCache.get(key);
        if (value != null) {
            Log.i(TAG, "在内存缓存中获取到数据,需要将其移动到活动缓存中，并删除内存缓存中的数据");
            //移动到活动缓存中
            memoryCache.remove(key);
            activeCache.put(key, value);
            value.useAction();
            return value;
        }
        //3从磁盘缓存中查找，找到就放到活动和内存缓存中，
        value = diskLruCache.get(key);
        if (value != null) {
            activeCache.put(key, value);
            value.useAction();
            //            memoryCache.put(key, value);
            return value;
        }
        //4 加载网络/SD卡图片
        value = new LoadDataManager().loadResource(context, path, this);
        if (value != null) {
            return value;
        }

        return null;

    }

    /**
     * RequestManager传递的值
     */
    public void loadValueInitAction(Context context, String path) {
        this.context = context;
        this.path = path;
        key = new Key(path).getKey();

    }

    public RequestTargetEngine() {
        if (activeCache == null) {
            activeCache = new ActiveCache(this);
        }
        if (memoryCache == null) {
            memoryCache = new MemoryCache(MEMORY_MAX_SIZE);
            memoryCache.setMemoryCacheCallback(this);
        }
        if (diskLruCache == null) {
            diskLruCache = new DiskLruCacheImpl();
        }
    }

    /**
     * 活动缓存中数据不再使用时触发
     * @param key
     * @param value
     */
    @Override
    public void valueNonUseListener(String key, Value value) {
        //说明活动缓存中不再使用了，需要放到内存缓存中
        if (!TextUtils.isEmpty(key) && value != null) {
            memoryCache.put(key, value);
        }
    }

    /**
     * 内存缓存中被移除会触发这里
     * @param key
     * @param value
     */
    @Override
    public void entryRemovedMemoryCache(String key, Value value) {
        //
    }

    /**
     * 外部资源的成功
     * @param value
     */
    @Override
    public void responseSuccess(Value value) {
        if (value != null) {
            imageView.setImageBitmap(value.getmBitmap());
            saveCache(key, value);
        }
    }

    /**
     * 外部资源的失败
     * @param e
     */
    @Override
    public void responseFail(Exception e) {
        Log.i(TAG, "加载外部资源发生了异常" + e.getMessage());
    }

    /**
     * 加载外部资源后保存到缓存中
     * @param key
     * @param value
     */
    private void saveCache(String key, Value value) {
        value.setKey(key);
        if (diskLruCache != null) {
            //保存到磁盘缓存中
            diskLruCache.put(key, value);
        }
    }
}
