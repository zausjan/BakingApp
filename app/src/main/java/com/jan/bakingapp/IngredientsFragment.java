package com.jan.bakingapp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jan.bakingapp.model.Ingredient;
import com.jan.bakingapp.model.Recipe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.jan.bakingapp.RecipeActivity.EXTRA_RECIPE;

public class IngredientsFragment extends Fragment {
    @BindView(R.id.ingredients_rv) RecyclerView ingredientsRv;
    private Unbinder unbinder;
    private Recipe recipe;


    public IngredientsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        Bundle b = this.getArguments();
        if(b != null){
            recipe = b.getParcelable(EXTRA_RECIPE);
        }

        if(ingredientsRv.getParent()!=null)
            ((ViewGroup)ingredientsRv.getParent()).removeView(ingredientsRv);
        setupIngredientsList();
        return ingredientsRv;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setupIngredientsList() {
        ingredientsRv.setLayoutManager(new LinearLayoutManager(ingredientsRv.getContext()));
        if (recipe.getIngredients() == null) {
            return;
        }
        ingredientsRv.setAdapter(
                new IngredientsFragment.SimpleRecyclerViewAdapter(recipe.getIngredients()));
        ingredientsRv.setNestedScrollingEnabled(false);
    }


    public static class SimpleRecyclerViewAdapter extends
            RecyclerView.Adapter<IngredientsFragment.SimpleRecyclerViewAdapter.ViewHolder>{

        private ArrayList<Ingredient> ingredients;

        SimpleRecyclerViewAdapter(ArrayList<Ingredient> ingredients) {
            this.ingredients = ingredients;
        }

        static class ViewHolder extends RecyclerView.ViewHolder{
            Ingredient boundIngredient;
            final View view;
            @BindView(R.id.name_tv) TextView ingredientNameTv;
            @BindView(R.id.quantity_tv) TextView quantityTv;
            @BindView(R.id.measure_tv) TextView measureTv;

            ViewHolder(View view) {
                super(view);
                this.view = view;
                ButterKnife.bind(this, view);
            }
        }

        @Override
        public IngredientsFragment.SimpleRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ingredients_list_item, parent, false);
            return new IngredientsFragment.SimpleRecyclerViewAdapter.ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull final IngredientsFragment.SimpleRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.boundIngredient = getItem(holder.getAdapterPosition());
            holder.ingredientNameTv.setText(holder.boundIngredient.getIngredient());
            holder.quantityTv.setText(holder.boundIngredient.getQunatity());
            holder.measureTv.setText(holder.boundIngredient.getMeasure());
        }

        @Override
        public int getItemCount() {
            return ingredients.size();
        }

        Ingredient getItem(int position){
            return ingredients.get(position);
        }

    }
}