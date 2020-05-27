package es.deusto.androidapp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import es.deusto.androidapp.R;
import es.deusto.androidapp.adapter.AllRecipeListAdapter;
import es.deusto.androidapp.data.Recipe;
import es.deusto.androidapp.manager.RecipeLoaderTask;


public class HomeFragment extends Fragment {

    private final ArrayList<Recipe> recipes = new ArrayList<>();
    private RecyclerView recyclerView;

    private FirebaseUser user;

    private AllRecipeListAdapter mAdapter;

    private ProgressBar progressBar;
    private TextView noRecipeText;

    private FirebaseAnalytics mFirebaseAnalytics;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance(FirebaseUser user) {
        HomeFragment fragment = new HomeFragment();
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
        recipes.clear();
        mAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.VISIBLE);
        new RecipeLoaderTask(getContext(), mAdapter, recipes, user, progressBar, noRecipeText, recyclerView, 2).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home,
                container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        noRecipeText = view.findViewById(R.id.no_recipe);

        mAdapter = new AllRecipeListAdapter(getContext(), recipes, recyclerView, noRecipeText);

        recyclerView.setAdapter(mAdapter);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        progressBar = view.findViewById(R.id.progress_bar);

        final EditText searchBar = view.findViewById(R.id.search_bar_text);

        searchBar.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    searchRecipe (searchBar.getText().toString());
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        return view;
    }

    private void searchRecipe (String searchText) {
        recipes.clear();
        mAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.VISIBLE);
        int option;
        if (searchText.trim().isEmpty()) {
            option = 2;
        } else {
            option = 3;
            Bundle params = new Bundle();
            params.putString(FirebaseAnalytics.Param.SEARCH_TERM, searchText);
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, params);
        }

        RecipeLoaderTask task = new RecipeLoaderTask(getContext(), mAdapter, recipes, user, progressBar, noRecipeText, recyclerView, option);
        task.setSearchRecipe(searchText);
        task.execute();
    }
}
