package com.jan.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jan.bakingapp.model.Recipe;

public class RecipeActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    final static String EXTRA_POSITION = "extra_position";
    final static String EXTRA_STEP = "extra_step";
    final static String EXTRA_RECIPE = "extra_recipe";

    private RecipePagerAdapter pagerAdapter;
    private ImageView[] dots;
    private int dotsCount;
    private Recipe recipe;
    private LinearLayout pager_indicator;
    private RecipesIdlingResource idlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Intent intent = getIntent();
        if(intent == null){
            return;
        }

        recipe = intent.getParcelableExtra(EXTRA_RECIPE);
        if(recipe == null){
            return;
        }

        int position = intent.getIntExtra(EXTRA_POSITION, 0);


        pager_indicator = findViewById(R.id.viewPagerCountDots);
        ViewPager pager = findViewById(R.id.pager);
        pagerAdapter = new RecipePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        pager.addOnPageChangeListener(this);
        setUiPageViewController();
        pager.setCurrentItem(position);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }
        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setUiPageViewController() {
        dotsCount = pagerAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }

    private class RecipePagerAdapter extends FragmentStatePagerAdapter {
        RecipePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            args.putParcelable(EXTRA_STEP, recipe.getSteps().get(position));
            RecipeFragment fragment = new RecipeFragment();
            fragment.setArguments(args);
            return fragment;
        }



        @Override
        public int getCount() {
            return recipe.getSteps().size();
        }
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new RecipesIdlingResource();
        }
        return idlingResource;
    }

}
