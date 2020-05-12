package es.deusto.androidapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import es.deusto.androidapp.R;

import es.deusto.androidapp.adapter.RecipeCreatedListAdapter;
import es.deusto.androidapp.data.Recipe;
import es.deusto.androidapp.data.User;
import es.deusto.androidapp.manager.RecipeLoaderTask;

public class CreatedRecipesFragment extends Fragment {

    private final ArrayList<Recipe> recipesCreated = new ArrayList<>();

    private RecyclerView recyclerView;
    private User user;

    private RecipeCreatedListAdapter mAdapter;

    private ProgressBar progressBar;
    private TextView noRecipeText;

    public CreatedRecipesFragment() {
        // Required empty public constructor
    }

    public static CreatedRecipesFragment newInstance(User user) {
        CreatedRecipesFragment fragment = new CreatedRecipesFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        recipesCreated.clear();
        mAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.VISIBLE);
        new RecipeLoaderTask(getContext(), mAdapter, recipesCreated, user, progressBar, noRecipeText, recyclerView, 1).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_created_recipes,
                container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        noRecipeText = view.findViewById(R.id.no_recipe);

        mAdapter = new RecipeCreatedListAdapter(getContext(), user, recipesCreated, recyclerView, noRecipeText);

        recyclerView.setAdapter(mAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressBar = view.findViewById(R.id.progress_bar);

        return view;
    }
}
