package com.jan.bakingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jan.bakingapp.Widget.UpdateWidgetsService;
import com.jan.bakingapp.model.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jan.bakingapp.Widget.IngredientsWidgetProvider.WIDGET_RECIPE;
import static com.jan.bakingapp.RecipeActivity.EXTRA_RECIPE;

public class StepListActivity extends AppCompatActivity {


    @BindView(R.id.ingredients_cv) CardView ingredientsCv;
    private Recipe recipe;
    private boolean doublePane = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_list);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);
        if(findViewById(R.id.fragment_recipe) != null){
            doublePane = true;
        }

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        recipe = intent.getParcelableExtra(EXTRA_RECIPE);
        setTitle(recipe.getName());

        ingredientsCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              updateIngredients();
            }
        });

        if(savedInstanceState == null) {
            updateStepList();

        }
        // doublepane landscape
        if(doublePane && findViewById(R.id.fragment_ingredients) != null){
            updateIngredients();
        }

        if(getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE) == null){
            updateWidget();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_to_widget:
                updateWidget();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    private void updateIngredients(){
        if(doublePane){
            Bundle args = new Bundle();
            args.putParcelable(EXTRA_RECIPE, recipe);
            IngredientsFragment fragment = new IngredientsFragment();
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if(findViewById(R.id.fragment_ingredients) != null){
                transaction.replace(R.id.fragment_ingredients, fragment);
            }
            else {
                transaction.replace(R.id.fragment_recipe, fragment);
            }
            transaction.commit();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), IngredientsActivity.class);
            intent.putExtra(EXTRA_RECIPE, recipe);
            startActivity(intent);
        }
    }
    private void updateStepList(){
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_RECIPE, recipe);
        StepListFragment fragment = new StepListFragment();
        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_step_list, fragment);
        transaction.commit();

    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.activity_error_message, Toast.LENGTH_SHORT).show();
    }

    void setSharedPreferences(Recipe recipe){
        Gson gson = new Gson();
        String widget_recipe = gson.toJson(recipe);

        SharedPreferences.Editor editor =
                getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)
                .edit();
        editor.putString(WIDGET_RECIPE, widget_recipe);
        editor.apply();
    }

    private void updateWidget(){
        setSharedPreferences(recipe);
        UpdateWidgetsService.updateWidgets(this);
    }
}
