package integrals.inlens.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import integrals.inlens.Fragments.IntroSlide1Fragment;
import integrals.inlens.Fragments.IntroSlide2Fragment;
import integrals.inlens.Fragments.IntroSlide3Fragment;
import integrals.inlens.Fragments.IntroSlide4Fragment;
import integrals.inlens.Fragments.IntroSlide5Fragment;
import integrals.inlens.R;

public class IntroActivity extends AppCompatActivity {

    private IntroAdapter introAdapter;
    private ViewPager IntroViewPager;
    private int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        introAdapter = new IntroAdapter(getSupportFragmentManager());
        IntroViewPager = findViewById(R.id.initialviewpager);
        IntroViewPager.setAdapter(introAdapter);

        findViewById(R.id.initial_next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntroViewPager.setCurrentItem(i++);
                if(i==5)
                {
                    findViewById(R.id.initial_bottom_bar).clearAnimation();
                    findViewById(R.id.initial_bottom_bar).setAnimation(AnimationUtils.loadAnimation(IntroActivity.this,R.anim.fade_out));
                    findViewById(R.id.initial_bottom_bar).getAnimation().start();
                    findViewById(R.id.initial_bottom_bar).setVisibility(View.GONE);
                }
            }
        });

        findViewById(R.id.initial_skip_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findViewById(R.id.initial_bottom_bar).clearAnimation();
                findViewById(R.id.initial_bottom_bar).setAnimation(AnimationUtils.loadAnimation(IntroActivity.this,R.anim.fade_out));
                findViewById(R.id.initial_bottom_bar).getAnimation().start();
                findViewById(R.id.initial_bottom_bar).setVisibility(View.GONE);
                IntroViewPager.setCurrentItem(5);

            }
        });

        IntroViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(position==5)
                {
                    findViewById(R.id.initial_bottom_bar).clearAnimation();
                    findViewById(R.id.initial_bottom_bar).setAnimation(AnimationUtils.loadAnimation(IntroActivity.this,R.anim.fade_out));
                    findViewById(R.id.initial_bottom_bar).getAnimation().start();
                    findViewById(R.id.initial_bottom_bar).setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    private class IntroAdapter extends FragmentPagerAdapter {

        public IntroAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    IntroSlide1Fragment slide1Fragment = new IntroSlide1Fragment();
                    return slide1Fragment;
                case 1:
                    IntroSlide2Fragment slide2Fragment = new IntroSlide2Fragment();
                    return slide2Fragment;
                case 2:
                    IntroSlide3Fragment slide3Fragment = new IntroSlide3Fragment();
                    return slide3Fragment;
                case 3:
                    IntroSlide4Fragment slide4Fragment = new IntroSlide4Fragment();
                    return slide4Fragment;
                case 4:
                    IntroSlide5Fragment slide5Fragment = new IntroSlide5Fragment();
                    return slide5Fragment;
                default:
                    IntroSlide5Fragment defaultfragment = new IntroSlide5Fragment();
                    return defaultfragment;
            }

        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}
