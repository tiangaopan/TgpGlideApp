package com.tgp.tgpglideapp.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.tgp.tgpglideapp.resource.Value;

/**
 * 内存缓存
 * @author 田高攀
 * @since 2020/4/3 11:02 AM
 */
public class MemoryCache extends LruCache<String, Value> {

    private boolean isShutDownRemove;

    /**
     * @param maxSize
     *         for caches that do not override {@link #sizeOf}, this is
     *         the maximum number of entries in the cache. For all other caches,
     *         this is the maximum sum of the sizes of the entries in this cache.
     */
    public MemoryCache(int maxSize) {
        super(maxSize);
    }

    private MemoryCacheCallback memoryCacheCallback;

    public void setMemoryCacheCallback(MemoryCacheCallback memoryCacheCallback) {
        this.memoryCacheCallback = memoryCacheCallback;
    }


    @Override
    protected int sizeOf(String key, Value value) {
        Bitmap bitmap = value.getmBitmap();
        return bitmap.getAllocationByteCount();
    }


    /**
     * 1.最少使用的元素会被移除
     * 2.重复的key
     */
    @Override
    protected void entryRemoved(boolean evicted, String key, Value oldValue, Value newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        //被动的移除
        if (memoryCacheCallback != null && !isShutDownRemove) {
            memoryCacheCallback.entryRemovedMemoryCache(key, oldValue);
        }
    }

    /**
     * 当进入到活动缓存中时需要手动移除
     */
    public Value shutDownRemove(String key) {
        isShutDownRemove = true;
        Value remove = remove(key);
        isShutDownRemove = false;
        return remove;
    }

}
