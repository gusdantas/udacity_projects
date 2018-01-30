package com.example.gustavohidalgo.bakingapp.interfaces;

import android.net.Uri;

import org.json.JSONObject;

/**
 * Created by gustavo.hidalgo on 18/01/30.
 */
/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */
public interface OnFragmentInteractionListener {
    void onFragmentInteraction(JSONObject jsonObject);
}
