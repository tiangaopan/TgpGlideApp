package com.tgp.tgpglideapp.cache;

import com.tgp.tgpglideapp.resource.Value;

/**
 * 内存缓存中元素被移除
 * @author 田高攀
 * @since 2020/4/3 11:20 AM
 */
public interface MemoryCacheCallback {
    /**
     * 移除内存缓存中的Key
     * @param key
     * @param value
     */
    void entryRemovedMemoryCache(String key, Value value);

}
