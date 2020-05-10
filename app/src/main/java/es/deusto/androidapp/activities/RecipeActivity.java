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


import es.deusto.androidapp.R;
import es.deusto.androidapp.data.Recipe;
import es.deusto.androidapp.data.User;
import es.deusto.androidapp.manager.SQLiteManager;

public class RecipeActivity extends AppCompatActivity {

    private User user;
    private Recipe recipe;

    private SQLiteManager sqlite;

    private ImageView recipeImage;
    private TextView recipeName;
    private TextView recipeCategory;
    private TextView recipeCountry;
    private TextView recipeIngredients;
    private TextView recipeDescription;

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
            editingOptions.setVisibility(View.GONE);
        }

        recipeImage = findViewById(R.id.recipe_image);
        recipeName = findViewById(R.id.recipe_name);
        recipeCategory = findViewById(R.id.recipe_category);
        recipeCountry = findViewById(R.id.recipe_country);
        recipeIngredients = findViewById(R.id.recipe_ingredients);
        recipeDescription = findViewById(R.id.recipe_description);

        loadRecipe(recipe);

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
                        sqlite.deleteRecipe(recipe);
                        finish();
                    }

                })
                .setNegativeButton( getString(R.string.no), null)
                .show();
    }

    public void searchMap(View view) {

        Uri location = Uri.parse("geo:0,0?q=" + recipe.getCountry().replace(" ", "+"));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }
}
