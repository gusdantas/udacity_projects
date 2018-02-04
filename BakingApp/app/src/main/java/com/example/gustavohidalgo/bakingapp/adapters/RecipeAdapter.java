package com.example.gustavohidalgo.bakingapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gustavohidalgo.bakingapp.R;
import com.example.gustavohidalgo.bakingapp.interfaces.OnAdapterToDetailListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gustavo.hidalgo on 18/01/29.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private final Context mContext;
    private static JSONArray mRecipeList;
    private static OnAdapterToDetailListener mOnAdapterToDetailListener;

    public RecipeAdapter(Context context){ this.mContext = context; }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        StringBuilder recipeInfo = new StringBuilder();
        String image = "";
        try {
            recipeInfo.append(mRecipeList.getJSONObject(position).getString("name"))
                    .append("\n")
                    .append(mRecipeList.getJSONObject(position).getJSONArray("steps").length())
                    .append(" steps").append("\nServes ")
                    .append(mRecipeList.getJSONObject(position).getString("servings"))
                    .append(" people");
            image = mRecipeList.getJSONObject(position).getString("image");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.mRecipeTitle.setText(recipeInfo.toString());
        if (image.isEmpty()) {
            Picasso.with(mContext).load(R.drawable.pastry_assortment).fit().into(holder.mRecipeThumb);
        } else {
            Picasso.with(mContext).load(image).placeholder(R.drawable.pastry_assortment).fit()
                    .error(R.drawable.pastry_assortment).fit().into(holder.mRecipeThumb);
        }
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
        mRecipeList = recipeList;
        notifyDataSetChanged();
    }

    public void setListener(OnAdapterToDetailListener listener) {
        mOnAdapterToDetailListener = listener;
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_iv) ImageView mRecipeThumb;
        @BindView(R.id.recipe_tv) TextView mRecipeTitle;


        public RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnAdapterToDetailListener.onStepChosen(getAdapterPosition());
        }
    }
}
