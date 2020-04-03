package com.tgp.tgpglideapp.load;

import com.tgp.tgpglideapp.resource.Value;

/**
 * 加载外部资源成功失败的回调
 * @author 田高攀
 * @since 2020/4/3 5:05 PM
 */
public interface ResponseListener {

    void responseSuccess(Value value);
    void responseFail(Exception e);
}
