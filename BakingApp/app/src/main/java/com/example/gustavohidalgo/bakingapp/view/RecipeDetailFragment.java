package com.example.gustavohidalgo.bakingapp.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gustavohidalgo.bakingapp.R;
import com.example.gustavohidalgo.bakingapp.interfaces.OnAdapterToDetailListener;
import com.example.gustavohidalgo.bakingapp.interfaces.OnDetailToRecipeListener;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

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
    private JSONObject mStepDetail;
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private long mPlaybackPosition;
    private int mStepIndex, mCurrentWindow;
    private boolean mPlayWhenReady;
    private TextView mStepDetailsTV;
    private FloatingActionButton mNextFab, mPrevFab;

    private OnDetailToRecipeListener mListener;

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
        if (getArguments() != null) {
            try {
                mStepDetail = new JSONObject(getArguments().getString(STEP_DETAILS));
                mStepIndex = mStepDetail.getInt("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        mPlayerView = view.findViewById(R.id.step_player);
        mStepDetailsTV = view.findViewById(R.id.step_instruction_tv);
        StringBuilder instruction = new StringBuilder();
        try {
            instruction.append(mStepDetail.getString("shortDescription"))
                    .append("\n\n").append(mStepDetail.getString("description"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mStepDetailsTV.setText(instruction.toString());

        mNextFab = view.findViewById(R.id.next_fab);
        mPrevFab = view.findViewById(R.id.prev_fab);
        mNextFab.setOnClickListener(this);
        mPrevFab.setOnClickListener(this);

        if (mStepIndex == 0) mPrevFab.setVisibility(View.INVISIBLE);
        if (mStepIndex == mListener.getLastStepIndex()) mNextFab.setVisibility(View.INVISIBLE);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDetailToRecipeListener) {
            mListener = (OnDetailToRecipeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDetailToRecipeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || mExoPlayer == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.next_fab:
                mListener.onFragmentInteraction(++mStepIndex);
                break;
            case R.id.prev_fab:
                mListener.onFragmentInteraction(--mStepIndex);
                break;
        }
    }

    private void initializePlayer() {
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getContext()),
                new DefaultTrackSelector(), new DefaultLoadControl());

        mExoPlayer.addListener(this);

        mPlayerView.setPlayer(mExoPlayer);

        mExoPlayer.setPlayWhenReady(mPlayWhenReady);
        mExoPlayer.seekTo(mCurrentWindow, mPlaybackPosition);

        String videoUrl = getVideoUrl();
        if (videoUrl.isEmpty()){
            releasePlayer();
        } else {
            Uri uri = Uri.parse(videoUrl);
            MediaSource mediaSource = buildMediaSource(uri);
            mExoPlayer.prepare(mediaSource, true, false);
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

    private String getVideoUrl(){
        String videoURL = "";
        try {
            videoURL = mStepDetail.getString("videoURL");
            String thumbnailURL = mStepDetail.getString("thumbnailURL");
            if (videoURL.equals("") && !thumbnailURL.equals("")) videoURL = thumbnailURL;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return videoURL;
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


}
