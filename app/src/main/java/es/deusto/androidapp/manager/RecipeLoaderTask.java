package es.deusto.androidapp.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import es.deusto.androidapp.adapter.RecyclerViewRecipeListAdapter;
import es.deusto.androidapp.data.Recipe;
import es.deusto.androidapp.data.User;

public class RecipeLoaderTask extends AsyncTask<Void, Void, List<Recipe>> {

    private Context context;
    private RecyclerViewRecipeListAdapter adapter;
    private List<Recipe> recipes;
    private SQLiteManager sqlite;
    private User user;
    private ProgressBar progressBar;

    public RecipeLoaderTask(Context context, RecyclerViewRecipeListAdapter adapter, List<Recipe> recipes, User user, ProgressBar progressBar) {
        this.context = context;
        this.adapter = adapter;
        this.recipes = recipes;
        this.user = user;
        this.progressBar = progressBar;

        sqlite = new SQLiteManager(context);
    }

    @Override
    protected List<Recipe> doInBackground(Void... params) {

        List<Recipe> data = sqlite.retrieveAllRecipesLikesUser(user.getUsername());

        return data;
    }


    @Override
    protected void onPostExecute(List<Recipe> data) {
        this.recipes.addAll(data);
        this.progressBar.setVisibility(View.GONE);
        this.adapter.notifyDataSetChanged();
    }
}
