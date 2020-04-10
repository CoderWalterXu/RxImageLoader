package com.xlh.study.library;

import android.content.Context;
import android.widget.ImageView;

import com.xlh.study.library.bean.Image;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;


/**
 * @author: Watler Xu
 * time:2020/4/10
 * description:
 * version:0.0.1
 */
public class RxImageLoader {

    private String mUrl;

    private RequestCreator mRequestCreator;


    volatile static RxImageLoader instance;

    private RxImageLoader(Builder builder) {
        mRequestCreator = new RequestCreator(builder.context);
    }

    public static RxImageLoader with(Context context) {
        if (instance == null) {
            synchronized (RxImageLoader.class) {
                if (instance == null) {
                    instance = new Builder(context).build();
                }
            }
        }
        return instance;
    }

    public RxImageLoader load(String url) {
        this.mUrl = url;
        return instance;
    }

    public void into(final ImageView imageView) {

        rxJava2(imageView);

//        rxJava1(imageView);
    }

    /**
     * concat合并多个观察者
     * filter过滤非空
     * firstElement取第一个非空
     *
     * @param imageView
     */
    private void rxJava2(final ImageView imageView) {
        Observable.concat(
                mRequestCreator.getImageFromMemory(mUrl),
                mRequestCreator.getImageFromDisk(mUrl),
                mRequestCreator.getImageFromNetwork(mUrl))
                .filter(new Predicate<Image>() {
                    @Override
                    public boolean test(Image image) throws Exception {
                        return image != null;
                    }
                })
                .firstElement()
                .subscribe(new Consumer<Image>() {
                    @Override
                    public void accept(Image image) throws Exception {
                        if (image.getBitmap() != null) {
                            imageView.setImageBitmap(image.getBitmap());
                        }

                    }
                });
    }

    /**
     * concat合并多个观察者
     * first过滤非空,取第一个非空
     *
     * @param
     */
//    private void rxJava1(final ImageView imageView) {
//        Observable.concat(
//                mRequestCreator.getImageFromMemory(mUrl),
//                mRequestCreator.getImageFromDisk(mUrl),
//                mRequestCreator.getImageFromNetwork(mUrl))
//                .first(new Func1<Image, Boolean>() {
//                    @Override
//                    public Boolean call(Image image) {
//                        return image != null;
//                    }
//                })
//                .subscribe(new Observer<Image>() {
//
//                    @Override
//                    public void onNext(Image image) {
//                        imageView.setImageBitmap(image.getBitmap());
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Toast.makeText(mContext.getApplicationContext(), "加载失败", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onCompleted() {
//
//                    }
//                });
//    }

    public static class Builder {
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public RxImageLoader build() {
            return new RxImageLoader(this);
        }

    }


}
