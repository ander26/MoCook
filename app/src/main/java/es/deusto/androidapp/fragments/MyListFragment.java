package es.deusto.androidapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.deusto.androidapp.R;
import es.deusto.androidapp.activities.CreateRecipeActivity;
import es.deusto.androidapp.activities.RecipeActivity;
import es.deusto.androidapp.adapter.RecipeLikesListAdapter;
import es.deusto.androidapp.data.Recipe;

public class MyListFragment extends Fragment {

    private static final String TAG = MyListFragment.class.getName();

    private final ArrayList<Recipe> recipesLiked = new ArrayList<>();
    private Map<String, Recipe> mRecipesMap = new HashMap<>();

    private RecyclerView recyclerView;
    private FirebaseUser user;
    private RecipeLikesListAdapter mAdapter;
    private TextView noRecipeText;

    private DatabaseReference mRecipesRef;
    private ChildEventListener mLikesChildEventListener;

    public MyListFragment() {
        // Required empty public constructor
    }

    public static MyListFragment newInstance(FirebaseUser user) {
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

        initFirebaseDatabaseReference();
        initFirebaseDatabaseMessageRefListener();
    }

    @Override
    public void onResume() {
        if (mLikesChildEventListener == null) {
            recipesLiked.clear();
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
        if (mLikesChildEventListener != null) {
            mRecipesRef.removeEventListener(mLikesChildEventListener);
            mLikesChildEventListener = null;
        }
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_list,
                container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        noRecipeText = view.findViewById(R.id.no_recipe);

        mAdapter = new RecipeLikesListAdapter(getContext(), user, recipesLiked, recyclerView, noRecipeText);

        recyclerView.setAdapter(mAdapter);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        noRecipeText.setVisibility(View.VISIBLE);

        return view;
    }

    private void initFirebaseDatabaseMessageRefListener() {

        mRecipesRef = mRecipesRef.child(RecipeActivity.LIKES_CHILD).child(user.getUid());
        mLikesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot,
                                     @Nullable String s) {
                if (recipesLiked.size() == 0 ) {
                    noRecipeText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                FirebaseDatabase.getInstance().getReference().
                        child(CreateRecipeActivity.RECIPES_CHILD).child(dataSnapshot.getKey()).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Recipe recipe = dataSnapshot.getValue(Recipe.class);
                        recipe.setId(dataSnapshot.getKey());
                        mRecipesMap.put(dataSnapshot.getKey(), recipe);
                        recipesLiked.add(recipe);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot,
                                       @Nullable String s) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                if (mRecipesMap.containsKey(dataSnapshot.getKey())) {
                    Recipe messageToRemove = mRecipesMap.get(dataSnapshot.getKey());
                    recipesLiked.remove(messageToRemove);
                    mRecipesMap.remove(messageToRemove);
                    mAdapter.notifyDataSetChanged();
                    if (recipesLiked.size() == 0) {
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

        mRecipesRef.addChildEventListener(mLikesChildEventListener);

    }

    private void initFirebaseDatabaseReference() {
        recipesLiked.clear();
        mRecipesMap.clear();
        mRecipesRef = FirebaseDatabase.getInstance().getReference();
    }
}
