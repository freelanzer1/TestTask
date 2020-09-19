package com.konstantinov.testtask.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.konstantinov.testtask.R;
import com.squareup.picasso.Picasso;

public class FullscreenActivity extends AppCompatActivity {
    ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_fullscreen );
        mImageView = findViewById(R.id.imageViewFull);
        Bundle arguments = getIntent().getExtras();

        if (arguments != null) {
            Uri outUri = (Uri)arguments.get("image_uri");

            Picasso
                    .get()
                    .load(outUri)
                    .placeholder(R.drawable.gallery)
                    .error(R.drawable.nophoto)
                    .into(mImageView);
        }
    }
}
