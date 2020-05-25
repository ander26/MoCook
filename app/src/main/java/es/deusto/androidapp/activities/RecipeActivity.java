package es.deusto.androidapp.activities;

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


import com.google.firebase.analytics.FirebaseAnalytics;

import es.deusto.androidapp.R;
import es.deusto.androidapp.data.Recipe;
import es.deusto.androidapp.data.User;
import es.deusto.androidapp.manager.SQLiteManager;

public class RecipeActivity extends AppCompatActivity {

    private User user;
    private Recipe recipe;

    private SQLiteManager sqlite;

    private ImageView recipeImage;
    private ImageView likeIcon;
    private TextView recipeName;
    private TextView recipeCategory;
    private TextView recipeCountry;
    private TextView recipeIngredients;
    private TextView recipeDescription;

    private boolean liked = false;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onResume(){
        super.onResume();
        recipe = sqlite.retrieveRecipeID(recipe.getId()).get(0);
        loadRecipe(recipe);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        sqlite = new SQLiteManager(this);

        user = getIntent().getParcelableExtra("user");
        int recipeID = getIntent().getIntExtra("recipe", 0);

        recipe = sqlite.retrieveRecipeID(recipeID).get(0);

        if (!user.getUsername().equals(recipe.getCreator())) {
            LinearLayout editingOptions = findViewById(R.id.editing_options);
            editingOptions.setVisibility(View.INVISIBLE);
        }

        recipeImage = findViewById(R.id.recipe_image);
        recipeName = findViewById(R.id.recipe_name);
        recipeCategory = findViewById(R.id.recipe_category);
        recipeCountry = findViewById(R.id.recipe_country);
        recipeIngredients = findViewById(R.id.recipe_ingredients);
        recipeDescription = findViewById(R.id.recipe_description);
        likeIcon = findViewById(R.id.like_icon);


        loadRecipe(recipe);

        checkLike();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

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
        intent.putExtra("user", user);
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

                        Bundle params = new Bundle();
                        params.putString(FirebaseAnalytics.Param.ITEM_ID, Integer.toString(recipe.getId()));
                        params.putString("recipe_name", recipe.getName());
                        mFirebaseAnalytics.logEvent("delete_recipe", params);

                        sqlite.deleteRecipe(recipe);
                        finish();
                    }

                })
                .setNegativeButton( getString(R.string.no), null)
                .show();
    }

    public void searchMap(View view) {

        Bundle params = new Bundle();
        params.putString("search_country", recipe.getCountry());
        params.putString(FirebaseAnalytics.Param.ITEM_ID, Integer.toString(recipe.getId()));
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
        params.putString(FirebaseAnalytics.Param.ITEM_ID, Integer.toString(recipe.getId()));
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, params);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + " " +  recipe.getName());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void checkLike() {
        boolean likedDB = sqlite.checkLike(user.getUsername(), recipe.getId());

        if (likedDB) {
            likeIcon.setImageDrawable(getDrawable(R.drawable.ic_heart_full));
            liked = true;
        }
    }

    public void likeRecipe(View view) {
        liked = !liked;
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.ITEM_ID, Integer.toString(recipe.getId()));
        params.putString("recipe_category", recipe.getCategory());

        if (liked) {
            sqlite.storeLike(user.getUsername(), recipe.getId());
            likeIcon.setImageDrawable(getDrawable(R.drawable.ic_heart_full));
            mFirebaseAnalytics.logEvent("like_recipe", params);
        } else {
            sqlite.deleteLike(user.getUsername(), recipe.getId());
            likeIcon.setImageDrawable(getDrawable(R.drawable.ic_heart_border));
            mFirebaseAnalytics.logEvent("dislike_recipe", params);
        }
    }
}
