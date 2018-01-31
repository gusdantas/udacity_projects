package com.example.gustavohidalgo.bakingapp.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gustavohidalgo.bakingapp.R;
import com.example.gustavohidalgo.bakingapp.adapter.StepAdapter;
import com.example.gustavohidalgo.bakingapp.interfaces.OnDetailToRecipeListener;
import com.example.gustavohidalgo.bakingapp.interfaces.OnAdapterToDetailListener;
import com.example.gustavohidalgo.bakingapp.utils.Measure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDetailToRecipeListener} interface
 * to handle interaction events.
 * Use the {@link RecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeFragment extends Fragment implements OnAdapterToDetailListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String RECIPE = "recipe";

    // TODO: Rename and change types of parameters
    private String mRecipe;
    private TextView mIngredientsTV;
    private RecyclerView mStepRV;
    private StepAdapter mStepAdapter;
    private JSONObject mRecipeJson;
    private JSONArray mIngredientList, mStepList;

    private OnDetailToRecipeListener mListener;

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
            mRecipeJson = new JSONObject(mRecipe);
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
        mIngredientsTV = view.findViewById(R.id.ingredients_tv);
        mStepRV = view.findViewById(R.id.step_rv);

        StringBuilder ingredients = new StringBuilder();
        for (int i = 0; i < mIngredientList.length(); i++){
            JSONObject ingredient = null;
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
        mStepAdapter = new StepAdapter(getContext());
        mStepAdapter.setStepList(mStepList);
        mStepAdapter.setListener(this);
        mStepRV.setAdapter(mStepAdapter);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onStepChosen(int stepIndex) {
        if (mListener != null) {
            mListener.onFragmentInteraction(stepIndex);
        }
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
}
