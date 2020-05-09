package es.deusto.androidapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.deusto.androidapp.R;

public class CreatedRecipesFragment extends Fragment {

    public CreatedRecipesFragment() {
        // Required empty public constructor
    }

    public static CreatedRecipesFragment newInstance() {
        CreatedRecipesFragment fragment = new CreatedRecipesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_created_recipes, container, false);
    }
}
