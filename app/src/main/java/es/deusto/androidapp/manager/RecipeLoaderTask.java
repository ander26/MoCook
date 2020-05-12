package es.deusto.androidapp.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.deusto.androidapp.data.Recipe;
import es.deusto.androidapp.data.User;

public class RecipeLoaderTask extends AsyncTask<Void, Void, List<Recipe>> {

    private RecyclerView.Adapter adapter;
    private List<Recipe> recipes;
    private SQLiteManager sqlite;
    private User user;
    private ProgressBar progressBar;
    private TextView noRecipeText;
    private RecyclerView recyclerView;
    private int option;

    private String searchRecipe = "";

    public RecipeLoaderTask(Context context, RecyclerView.Adapter adapter, List<Recipe> recipes, User user, ProgressBar progressBar, TextView noRecipeText, RecyclerView recyclerView, int option) {
        this.adapter = adapter;
        this.recipes = recipes;
        this.user = user;
        this.progressBar = progressBar;
        this.noRecipeText = noRecipeText;
        this.recyclerView = recyclerView;
        this.option = option;
        sqlite = new SQLiteManager(context);
    }

    @Override
    protected List<Recipe> doInBackground(Void... params) {

        List<Recipe> data = new ArrayList<>();
        switch (option) {
            case 0:
                data = sqlite.retrieveAllRecipesLikesUser(user.getUsername());
                break;
            case 1:
                data = sqlite.retrieveAllRecipesCreator(user.getUsername());
                break;
            case 2:
                data = sqlite.retrieveAllRecipes();
                break;
            case 3:
                data = sqlite.searchRecipe(searchRecipe);
                break;
        }

        return data;
    }


    @Override
    protected void onPostExecute(List<Recipe> data) {
        this.recipes.addAll(data);
        this.progressBar.setVisibility(View.GONE);
        this.adapter.notifyDataSetChanged();
        if (this.recipes.size() == 0) {
            noRecipeText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noRecipeText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void setSearchRecipe(String searchRecipe) {
        this.searchRecipe = searchRecipe;
    }
}
