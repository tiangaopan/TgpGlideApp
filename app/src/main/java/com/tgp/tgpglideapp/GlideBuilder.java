package com.tgp.tgpglideapp;

/**
 * 类的大体描述放在这里
 * @author 田高攀
 * @since 2020/4/3 2:27 PM
 */
class GlideBuilder {
    /**
     * 创建glide
     */
    public Glide build() {
        RequestManagerRetriever requestManagerRetriver = new RequestManagerRetriever();
        return new Glide(requestManagerRetriver);
    }
}
