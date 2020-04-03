package com.tgp.tgpglideapp.cache.disklrucache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import com.tgp.tgpglideapp.resource.Value;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 磁盘缓存的目录
 * @author 田高攀
 * @since 2020/4/3 11:59 AM
 */
public class DiskLruCacheImpl {
    private static final String TAG = "DiskLruCacheImpl";
    private static final String DISK_LRU_CACHE_DIR = "disk_lru_cache_dir";
    private DiskLruCache diskLruCache;
    /**
     * 做标记，版本号变更的话之前的缓存失效
     */
    private static final int APP_VERSION = 999;
    private static final int VALUE_COUNT = 1;
    private static final long MAX_SIZE = 1024 * 1024 * 10;

    public DiskLruCacheImpl() {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + DISK_LRU_CACHE_DIR);
        try {
            diskLruCache = DiskLruCache.open(file, APP_VERSION, VALUE_COUNT, MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加
     */
    public void put(String key, Value value) {
        DiskLruCache.Editor edit = null;
        OutputStream outputStream = null;
        try {
            edit = diskLruCache.edit(key);
            //index 不能大于VALUE_COUNT
            outputStream = edit.newOutputStream(0);
            Bitmap bitmap = value.getmBitmap();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                edit.abort();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                edit.commit();
                diskLruCache.flush();
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取
     */
    public Value get(String key) {
        if (TextUtils.isEmpty(key)) {
            throw new IllegalStateException(TAG + " : key不能为空");
        }
        InputStream inputStream = null;
        Value value = Value.getInstance();
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
            if (snapshot != null) {
                inputStream = snapshot.getInputStream(0);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                value.setmBitmap(bitmap);
                value.setKey(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }
}
