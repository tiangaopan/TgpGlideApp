package com.tgp.tgpglideapp.resource;

import com.tgp.tgpglideapp.Utils;

/**
 * 对图片资源的唯一的描述
 * @author 田高攀
 * @since 2020/4/2 5:04 PM
 */
public class Key {
    private String key;

    /**
     * 需要对key进行加密操作，因为key是url，所以会存在特殊符号的可能
     * @param key
     */
    public Key(String key) {
        this.key = Utils.AES256Encode(key);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
