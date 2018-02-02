package com.example.gustavohidalgo.bakingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gustavohidalgo.bakingapp.R;
import com.example.gustavohidalgo.bakingapp.interfaces.OnAdapterToDetailListener;
import com.example.gustavohidalgo.bakingapp.view.RecipeActivity;
import com.example.gustavohidalgo.bakingapp.view.RecipeDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gustavo.hidalgo on 18/01/29.
 */

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {
    private final Context mContext;
    private static JSONArray mStepList;
    private static OnAdapterToDetailListener mOnAdapterToDetailListener;

    public StepAdapter(Context context){ this.mContext = context; }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.step_item, parent, false);
        return new StepViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {
        StringBuilder stepTitle = new StringBuilder();
        try {
            stepTitle.append(position).append(" - ")
                    .append(mStepList.getJSONObject(position).getString("shortDescription"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.mStepTitle.setText(stepTitle);
    }

    @Override
    public int getItemCount() {
        if (mStepList != null) {
            return mStepList.length();
        } else {
            return 0;
        }
    }

    public void setStepList(JSONArray stepList) {
        mStepList = stepList;
        notifyDataSetChanged();
    }

    public void setListener(OnAdapterToDetailListener listener) {
        mOnAdapterToDetailListener = listener;
    }

    static class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final Context mContext;
        @BindView(R.id.step_title) TextView mStepTitle;

        public StepViewHolder(Context context, View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = context;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnAdapterToDetailListener.onStepChosen(getAdapterPosition());
        }
    }
}
