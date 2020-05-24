package es.deusto.androidapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

public class RecipeCreatedListAdapter extends RecyclerView.Adapter <RecipeCreatedListAdapter.RecipeCreatedViewHolder> {

    private final ArrayList<Recipe> recipes;
    private final User user;
    private final Context context;
    private final LayoutInflater mInflater;

    private final TextView noRecipeText;
    private final RecyclerView recyclerView;

    class RecipeCreatedViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private ImageView recipeImage;
        private TextView recipeName;

        public RecipeCreatedViewHolder(View itemView, RecipeCreatedListAdapter adapter) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            recipeName = itemView.findViewById(R.id.recipe_name);

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

    public RecipeCreatedListAdapter(Context context, User user, ArrayList<Recipe> recipes, RecyclerView recyclerView, TextView noRecipeText) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.recipes = recipes;
        this.user = user;
        this.recyclerView = recyclerView;
        this.noRecipeText = noRecipeText;
    }

    @Override
    public RecipeCreatedViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // Inflate an item view.
        View mItemView = mInflater.inflate(
                R.layout.item_recipe_row, parent, false);
        return new RecipeCreatedViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(RecipeCreatedViewHolder holder,
                                 int position) {

        // Retrieve the data for that position.
        String name = recipes.get(position).getName();
        Bitmap image = recipes.get(position).getPicture();

        // Add the data to the view holder.
        holder.recipeName.setText(name);
        if (image == null) {
            holder.recipeImage.setImageDrawable(context.getDrawable(R.drawable.loader_background));
        } else {
            holder.recipeImage.setImageBitmap(image);
        }
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
}
