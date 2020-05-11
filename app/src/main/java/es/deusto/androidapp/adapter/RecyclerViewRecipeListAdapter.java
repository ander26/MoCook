package es.deusto.androidapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import es.deusto.androidapp.R;
import es.deusto.androidapp.activities.RecipeActivity;
import es.deusto.androidapp.data.Recipe;
import es.deusto.androidapp.data.User;
import es.deusto.androidapp.manager.SQLiteManager;

public class RecyclerViewRecipeListAdapter extends RecyclerView.Adapter <RecyclerViewRecipeListAdapter.RecipeViewHolder> {

    private final ArrayList<Recipe> recipes;
    private final User user;
    private final Context context;
    private final LayoutInflater mInflater;
    private SQLiteManager sqlite;

    class RecipeViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private ImageView recipeImage;
        private TextView recipeName;

        private ImageView heartIcon;

        public RecipeViewHolder(View itemView, RecyclerViewRecipeListAdapter adapter) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            recipeName = itemView.findViewById(R.id.recipe_name);
            heartIcon = itemView.findViewById(R.id.like_icon);

            heartIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    sqlite.deleteLike(user.getUsername(), recipes.get(position).getId());
                    recipes.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, recipes.size());

                }
            });

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {

            int position = getLayoutPosition();

            int recipeID = recipes.get(position).getId();

            Intent intent = new Intent(context, RecipeActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("recipe", recipeID);
            context.startActivity(intent);

        }
    }

    public RecyclerViewRecipeListAdapter (Context context, User user,  ArrayList<Recipe> recipes) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.recipes = recipes;
        this.user = user;

        sqlite = new SQLiteManager(context);
    }

    @Override
    public RecyclerViewRecipeListAdapter.RecipeViewHolder onCreateViewHolder(ViewGroup parent,
                                                                             int viewType) {
        // Inflate an item view.
        View mItemView = mInflater.inflate(
                R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(RecyclerViewRecipeListAdapter.RecipeViewHolder holder,
                                 int position) {

        // Retrieve the data for that position.
        String name = recipes.get(position).getName();
        Bitmap image = recipes.get(position).getPicture();

        // Add the data to the view holder.
        holder.recipeName.setText(name);
        holder.recipeImage.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }


}
