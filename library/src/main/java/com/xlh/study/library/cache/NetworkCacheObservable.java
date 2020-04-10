package com.xlh.study.library.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.xlh.study.library.bean.Image;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.xlh.study.library.helper.Constants.TAG;


/**
 * @author: Watler Xu
 * time:2020/4/10
 * description:
 * version:0.0.1
 */
public class NetworkCacheObservable extends CacheObservable {

    @Override
    public Image getDataFromCache(String url) {

        Bitmap bitmap = downloadImage(url);
        if (bitmap != null) {
            Log.e(TAG,"getDataFromCache--->Network");
            return new Image(url, bitmap);
        }
        return null;
    }

    @Override
    public void putDataToCache(Image image) {

    }

    private Bitmap downloadImage(String urlString) {
        Bitmap bitmap = null;
        InputStream inputStream = null;

        HttpURLConnection urlConnection = null;
        URL url = null;
        try {
            url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(6000);
            urlConnection.setUseCaches(false);
            urlConnection.setInstanceFollowRedirects(true);
            urlConnection.setReadTimeout(3000);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            urlConnection.connect();

            OutputStream out = urlConnection.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(out);
            bos.flush();
            out.close();
            bos.close();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception e) {

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            urlConnection.disconnect();
        }
        return bitmap;
    }

}
