package com.jan.bakingapp;


import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.jan.bakingapp.model.Step;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.jan.bakingapp.RecipeActivity.EXTRA_STEP;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeFragment extends Fragment implements ExoPlayer.EventListener{

    private final static String EXTRA_PLAYER_POSITION = "extra_position";
    private SimpleExoPlayer exoPlayer;
    @BindView(R.id.player_view)  SimpleExoPlayerView exoPlayerView;
    @BindView(R.id.tv_instruction)  TextView instructionsTv;
    private Unbinder unbinder;
    public RecipeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        Bundle args = getArguments();
        Step step = args.getParcelable(EXTRA_STEP);
        instructionsTv.setText(step.getDescription());



        Uri videoURI = Uri.parse(step.getVideoURL());
        initializePlayer(videoURI);
        if(savedInstanceState != null){
            long playerPosition = savedInstanceState.getLong(EXTRA_PLAYER_POSITION);
            exoPlayer.seekTo(playerPosition);
        }

        exoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_launcher_background));

        setLayout(getResources().getConfiguration());

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        long position = exoPlayer.getCurrentPosition();
        outState.putLong(EXTRA_PLAYER_POSITION, position);
    }


    @Override
    public void onPause() {
        super.onPause();
        pausePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        startPlayer();
    }

    private void initializePlayer(Uri mediaUri){
        if(exoPlayer == null){
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();

            exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            exoPlayerView.setPlayer(exoPlayer);

            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri,
                    new DefaultDataSourceFactory(getContext(), userAgent),
                    new DefaultExtractorsFactory(),
                    null, null);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer(){
        exoPlayer.stop();
        exoPlayer.release();
        exoPlayer = null;
    }

    private void setLayout(Configuration config){
        //MultiPane is active
        if(getActivity().findViewById(R.id.fragment_recipe) != null){
            return;
        }
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) exoPlayerView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            exoPlayerView.setLayoutParams(params);
            if (getActivity().getActionBar() != null) {
                getActivity().getActionBar().hide();
            }
            getActivity().getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE);
            exoPlayerView.bringToFront();
            RelativeLayout pager_indicator = getActivity().findViewById(R.id.viewPagerIndicator);
            pager_indicator.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            if (!isVisibleToUser) {
               pausePlayer();
            }

            if (isVisibleToUser) {
               startPlayer();
            }

        }

    }
    private void pausePlayer(){
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.getPlaybackState();
    }

    private void startPlayer(){
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.getPlaybackState();
    }
}
