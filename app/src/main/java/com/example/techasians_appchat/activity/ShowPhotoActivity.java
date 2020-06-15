package com.example.techasians_appchat.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.techasians_appchat.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShowPhotoActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ArrayList<String> photoList;
    private String imageURL;
    private PhotoView photo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);

        initView();
    }

    private void initView() {
        photo = findViewById(R.id.imagephoto);
        imageURL = getIntent().getStringExtra("image");
        Picasso.with(ShowPhotoActivity.this).load(imageURL)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(photo);
    }


}
