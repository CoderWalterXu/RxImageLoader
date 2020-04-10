package com.xlh.study.library.cache;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.xlh.study.library.bean.Image;

import static com.xlh.study.library.helper.Constants.TAG;


/**
 * @author: Watler Xu
 * time:2020/4/10
 * description:
 * version:0.0.1
 */
public class MemoryCacheObservable extends CacheObservable {

    int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);//kb
    int cacheSize = maxMemory / 8;

    LruCache<String, Bitmap> mLruCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight() / 1024;
        }
    };

    @Override
    public Image getDataFromCache(String url) {

        Bitmap bitmap = mLruCache.get(url);
        if (bitmap != null) {
            Log.e(TAG,"getDataFromCache--->Memory");
            return new Image(url, bitmap);
        }

        return null;
    }

    @Override
    public void putDataToCache(Image image) {
        mLruCache.put(image.getUrl(), image.getBitmap());
    }
}
