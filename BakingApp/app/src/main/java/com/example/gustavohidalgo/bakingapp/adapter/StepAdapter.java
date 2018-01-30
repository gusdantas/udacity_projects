package com.example.gustavohidalgo.bakingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gustavohidalgo.bakingapp.R;
import com.example.gustavohidalgo.bakingapp.interfaces.OnFragAdapterListener;
import com.example.gustavohidalgo.bakingapp.interfaces.OnFragmentInteractionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gustavo.hidalgo on 18/01/29.
 */

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {
    private final Context mContext;
    private static JSONArray mStepList;
    private static OnFragAdapterListener mOnFragAdapterListener;

    public StepAdapter(Context context){ this.mContext = context; }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        mOnFragAdapterListener = (OnFragAdapterListener) context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.step_item, parent, false);
        return new StepViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {
        String stepTitle = "";
        try {
            stepTitle = mStepList.getJSONObject(position).getString("shortDescription");
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

    static class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final Context mContext;
        final TextView mStepTitle;

        public StepViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mStepTitle = itemView.findViewById(R.id.step_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            try {
                mOnFragAdapterListener.onStepChosen(mStepList.getJSONObject(getAdapterPosition()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
