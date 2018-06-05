package com.jan.bakingapp.Retrofit;


import com.jan.bakingapp.model.Recipe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BakingAppAPI {
    @GET("baking.json")
    Call<List<Recipe>> fetchRecipes();
}
