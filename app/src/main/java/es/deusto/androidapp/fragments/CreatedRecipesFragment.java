package es.deusto.androidapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.deusto.androidapp.R;

import es.deusto.androidapp.activities.CreateRecipeActivity;
import es.deusto.androidapp.adapter.RecipeCreatedListAdapter;
import es.deusto.androidapp.data.Recipe;

public class CreatedRecipesFragment extends Fragment {

    private static final String TAG = CreatedRecipesFragment.class.getName();

    private final ArrayList<Recipe> recipesCreated = new ArrayList<>();
    private Map<String, Recipe> mRecipesMap = new HashMap<>();

    private RecyclerView recyclerView;
    private FirebaseUser user;

    private RecipeCreatedListAdapter mAdapter;

    private TextView noRecipeText;

    private FirebaseAnalytics mFirebaseAnalytics;
    private DatabaseReference mRecipesRef;
    private ChildEventListener mRecipesChildEventListener;
    private Query queryRef;

    public CreatedRecipesFragment() {
        // Required empty public constructor
    }

    public static CreatedRecipesFragment newInstance(FirebaseUser user) {
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
        initFirebaseDatabaseReference();
        initFirebaseDatabaseMessageRefListener();
    }

    @Override
    public void onResume() {
        if (queryRef == null) {
            recipesCreated.clear();
            mRecipesMap.clear();
            noRecipeText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            initFirebaseDatabaseReference();
            initFirebaseDatabaseMessageRefListener();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (queryRef != null) {
            queryRef.removeEventListener(mRecipesChildEventListener);
            queryRef = null;
        }
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_created_recipes,
                container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        noRecipeText = view.findViewById(R.id.no_recipe);

        mAdapter = new RecipeCreatedListAdapter(getContext(), recipesCreated, recyclerView, noRecipeText);

        recyclerView.setAdapter(mAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noRecipeText.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        mFirebaseAnalytics.logEvent("check_created_recipes", null);

        return view;
    }

    private void initFirebaseDatabaseMessageRefListener() {

        mRecipesRef = mRecipesRef.child(CreateRecipeActivity.RECIPES_CHILD);
        queryRef = mRecipesRef.orderByChild("creator").equalTo(user.getUid());
        mRecipesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot,
                                     @Nullable String s) {
                if (recipesCreated.size() == 0 ) {
                    noRecipeText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                recipe.setId(dataSnapshot.getKey());
                mRecipesMap.put(dataSnapshot.getKey(), recipe);
                recipesCreated.add(recipe);
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
                    recipesCreated.remove(messageToRemove);
                    mRecipesMap.remove(messageToRemove);
                    mAdapter.notifyDataSetChanged();
                    if (recipesCreated.size() == 0) {
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

        queryRef.addChildEventListener(mRecipesChildEventListener);

    }

    private void initFirebaseDatabaseReference() {
        recipesCreated.clear();
        mRecipesMap.clear();
        mRecipesRef = FirebaseDatabase.getInstance().getReference();
    }


}
