package com.tgp.tgpglideapp.load;

import android.content.Context;

import com.tgp.tgpglideapp.resource.Value;

/**
 * 加载外部资源的标准
 * @author 田高攀
 * @since 2020/4/3 5:04 PM
 */
public interface ILoadData {
    Value loadResource(Context context, String path, ResponseListener listener);
}
