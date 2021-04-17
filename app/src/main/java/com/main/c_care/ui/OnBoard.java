package com.main.c_care.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.main.c_care.R;

public class OnBoard extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout linearLayout;
    private TextView[] dots;
    private Button prevButton;
    private Button nextButton;

    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);

        viewPager = findViewById(R.id.slideViewPager);
        linearLayout = findViewById(R.id.dotsLayout);
        nextButton = findViewById(R.id.nextBtn);
        prevButton = findViewById(R.id.prevBtn);
        SliderAdapter sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);
        viewPager.addOnPageChangeListener(viewListener);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage == dots.length - 1) {
                    startLoginPage();
                }
                else
                    viewPager.setCurrentItem(currentPage + 1);
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(currentPage - 1);
            }
        });
    }

    public void addDotsIndicator(int position) {
        dots = new TextView[3];
        linearLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));

            linearLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    private void startLoginPage() {
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
        finish();
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            currentPage = position;
            if (position == 0) {
                nextButton.setEnabled(true);
                prevButton.setEnabled(false);
                prevButton.setVisibility(View.INVISIBLE);

                nextButton.setText("Next");
                prevButton.setText("");
            } else if (position == dots.length - 1) {
                nextButton.setEnabled(true);
                prevButton.setEnabled(true);
                prevButton.setVisibility(View.VISIBLE);

                nextButton.setText("Finish");
                prevButton.setText("Back");
            } else {
                nextButton.setEnabled(true);
                prevButton.setEnabled(true);
                prevButton.setVisibility(View.VISIBLE);

                nextButton.setText("Next");
                prevButton.setText("Back");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}