package com.example.gustavohidalgo.bakingapp.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gustavohidalgo.bakingapp.R;
import com.example.gustavohidalgo.bakingapp.interfaces.OnDetailToRecipeListener;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_SCALE_TO_FIT;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDetailToRecipeListener} interface
 * to handle interaction events.
 * Use the {@link RecipeDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDetailFragment extends Fragment implements View.OnClickListener,
        Player.EventListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STEP_DETAILS = "step_details";

    // TODO: Rename and change types of parameters
    @BindView(R.id.step_player)
    SimpleExoPlayerView mPlayerView;
    @BindView(R.id.step_thumbnail)
    ImageView mStepThumb;
    @BindView(R.id.step_instruction_tv)
    TextView mStepDetailsTV;
    @BindView(R.id.next_fab)
    FloatingActionButton mNextFab;
    @BindView(R.id.prev_fab)
    FloatingActionButton mPrevFab;

    private JSONObject mStepDetail;
    private SimpleExoPlayer mExoPlayer;
    private long mPlaybackPosition;
    private int mStepIndex, mCurrentWindow;
    private boolean mPlayWhenReady = true;

    private OnDetailToRecipeListener mOnDetailToRecipeListener;

    public RecipeDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param stepDetail Parameter 1.
     * @return A new instance of fragment RecipeDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeDetailFragment newInstance(JSONObject stepDetail) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        args.putString(STEP_DETAILS, stepDetail.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mPlayWhenReady = savedInstanceState.getBoolean("video_should_play");
            mPlaybackPosition = savedInstanceState.getLong("video_position");
        }
        if (getArguments() != null) {
            try {
                mStepDetail = new JSONObject(getArguments().getString(STEP_DETAILS));
                mStepIndex = mStepDetail.getInt("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // Checks the orientation of the screen
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(this, view);
        setup();

        return view;
    }

    private void setup(){

        StringBuilder instruction = new StringBuilder();
        try {
            instruction.append(mStepDetail.getString("shortDescription"))
                    .append("\n\n").append(mStepDetail.getString("description"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mStepDetailsTV.setText(instruction.toString());

        mNextFab.setOnClickListener(this);
        mPrevFab.setOnClickListener(this);

        if (mStepIndex == 0) mPrevFab.setVisibility(View.INVISIBLE);
        if (mStepIndex == mOnDetailToRecipeListener.getLastStepIndex()) mNextFab.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDetailToRecipeListener) {
            mOnDetailToRecipeListener = (OnDetailToRecipeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDetailToRecipeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnDetailToRecipeListener = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.next_fab:
                mOnDetailToRecipeListener.onFragmentInteraction(++mStepIndex);
                break;
            case R.id.prev_fab:
                mOnDetailToRecipeListener.onFragmentInteraction(--mStepIndex);
                break;
        }
    }

    private void initializePlayer() {
        mPlayerView.requestFocus();
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getContext()),
                new DefaultTrackSelector(), new DefaultLoadControl());

        mExoPlayer.addListener(this);
        mPlayerView.setPlayer(mExoPlayer);
        mExoPlayer.setPlayWhenReady(mPlayWhenReady);
        mExoPlayer.seekTo(mCurrentWindow, mPlaybackPosition);

        String thumbnailUrl = "";
        String videoUrl = "";
        try {
            videoUrl = mStepDetail.getString("videoURL");
            thumbnailUrl = mStepDetail.getString("thumbnailURL");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (videoUrl.isEmpty()){
            if(thumbnailUrl.isEmpty()){
                Picasso.with(getContext()).load(R.drawable.icon_no_video).fit().into(mStepThumb);
            } else {
                Picasso.with(getContext()).load(thumbnailUrl).fit()
                        .error(R.drawable.icon_no_video).fit().into(mStepThumb);
            }
            mStepThumb.setVisibility(View.VISIBLE);
            releasePlayer();
        } else {
            Uri uri = Uri.parse(videoUrl);
            MediaSource mediaSource = buildMediaSource(uri);
            mExoPlayer.prepare(mediaSource);
        }
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mPlaybackPosition = mExoPlayer.getCurrentPosition();
            mCurrentWindow = mExoPlayer.getCurrentWindowIndex();
            mPlayWhenReady = mExoPlayer.getPlayWhenReady();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                createMediaSource(uri);
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
    public void onRepeatModeChanged(int repeatMode) {
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    @Override
    public void onSeekProcessed() {
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        populateViewForOrientation(inflater, (ViewGroup) getView());
    }

    private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup) {
        viewGroup.removeAllViewsInLayout();
        View subview = inflater.inflate(R.layout.fragment_recipe_detail, viewGroup);
        ButterKnife.bind(this, subview);
        setup();
    }

    public void setListener(OnDetailToRecipeListener onDetailToRecipeListener){
        this.mOnDetailToRecipeListener = onDetailToRecipeListener;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mExoPlayer != null) {
            outState.putLong("video_position", mExoPlayer.getContentPosition());
            outState.putBoolean("video_should_play", mExoPlayer.getPlayWhenReady());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mExoPlayer == null) {
            initializePlayer();
        }
        if (mExoPlayer != null && mPlayWhenReady) {
            mExoPlayer.seekTo(mPlaybackPosition);
            mExoPlayer.setPlayWhenReady(mPlayWhenReady);
        }
    }
}
