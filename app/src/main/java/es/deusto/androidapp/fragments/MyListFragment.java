package es.deusto.androidapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

import es.deusto.androidapp.R;
import es.deusto.androidapp.adapter.RecyclerViewRecipeListAdapter;
import es.deusto.androidapp.data.Recipe;
import es.deusto.androidapp.data.User;
import es.deusto.androidapp.manager.RecipeLoaderTask;

public class MyListFragment extends Fragment {

    private final ArrayList<Recipe> recipesLiked = new ArrayList<>();

    private RecyclerView recyclerView;
    private User user;
    private RecyclerViewRecipeListAdapter mAdapter;
    private ProgressBar progressBar;

    public MyListFragment() {
        // Required empty public constructor
    }

    public static MyListFragment newInstance(User user) {
        MyListFragment fragment = new MyListFragment();
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
        recipesLiked.clear();
        mAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.VISIBLE);
        new RecipeLoaderTask(getContext(), mAdapter, recipesLiked, user, progressBar).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_list,
                container, false);

        recyclerView = view.findViewById(R.id.recycler_view);

        mAdapter = new RecyclerViewRecipeListAdapter(getContext(), user, recipesLiked);

        recyclerView.setAdapter(mAdapter);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        progressBar = view.findViewById(R.id.progress_bar);

        return view;
    }
}
