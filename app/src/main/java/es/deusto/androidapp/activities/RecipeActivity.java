package es.deusto.androidapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import es.deusto.androidapp.R;
import es.deusto.androidapp.data.Recipe;
import es.deusto.androidapp.manager.UserPropertyManager;

public class RecipeActivity extends AppCompatActivity {

    private static final String TAG = RecipeActivity.class.getName();

    public static final String LIKES_CHILD = "likes";

    private Recipe recipe;

    private ImageView recipeImage;
    private ImageView likeIcon;
    private TextView recipeName;
    private TextView recipeCategory;
    private TextView recipeCountry;
    private TextView recipeIngredients;
    private TextView recipeDescription;

    private DatabaseReference mRecipeRef;
    private ValueEventListener mRecipeValueEventListener;
    private String recipeID;

    private DatabaseReference mLikeRef;
    private ValueEventListener mLikeValueEventListener;

    private boolean liked = false;

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private UserPropertyManager mUserPropertyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        initFirebaseAuth();

        recipeID = getIntent().getStringExtra("recipe");

        initFirebaseDatabaseReference();
        initFirebaseDatabaseMessageRefListener();

        recipeImage = findViewById(R.id.recipe_image);
        recipeName = findViewById(R.id.recipe_name);
        recipeCategory = findViewById(R.id.recipe_category);
        recipeCountry = findViewById(R.id.recipe_country);
        recipeIngredients = findViewById(R.id.recipe_ingredients);
        recipeDescription = findViewById(R.id.recipe_description);
        likeIcon = findViewById(R.id.like_icon);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mUserPropertyManager = UserPropertyManager.getInstance();

    }

    private void initFirebaseDatabaseMessageRefListener() {

        mRecipeRef = mRecipeRef.child(CreateRecipeActivity.RECIPES_CHILD).child(recipeID);

        mRecipeValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    recipe = dataSnapshot.getValue(Recipe.class);
                    recipe.setId(recipeID);
                    loadRecipe(recipe);
                    if (mFirebaseUser.getUid().equals(recipe.getCreator())) {
                        LinearLayout editingOptions = findViewById(R.id.editing_options);
                        editingOptions.setVisibility(View.VISIBLE);
                    }
                    checkLike();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mRecipeRef.addValueEventListener(mRecipeValueEventListener);

    }

    private void initFirebaseDatabaseReference() {
        mRecipeRef = FirebaseDatabase.getInstance().getReference();
        mLikeRef = FirebaseDatabase.getInstance().getReference();
    }


    private void loadRecipe (Recipe recipe) {

        if (recipe.getPicture() != null) {
            recipeImage.setImageBitmap(recipe.getPicture());
        }

        recipeName.setText(recipe.getName());
        recipeCategory.setText(recipe.getCategory());
        recipeCountry.setText(recipe.getCountry());
        recipeIngredients.setText(recipe.getIngredients());
        recipeDescription.setText(recipe.getDescription());

    }

    public void editRecipe (View view) {

        Intent intent = new Intent(this, CreateRecipeActivity.class);
        intent.putExtra("recipe", recipe.getId());
        startActivity(intent);
    }

    public void deleteRecipe (View view) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.deleting_recipe_title))
                .setMessage(getString(R.string.deleting_recipe_text))
                .setPositiveButton( getString(R.string.yes), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mRecipeRef.removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                Bundle params = new Bundle();
                                params.putString(FirebaseAnalytics.Param.ITEM_ID, recipe.getId());
                                params.putString("recipe_name", recipe.getName());
                                mFirebaseAnalytics.logEvent("delete_recipe", params);
                                Toast.makeText(getBaseContext(),"Recipe deleted", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

                    }

                })
                .setNegativeButton( getString(R.string.no), null)
                .show();
    }

    public void searchMap(View view) {

        Bundle params = new Bundle();
        params.putString("search_country", recipe.getCountry());
        params.putString(FirebaseAnalytics.Param.ITEM_ID, recipe.getId());
        mFirebaseAnalytics.logEvent("consult_map", params);

        Uri location = Uri.parse("geo:0,0?q=" + recipe.getCountry().replace(" ", "+"));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    public void goBack(View view) {
        finish();
    }

    public void shareRecipe(View view) {

        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "recipe");
        params.putString(FirebaseAnalytics.Param.ITEM_ID, recipe.getId());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, params);

        mUserPropertyManager.registerUserAsInfluencer(this);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + " " +  recipe.getName());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void checkLike() {

        if (mLikeValueEventListener != null) {
            mLikeRef.removeEventListener(mLikeValueEventListener);
        }

        mLikeRef = mLikeRef.child(LIKES_CHILD).child(mFirebaseUser.getUid()).child(recipe.getId());

        mLikeValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    likeIcon.setImageDrawable(getDrawable(R.drawable.ic_heart_full));
                    liked = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mLikeRef.addListenerForSingleValueEvent(mLikeValueEventListener);

    }

    public void likeRecipe(View view) {
        liked = !liked;
        final Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.ITEM_ID, recipe.getId());
        params.putString("recipe_category", recipe.getCategory());

        if (liked) {
            HashMap<String, Boolean> like = new HashMap<>();
            like.put("liked", true);

            mLikeRef.setValue(like).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    likeIcon.setImageDrawable(getDrawable(R.drawable.ic_heart_full));
                    mFirebaseAnalytics.logEvent("like_recipe", params);
                    if (recipe.getCategory().equals(getString(R.string.desserts))) {
                        mUserPropertyManager.registerUserAsPastryChef(RecipeActivity.this);
                    } else if (recipe.getCategory().equals(getString(R.string.meat))) {
                        mUserPropertyManager.registerUserAsMeatEating(RecipeActivity.this);
                    } else if (recipe.getCategory().equals(getString(R.string.fish))) {
                        mUserPropertyManager.incrementFishEatingUser();
                    } else if (recipe.getCategory().equals(getString(R.string.salads)) || recipe.getCategory().equals(getString(R.string.vegetables)) ) {
                        mUserPropertyManager.registerUserAsVeggie(RecipeActivity.this);
                    }
                }
            });


        } else {
            mLikeRef.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    likeIcon.setImageDrawable(getDrawable(R.drawable.ic_heart_border));
                    mFirebaseAnalytics.logEvent("dislike_recipe", params);
                    if (recipe.getCategory().equals(getString(R.string.desserts))) {
                        mUserPropertyManager.decrementPastryChefUser();
                    } else if (recipe.getCategory().equals(getString(R.string.meat))) {
                        mUserPropertyManager.decrementMeatEatingUser();
                    } else if (recipe.getCategory().equals(getString(R.string.fish))) {
                        mUserPropertyManager.decrementFishEatingUser();
                    } else if (recipe.getCategory().equals(getString(R.string.salads)) || recipe.getCategory().equals(getString(R.string.vegetables)) ) {
                        mUserPropertyManager.decrementVeggieEatingUser();
                    }
                }
            });

        }
    }

    private void initFirebaseAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onResume() {
        if (mRecipeValueEventListener == null) {
            initFirebaseDatabaseReference();
            initFirebaseDatabaseMessageRefListener();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mRecipeValueEventListener != null) {
            mRecipeRef.removeEventListener(mRecipeValueEventListener);
            mRecipeValueEventListener = null;
        }
        super.onPause();
    }


}
