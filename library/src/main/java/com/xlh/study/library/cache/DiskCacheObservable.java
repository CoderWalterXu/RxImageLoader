package com.xlh.study.library.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.xlh.study.library.bean.Image;
import com.xlh.study.library.disk.DiskCacheUtil;
import com.xlh.study.library.disk.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

import static com.xlh.study.library.helper.Constants.TAG;


/**
 * @author: Watler Xu
 * time:2020/4/10
 * description:
 * version:0.0.1
 */
public class DiskCacheObservable extends CacheObservable {

    private DiskLruCache mDiskLruCache;

    private Context mContext;
    // DiskLruCache中对于图片的最大缓存值
    private int maxSize = 20 * 1024 * 1024;

    public DiskCacheObservable(Context mContext) {
        this.mContext = mContext;
        initDiskLruCache();
    }

    @Override
    public Image getDataFromCache(String url) {

        Bitmap bitmap = getDataFromDiskLruCache(url);
        if (bitmap != null) {
            Log.e(TAG,"getDataFromCache--->Disk");
            return new Image(url, bitmap);
        }

        return null;
    }

    @Override
    public void putDataToCache(final Image image) {
        Observable.create(new ObservableOnSubscribe<Image>() {
            @Override
            public void subscribe(ObservableEmitter<Image> emitter) throws Exception {
                putDataToDiskLruCache(image);
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private void putDataToDiskLruCache(Image image) {
        try {
            // 获取将要缓存图片的对应唯一key值
            String key = DiskCacheUtil.getMd5String(image.getUrl());
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);

            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                boolean isSuccessful = download(image.getUrl(), outputStream);

                if (isSuccessful) {
                    editor.commit();
                } else {
                    editor.abort();
                }
                mDiskLruCache.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean download(String urlString, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;

        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void initDiskLruCache() {
        try {
            File cacheDir = DiskCacheUtil.getDiskCacheDir(this.mContext, "image_cache");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            int versionCode = DiskCacheUtil.getAppVersionCode(mContext);
            mDiskLruCache = DiskLruCache.open(cacheDir, versionCode, 1, maxSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getDataFromDiskLruCache(String url) {
        FileDescriptor fileDescriptor = null;
        FileInputStream fileInputStream = null;
        DiskLruCache.Snapshot snapshot = null;

        try {
            // 生成图片URL对应的key
            final String key = DiskCacheUtil.getMd5String(url);
            // 查找key对应的缓存
            snapshot = mDiskLruCache.get(key);

            if (snapshot != null) {
                fileInputStream = (FileInputStream) snapshot.getInputStream(0);
                fileDescriptor = fileInputStream.getFD();
            }
            // 将缓存数据解析成Bitmap对象
            Bitmap bitmap = null;
            if (fileDescriptor != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            }
            return bitmap;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileDescriptor == null && fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
