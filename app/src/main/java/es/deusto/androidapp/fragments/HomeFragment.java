package es.deusto.androidapp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.deusto.androidapp.R;
import es.deusto.androidapp.activities.CreateRecipeActivity;
import es.deusto.androidapp.adapter.AllRecipeListAdapter;
import es.deusto.androidapp.data.Recipe;
import es.deusto.androidapp.manager.RecipeLoaderTask;


public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getName();

    private final ArrayList<Recipe> recipes = new ArrayList<>();
    private Map<String, Recipe> mRecipesMap = new HashMap<>();
    private RecyclerView recyclerView;

    private FirebaseUser user;

    private AllRecipeListAdapter mAdapter;

    private ProgressBar progressBar;
    private TextView noRecipeText;

    private FirebaseAnalytics mFirebaseAnalytics;

    private DatabaseReference mRecipesRef;
    private ChildEventListener mRecipesChildEventListener;
    private DatabaseReference mFirebaseDatabaseRef;

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
        initFirebaseDatabaseReference();
        initFirebaseDatabaseMessageRefListener();
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
        progressBar.setVisibility(View.GONE);
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

    @Override
    public void onResume() {
        super.onResume();
        noRecipeText.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        if (mRecipesRef != null) {
            recipes.clear();
            mRecipesMap.clear();
            mRecipesRef.addChildEventListener(mRecipesChildEventListener);
        }
    }

    @Override
    public void onPause() {
        if (mRecipesRef != null) {
            mRecipesRef.removeEventListener(mRecipesChildEventListener);
        }
        super.onPause();
    }


    private void initFirebaseDatabaseMessageRefListener() {
        mRecipesRef = mFirebaseDatabaseRef.child(CreateRecipeActivity.RECIPES_CHILD);
        mRecipesChildEventListener = new ChildEventListener() {@Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot,
                                 @Nullable String s) {
                if (recipes.size() == 0 ) {
                    noRecipeText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                mRecipesMap.put(dataSnapshot.getKey(), recipe);
                recipes.add(recipe);
                mAdapter.notifyDataSetChanged();
        }
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot,
                                   @Nullable String s) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                if (mRecipesMap.containsKey(dataSnapshot.getKey())) {
                    Recipe updatedRecipe =
                            dataSnapshot.getValue(Recipe.class);
                    Recipe recipeToUpdate =
                            mRecipesMap.get(dataSnapshot.getKey());
                    if (updatedRecipe != null && recipeToUpdate != null) {
                        //TODO: Update picture
                        recipeToUpdate.setName(updatedRecipe.getName());
                        mAdapter.notifyDataSetChanged();
                    }
                }
        }
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
            if (mRecipesMap.containsKey(dataSnapshot.getKey())) {
                Recipe messageToRemove = mRecipesMap.get(dataSnapshot.getKey());
                recipes.remove(messageToRemove);
                mRecipesMap.remove(messageToRemove);
                mAdapter.notifyDataSetChanged();
                if (recipes.size() == 0) {
                    noRecipeText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
            }
        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot,
                                 @Nullable String s) { Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d(TAG, "onCancelled:" + databaseError.getMessage()); }
        };

        mRecipesRef.addChildEventListener(mRecipesChildEventListener);

    }

    private void initFirebaseDatabaseReference() {
        mFirebaseDatabaseRef = FirebaseDatabase.getInstance().getReference();
    }

}
