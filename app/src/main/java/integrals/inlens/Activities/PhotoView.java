package integrals.inlens.Activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import integrals.inlens.Models.Blog;
import integrals.inlens.R;
import uk.co.senab.photoview.PhotoViewAttacher;


public class PhotoView extends AppCompatActivity {
    public ArrayList<Blog> blogArrayList = new ArrayList<>();
    private int Position;
    private Button OriginalImageButton;
    private PhotoViewAttacher photoViewAttacher;
    private Boolean OriginalImageViewed = false;
    private CardView cardView;

    //For the purpose of swipe
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private ImageView imageView;
    private GestureDetector gdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For Full screen purpose
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //


        setContentView(R.layout.photo_card);
        cardView = (CardView) findViewById(R.id.PhotoCardView);
        blogArrayList = getIntent().getExtras().getParcelableArrayList("data");
        Position = getIntent().getExtras().getInt("position");
        OriginalImageButton = (Button) findViewById(R.id.OriginalImageButton);
         gdt = new GestureDetector(new GestureListener());




        SetThumbPhoto(getApplicationContext(),
                blogArrayList.get(Position).getImageThumb(),
                blogArrayList.get(Position).getOriginalImageName(),
                blogArrayList.get(Position).getBlogTitle(),
                blogArrayList.get(Position).getBlogDescription(),
                "NULLX",
                blogArrayList.get(Position).getLocation(),
                blogArrayList.get(Position).getWeatherDetails()



        );
        OriginalImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (OriginalImageViewed == false) {
                    LoadOriginalPhoto(getApplicationContext(),
                            blogArrayList.get(Position).getOriginalImageName());
                    OriginalImageViewed = true;
                } else {
                    SetThumbPhoto(getApplicationContext(),
                            blogArrayList.get(Position).getImageThumb(),
                            blogArrayList.get(Position).getOriginalImageName(),
                            blogArrayList.get(Position).getBlogTitle(),
                            blogArrayList.get(Position).getBlogDescription(),
                            "NULLX",
                            blogArrayList.get(Position).getLocation(),
                            blogArrayList.get(Position).getWeatherDetails()
                    );
                    OriginalImageViewed = false;
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();


    }








    public void SetThumbPhoto(final Context context,
                              final String ImageUri,
                              final String getImage,
                              final String getBlogTitle
            , final String getBlogDescription,
                              final String PostKey
            , final String LocationT
            , final String Weather) {
        OriginalImageButton.setBackgroundResource(R.drawable.ic_original_image);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.ImageProgress);
        final ImageView imageView = (ImageView) findViewById(R.id.PhotoCardImageView);
        final RelativeLayout CaptionLayout = (RelativeLayout) findViewById(R.id.CaptionLayout);
        final RelativeLayout WeatherLayout = (RelativeLayout) findViewById(R.id.WeatherLayout);
        final RelativeLayout LocationLayout = (RelativeLayout) findViewById(R.id.LocationLayout);
        TextView CaptionText = (TextView) findViewById(R.id.CaptionTextView);
        TextView WeatherText = (TextView) findViewById(R.id.WeatherText);
        TextView LocationText = (TextView) findViewById(R.id.LocationText);
        if (getBlogTitle.contentEquals("NULLX")) {
            CaptionLayout.setVisibility(View.GONE);
        } else {
            CaptionText.setText(getBlogTitle);

        }
        if (Weather.contentEquals("NULLX")) {
            WeatherLayout.setVisibility(View.GONE);
        } else {
            WeatherText.setText(Weather);
        }
        if (LocationT.contentEquals("NULLX")) {
            LocationLayout.setVisibility(View.GONE);
        } else {
            LocationText.setText(LocationT);
        }

        progressBar.setVisibility(View.VISIBLE);

        Glide.with(context)
                .load(ImageUri)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(context, "Load Failed. Please check your internet connection. ", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }


                })
                .into(imageView);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                gdt.onTouchEvent(event);
                return true;
            }
        });


    }





    private void LoadOriginalPhoto(final Context context,
                                   final String OriginalImageUri
    ) {
        OriginalImageButton.setBackgroundResource(R.drawable.ic_close);
        ImageView imageView = (ImageView) findViewById(R.id.PhotoCardImageView);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.ImageProgress);
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(context)
                .load(OriginalImageUri)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(context, "Load Failed. Please check your internet connection. ", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }


                })
                .into(imageView);
        photoViewAttacher = new PhotoViewAttacher(imageView);


    }















    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Position+=1;
                try {
                    SetThumbPhoto(getApplicationContext(),
                            blogArrayList.get(Position).getImageThumb(),
                            blogArrayList.get(Position).getOriginalImageName(),
                            blogArrayList.get(Position).getBlogTitle(),
                            blogArrayList.get(Position).getBlogDescription(),
                            "NULLX",
                            blogArrayList.get(Position).getLocation(),
                            blogArrayList.get(Position).getWeatherDetails()
                    );

                }catch (IndexOutOfBoundsException e){
                    Position-=1;
                    Toast.makeText(getApplicationContext(),"Last Post",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                return false; // Right to left
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Position-=1;
                try {

                    SetThumbPhoto(getApplicationContext(),
                            blogArrayList.get(Position).getImageThumb(),
                            blogArrayList.get(Position).getOriginalImageName(),
                            blogArrayList.get(Position).getBlogTitle(),
                            blogArrayList.get(Position).getBlogDescription(),
                            "NULLX",
                            blogArrayList.get(Position).getLocation(),
                            blogArrayList.get(Position).getWeatherDetails()
                    );
                }catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                    Position+=1;
                    Toast.makeText(getApplicationContext(),"First Post",Toast.LENGTH_SHORT).show();
                }

                return false; // Left to right
            }

            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Bottom to top
            }  else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Top to bottom
            }
            return false;
        }
    }


}