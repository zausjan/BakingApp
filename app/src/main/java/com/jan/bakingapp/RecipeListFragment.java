package com.jan.bakingapp;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jan.bakingapp.Retrofit.BakingAppAPI;
import com.jan.bakingapp.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.jan.bakingapp.RecipeActivity.EXTRA_POSITION;


public class RecipeListFragment extends Fragment {
    @BindView(R.id.recipes_rv) RecyclerView recipesRv;
    private Unbinder unbinder;

    final static String RECIPES_KEY = "recipes";
    private ArrayList<Recipe> recipesList;

    public RecipeListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            fetchRecipes();
        }
        else{
            recipesList = savedInstanceState.getParcelableArrayList(RECIPES_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        if(recipesRv.getParent() != null){
            ((ViewGroup)recipesRv.getParent()).removeView(recipesRv);
        }
        setupRecipesRv();
        return recipesRv;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RECIPES_KEY, recipesList);
    }

    private void setupRecipesRv() {
        Configuration config = getResources().getConfiguration();
        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){
            recipesRv.setLayoutManager(new GridLayoutManager(recipesRv.getContext(), 3));
        }
        else{
            recipesRv.setLayoutManager(new LinearLayoutManager(recipesRv.getContext()));
        }
        if(recipesList != null){
            SimpleRecyclerViewAdapter adapter = new SimpleRecyclerViewAdapter(recipesList);
            recipesRv.setAdapter(adapter);
        }

    }

    void fetchRecipes(){
        final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        BakingAppAPI api = retrofit.create(BakingAppAPI.class);
        Call<List<Recipe>> call = api.fetchRecipes();
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                recipesList = (ArrayList<Recipe>) response.body();
                if(recipesList == null){
                    Toast.makeText(getActivity(), R.string.error_network, Toast.LENGTH_LONG).show();
                }
                setupRecipesRv();
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Toast.makeText(getActivity(), R.string.error_network, Toast.LENGTH_LONG).show();
                t.printStackTrace();

            }
        });
    }
    public static class SimpleRecyclerViewAdapter extends RecyclerView.Adapter<SimpleRecyclerViewAdapter.ViewHolder>{

        List<Recipe> mRecipes;
        Recipe mBoundRecipe;

        SimpleRecyclerViewAdapter(List<Recipe> recipes){
            mRecipes = recipes;
        }

        static class ViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.name_tv) TextView recipeNameTv;
            @BindView(R.id.iv_image) ImageView recipeIv;
            final View mView;
            Context context;

            ViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                context = mView.getContext();
                ButterKnife.bind(this, itemView);
            }
        }

        @NonNull
        @Override
        public SimpleRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipe_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final SimpleRecyclerViewAdapter.ViewHolder holder, final int position) {
            mBoundRecipe = getItem(holder.getAdapterPosition());
            String recipeName = mBoundRecipe != null ? mBoundRecipe.getName() : null;
            holder.recipeNameTv.setText(recipeName);
            Picasso.with(holder.context).load(mBoundRecipe.getImage())
                    .error(R.mipmap.cake_icon)
                    .placeholder(R.mipmap.cake_icon)
                    .into(holder.recipeIv);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    Context context = v.getContext();
                    Intent intent = new Intent(context, StepListActivity.class);
                    mBoundRecipe = getItem(holder.getAdapterPosition());
                    intent.putExtra(EXTRA_POSITION, holder.getAdapterPosition());
                    intent.putExtra(RecipeActivity.EXTRA_RECIPE, mBoundRecipe);
                    context.startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mRecipes.size();
        }

        Recipe getItem(int position){
            return mRecipes.get(position);
        }
    }



}
