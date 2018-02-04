package com.example.gustavohidalgo.bakingapp.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gustavohidalgo.bakingapp.R;
import com.example.gustavohidalgo.bakingapp.adapters.StepAdapter;
import com.example.gustavohidalgo.bakingapp.interfaces.OnDetailToRecipeListener;
import com.example.gustavohidalgo.bakingapp.interfaces.OnAdapterToDetailListener;
import com.example.gustavohidalgo.bakingapp.utils.Measure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDetailToRecipeListener} interface
 * to handle interaction events.
 * Use the {@link RecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeFragment extends Fragment implements OnAdapterToDetailListener,
        View.OnClickListener {

    @BindView(R.id.ingredients_label_tv)
    TextView mIngredientsLabelTV;
    @BindView(R.id.expand_ing_iv)
    ImageView mExpandIngIV;
    @BindView(R.id.ingredients_tv)
    TextView mIngredientsTV;
    @BindView(R.id.step_rv)
    RecyclerView mStepRV;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String RECIPE = "recipe";

    // TODO: Rename and change types of parameters
    private String mRecipe;
    private JSONArray mIngredientList, mStepList;

    private OnDetailToRecipeListener mOnDetailToRecipeListener;

    public RecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recipe Parameter 1.
     * @return A new instance of fragment RecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeFragment newInstance(String recipe) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putString(RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipe = getArguments().getString(RECIPE);
        }
        try {
            JSONObject mRecipeJson = new JSONObject(mRecipe);
            mIngredientList = mRecipeJson.getJSONArray("ingredients");
            mStepList = mRecipeJson.getJSONArray("steps");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        ButterKnife.bind(this, view);
        if(savedInstanceState != null){
            int tt = savedInstanceState.getInt("show_ingredients");
            mIngredientsTV.setVisibility(tt);
        }

        StringBuilder ingredients = new StringBuilder();
        for (int i = 0; i < mIngredientList.length(); i++){
            JSONObject ingredient;
            try {
                ingredient = new JSONObject(mIngredientList.get(i).toString());
                ingredients.append(" - ")
                        .append(Measure.getMeasure(ingredient.getString("measure"),
                                ingredient.getDouble("quantity")))
                        .append(ingredient.getString("ingredient")).append(";\n");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mIngredientsTV.setText(ingredients);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mStepRV.setLayoutManager(linearLayoutManager);
        mStepRV.setHasFixedSize(true);
        StepAdapter mStepAdapter = new StepAdapter();
        mStepAdapter.setStepList(mStepList);
        mStepAdapter.setListener(this);
        mStepRV.setAdapter(mStepAdapter);

        mIngredientsLabelTV.setOnClickListener(this);
        mExpandIngIV.setOnClickListener(this);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onStepChosen(int stepIndex) {

        if(getResources().getBoolean(R.bool.isTablet)){
            RecipeDetailFragment recipeDetailFragment = null;
            try {
                recipeDetailFragment = RecipeDetailFragment
                        .newInstance(mStepList.getJSONObject(stepIndex));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_recipe_detail, recipeDetailFragment).commit();
        } else {
            mOnDetailToRecipeListener.onFragmentInteraction(stepIndex);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ingredients_label_tv || view.getId() == R.id.expand_ing_iv){
            if (mIngredientsTV.getVisibility() == View.GONE){
                mExpandIngIV.setImageResource(android.R.drawable.arrow_up_float);
                mIngredientsTV.setVisibility(View.VISIBLE);
            } else if (mIngredientsTV.getVisibility() == View.VISIBLE){
                mExpandIngIV.setImageResource(android.R.drawable.arrow_down_float);
                mIngredientsTV.setVisibility(View.GONE);
            }
        }
    }

    public void setListener(OnDetailToRecipeListener onDetailToRecipeListener){
        mOnDetailToRecipeListener = onDetailToRecipeListener;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("ingredients_visibility", mIngredientsTV.getVisibility());
    }
}
