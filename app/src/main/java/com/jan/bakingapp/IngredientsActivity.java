package com.jan.bakingapp;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jan.bakingapp.model.Recipe;

import static com.jan.bakingapp.RecipeActivity.EXTRA_RECIPE;

public class IngredientsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        Intent intent = getIntent();
        Recipe recipe = intent.getParcelableExtra(EXTRA_RECIPE);
        setTitle(recipe.getName());

        if(savedInstanceState == null){
            Bundle args = new Bundle();
            args.putParcelable(EXTRA_RECIPE, recipe);
            IngredientsFragment fragment = new IngredientsFragment();
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.ingredients_list_fragment, fragment);
            transaction.commit();
        }
    }
}
