package com.an.better.netease.cloud.music.api;

import android.net.Uri;

import com.an.better.netease.cloud.music.gank.child.everyday.model.ting.TingBlock;
import com.an.better.netease.cloud.music.gank.child.everyday.model.GanKDayBlock;
import com.trident.dating.libcommon.IRequest;
import com.trident.dating.libcommon.listener.ResponseListener;
import com.trident.dating.libnetwork.okhttp.OKHttpRequest;

/**
 *
 * Created by android_ls on 2018/2/11.
 */

public class Apis {

    /**
     * 首页轮播图
     * @param listener
     */
    public static void getBanner(ResponseListener<TingBlock> listener) {
        String url = Uri.withAppendedPath(Uri.parse(ApiUrls.TING_BASE_URL),
                ApiUrls.HOME_BANNER_URL).buildUpon().toString();
        new OKHttpRequest<>(url, listener, TingBlock.class).enqueue();
    }

    /**
     * 获取每天的干货
     * @param url
     * @param listener
     * @return
     */
    public static IRequest getGankDay(String url, final ResponseListener<GanKDayBlock> listener) {
        return new OKHttpRequest<>(url, listener, GanKDayBlock.class).enqueue();
    }




}