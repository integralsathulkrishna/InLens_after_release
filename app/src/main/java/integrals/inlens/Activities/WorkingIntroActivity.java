package integrals.inlens.Activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

import integrals.inlens.Fragments.IntroSlide1Fragment;
import integrals.inlens.Fragments.IntroSlide2Fragment;
import integrals.inlens.Fragments.IntroSlide3Fragment;
import integrals.inlens.Fragments.IntroSlide4Fragment;
import integrals.inlens.MainActivity;
import integrals.inlens.R;

public class WorkingIntroActivity extends AppCompatActivity {

    private IntroAdapter introAdapter;
    private ViewPager IntroViewPager;
    private int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_intro);

        introAdapter = new IntroAdapter(getSupportFragmentManager());
        IntroViewPager = findViewById(R.id.initialviewpager);
        IntroViewPager.setAdapter(introAdapter);

        findViewById(R.id.initial_next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(i<4)
                IntroViewPager.setCurrentItem(i++);

            }
        });

        findViewById(R.id.initial_skip_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(WorkingIntroActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.activity_fade_in,R.anim.activity_fade_out);
                finish();

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
                default:
                    IntroSlide4Fragment defaultFragment = new IntroSlide4Fragment();
                    return defaultFragment;
            }

        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent(WorkingIntroActivity.this,MainActivity.class));
            overridePendingTransition(R.anim.activity_fade_in,R.anim.activity_fade_out);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
