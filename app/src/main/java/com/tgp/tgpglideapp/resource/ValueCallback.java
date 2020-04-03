package com.tgp.tgpglideapp.resource;

/**
 * 当图片不再使用时，回调告诉外界
 * @author 田高攀
 * @since 2020/4/2 5:18 PM
 */
public interface ValueCallback {

    void valueNonUseListener(String key, Value value);
}
