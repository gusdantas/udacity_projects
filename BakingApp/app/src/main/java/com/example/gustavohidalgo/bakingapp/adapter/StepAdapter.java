package com.example.gustavohidalgo.bakingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gustavohidalgo.bakingapp.R;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by gustavo.hidalgo on 18/01/29.
 */

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {
    private final Context mContext;
    private JSONArray mStepList;

    public StepAdapter(Context context){ this.mContext = context; }

    @Override
    public StepAdapter.StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.step_item, parent, false);
        return new StepAdapter.StepViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(StepAdapter.StepViewHolder holder, int position) {
        String StepTitle = "";
        try {
            StepTitle = mStepList.getJSONObject(position).getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.mStepTitle.setText(StepTitle);
    }

    @Override
    public int getItemCount() {
        if (mStepList != null) {
            return mStepList.length();
        } else {
            return 0;
        }
    }

    public void setStepList(JSONArray StepList) {
        this.mStepList = StepList;
        notifyDataSetChanged();
    }

    class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
//            Intent intent = new Intent(mContext, StepActivity.class);
//            try {
//                intent.putExtra("Step", mStepList.getJSONObject(getAdapterPosition()).toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            mContext.startActivity(intent);
        }
    }
}
