package com.mubo.socialapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

public class ShowImage extends AppCompatActivity {

    ImageView img;
    String url;
    RelativeLayout back;
    Activity act;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        act=this;
        img=findViewById(R.id.image);
        back=findViewById(R.id.back);
        url=this.getIntent().getStringExtra("image");
        Picasso.get().load(url).into(img);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act.finish();
            }
        });
    }
}