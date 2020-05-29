package com.stylet.clamp.Activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.stylet.clamp.R;

public class ShowImageActivity extends AppCompatActivity {

    String blog_image;
    ProgressBar progressBar;
    ConstraintLayout showimaelayout;
    ImageView show_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        blog_image = getIntent().getStringExtra("blog_image");

        show_image = findViewById(R.id.showImage);
        progressBar = findViewById(R.id.image_progressbar);
        showimaelayout = findViewById(R.id.showimagelayout);


        progressBar.setVisibility(View.VISIBLE);



            Glide.with(this)
                    .load(blog_image)
                    .into(show_image);

            progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSwipeLeft(ShowImageActivity.this);
    }

    public static int getDominantColor(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;
    }



}
