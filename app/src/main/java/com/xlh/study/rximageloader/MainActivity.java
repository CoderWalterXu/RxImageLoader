package com.xlh.study.rximageloader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.xlh.study.library.RxImageLoader;

public class MainActivity extends AppCompatActivity {

    Button btn;
    ImageView iv;

    String url = "https://user-gold-cdn.xitu.io/2020/4/8/1715887e6504f544?imageView2/0/w/1280/h/960/format/webp/ignore-error/1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn = findViewById(R.id.btn);
        iv = findViewById(R.id.iv);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendImageRequest();
            }
        });


    }

    private void sendImageRequest() {

        RxImageLoader.with(this).load(url).into(iv);


    }





}
