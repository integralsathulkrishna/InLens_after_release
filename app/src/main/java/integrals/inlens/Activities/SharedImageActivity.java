package integrals.inlens.Activities;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import integrals.inlens.R;
import uk.co.senab.photoview.PhotoView;

public class SharedImageActivity extends AppCompatActivity {

    private PhotoView ShareImageView;
    private ProgressBar ShareImageProgressbar;
    private RelativeLayout RootForSharedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_shared_image);

        ShareImageView = findViewById(R.id.shareimage_photoview);
        ShareImageProgressbar = findViewById(R.id.shareimage_progressbar);
        RootForSharedImage = findViewById(R.id.rootforsharedimage);

        String ImageUrl = getIntent().getStringExtra("url");

        if(!TextUtils.isEmpty(ImageUrl))
        {
            RequestOptions requestOptions = new RequestOptions()
                    .fitCenter();

            Glide.with(getApplicationContext())
                    .load(ImageUrl)
                    .thumbnail(0.1f)
                    .apply(requestOptions)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            ShareImageProgressbar.setVisibility(View.GONE);
                            Snackbar.make(RootForSharedImage,"Failed to load image",Snackbar.LENGTH_SHORT).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            ShareImageProgressbar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(ShareImageView);
        }
        else
        {
            Snackbar.make(RootForSharedImage,"Unable to view image",Snackbar.LENGTH_SHORT).show();
        }

    }
}
