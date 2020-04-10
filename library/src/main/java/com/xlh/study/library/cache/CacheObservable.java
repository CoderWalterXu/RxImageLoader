package com.xlh.study.library.cache;

import android.util.Log;

import com.xlh.study.library.bean.Image;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.xlh.study.library.helper.Constants.TAG;


/**
 * @author: Watler Xu
 * time:2020/4/10
 * description:
 * version:0.0.1
 */
public abstract class CacheObservable {

    public Observable<Image> getImage(final String url) {
        return Observable.create(new ObservableOnSubscribe<Image>() {
            @Override
            public void subscribe(ObservableEmitter<Image> emitter) throws Exception {
                Image image = getDataFromCache(url);

                if (image != null) {
                    Log.e(TAG, "image != null");
                    emitter.onNext(image);
                    emitter.onComplete();
                } else {
                    Log.e(TAG, "image = null");
                    emitter.onComplete();
                }

            }
        }).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread());
    }

    public abstract Image getDataFromCache(String url);

    public abstract void putDataToCache(Image image);

}
