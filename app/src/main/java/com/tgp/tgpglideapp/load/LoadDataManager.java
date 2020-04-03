package com.tgp.tgpglideapp.load;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.SyncStateContract;

import com.tgp.tgpglideapp.resource.Value;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 类的大体描述放在这里
 * @author 田高攀
 * @since 2020/4/3 5:06 PM
 */
public class LoadDataManager implements ILoadData, Runnable {

    private String path;
    private ResponseListener responseListener;
    private Context context;

    @Override
    public Value loadResource(Context context, String path, ResponseListener listener) {
        this.path = path;
        this.responseListener = listener;
        this.context = context;

        //加载网络图片/SD卡本地图片
        Uri uri = Uri.parse(path);
        if ("HTTP".equalsIgnoreCase(uri.getScheme()) || "HTTPS".equalsIgnoreCase(uri.getScheme())) {
            new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, (BlockingQueue<Runnable>) new SyncStateContract()).execute(this);
        }
        //Sd本地图片 找到图片返回

        return null;
    }


    @Override
    public void run() {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(path);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(5000);
            final int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {
                inputStream = httpURLConnection.getInputStream();
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                //需要切换回主线程
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Value value = Value.getInstance();
                        value.setmBitmap(bitmap);
                        //回调成功
                        responseListener.responseSuccess(value);
                    }
                });

            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        responseListener.responseFail(new Exception("请求失败，请求码" + responseCode));
                    }
                });
            }
        } catch (final Exception e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    responseListener.responseFail(e);
                }
            });

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }
}
