package com.tgp.tgpglideapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView viewById = findViewById(R.id.image);
        View textIv = findViewById(R.id.text);
        textIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(MainActivity.this).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1585917271450&di=31a4faef4d52f209843d6e9e6cfa4d99&imgtype=0&src=http%3A%2F%2Ft8.baidu.com%2Fit%2Fu%3D1484500186%2C1503043093%26fm%3D79%26app%3D86%26f%3DJPEG%3Fw%3D1280%26h%3D853").into(viewById);
            }
        });
    }
}
