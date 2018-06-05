package com.jan.bakingapp.Widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.gson.Gson;
import com.jan.bakingapp.R;
import com.jan.bakingapp.model.Ingredient;
import com.jan.bakingapp.model.Recipe;

import java.util.ArrayList;

import static com.jan.bakingapp.Widget.IngredientsWidgetProvider.WIDGET_RECIPE;

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {


    private ArrayList<Ingredient> mIngredients;
    private Context mContext;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mIngredients.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_item);
        Ingredient ingredient = mIngredients.get(position);
        view.setTextViewText(R.id.name_tv, ingredient.getIngredient());
        view.setTextViewText(R.id.quantity_tv, ingredient.getQunatity());
        view.setTextViewText(R.id.measure_tv, ingredient.getMeasure());


        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String result = sharedPreferences.getString(WIDGET_RECIPE, null);
        Gson gson = new Gson();
        Recipe recipe = gson.fromJson(result, Recipe.class);
        mIngredients = recipe.getIngredients();
    }

}