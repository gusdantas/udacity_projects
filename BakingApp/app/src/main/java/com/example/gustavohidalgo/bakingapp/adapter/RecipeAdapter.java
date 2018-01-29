package com.example.gustavohidalgo.bakingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gustavohidalgo.bakingapp.R;
import com.example.gustavohidalgo.bakingapp.view.RecipeActivity;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by gustavo.hidalgo on 18/01/29.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private final Context mContext;
    private JSONArray mRecipeList;

    public RecipeAdapter(Context context){ this.mContext = context; }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        String recipeTitle = "";
        try {
            recipeTitle = mRecipeList.getJSONObject(position).getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.mRecipeTitle.setText(recipeTitle);
    }

    @Override
    public int getItemCount() {
        if (mRecipeList != null) {
            return mRecipeList.length();
        } else {
            return 0;
        }
    }

    public void setRecipeList(JSONArray recipeList) {
        this.mRecipeList = recipeList;
        notifyDataSetChanged();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final Context mContext;
        final TextView mRecipeTitle;


        public RecipeViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mRecipeTitle = itemView.findViewById(R.id.recipeTitle);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mContext, RecipeActivity.class);
            try {
                intent.putExtra("recipe", mRecipeList.getJSONObject(getAdapterPosition()).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mContext.startActivity(intent);
        }
    }
}
