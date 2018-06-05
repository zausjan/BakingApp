package com.jan.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jan.bakingapp.model.Recipe;
import com.jan.bakingapp.model.Step;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.jan.bakingapp.RecipeActivity.EXTRA_POSITION;
import static com.jan.bakingapp.RecipeActivity.EXTRA_RECIPE;
import static com.jan.bakingapp.RecipeActivity.EXTRA_STEP;


public class StepListFragment extends Fragment {
    @BindView(R.id.steps_rv) RecyclerView recipeStepsRv;
    private Unbinder unbinder;
    private Recipe recipe;

    public StepListFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = this.getArguments();
        if(b != null){
            recipe = b.getParcelable(EXTRA_RECIPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_step_list, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        setupStepsRv();
        if(recipeStepsRv.getParent()!=null)
            ((ViewGroup)recipeStepsRv.getParent()).removeView(recipeStepsRv);
        return recipeStepsRv;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setupStepsRv() {
        recipeStepsRv.setLayoutManager(new LinearLayoutManager(recipeStepsRv.getContext()));
        if (recipe.getSteps() == null) {
            return;
        }
        recipeStepsRv.setAdapter(new SimpleRecyclerViewAdapter(recipe.getSteps()));
        recipeStepsRv.setNestedScrollingEnabled(false);
    }

    public  class SimpleRecyclerViewAdapter extends
            RecyclerView.Adapter<StepListFragment.SimpleRecyclerViewAdapter.ViewHolder>{

        private ArrayList<Step> mSteps;

        SimpleRecyclerViewAdapter(ArrayList<Step> recipeSteps) {
            mSteps = recipeSteps;
        }

         class ViewHolder extends RecyclerView.ViewHolder{
            Step mBoundStep;
            final View mView;
            @BindView(R.id.description_rv) TextView descriptionTv;
            @BindView(R.id.iv_thumbnail) ImageView thumbnailIv;
            Context mContext;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mContext = view.getContext();
                ButterKnife.bind(this, view);
            }
        }

        @Override
        public StepListFragment.SimpleRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.step_list_item, parent, false);
            return new StepListFragment.SimpleRecyclerViewAdapter.ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull final StepListFragment.SimpleRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mBoundStep = getItem(holder.getAdapterPosition());
            holder.descriptionTv.setText(holder.mBoundStep.getShortDescription());

            String thumbnailUrl = holder.mBoundStep.getThumbnailURL();
            Picasso.with(holder.mContext).load(thumbnailUrl.equals("") ? null : thumbnailUrl)
                    .placeholder(R.mipmap.checklist_icon)
                    .error(R.mipmap.checklist_icon)
                    .into(holder.thumbnailIv);


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    // Create new RecipeActivity
                    if (getActivity().findViewById(R.id.fragment_recipe) == null) {
                        Intent intent = new Intent(context, RecipeActivity.class);
                        intent.putExtra(EXTRA_RECIPE, recipe);
                        intent.putExtra(EXTRA_POSITION, holder.getAdapterPosition());
                        context.startActivity(intent);
                    }
                    // MultiPane, replace fragment
                    else{
                        Bundle args = new Bundle();
                        args.putParcelable(EXTRA_STEP, recipe.getSteps().get(holder.getAdapterPosition()));
                        RecipeFragment fragment = new RecipeFragment();
                        fragment.setArguments(args);
                        FragmentTransaction transaction = ((AppCompatActivity)holder.mContext)
                                .getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_recipe, fragment);
                        transaction.commit();
                    }
                }
            });
        }
        @Override
        public int getItemCount() {
            return mSteps.size();
        }

        Step getItem(int position){
            return mSteps.get(position);
        }

    }

}
