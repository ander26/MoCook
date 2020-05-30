package es.deusto.androidapp.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import es.deusto.androidapp.data.Recipe;

public class SQLiteManager extends SQLiteOpenHelper {

    // Database name and version
    private static final String DATABASE_NAME = "mocook.db";
    private static final int DATABASE_VERSION = 1;

    // Table and columns names

    private static final String TABLE_RECIPES = "recipes";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_COUNTRY = "country";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_INGREDIENTS = "ingredients";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_CREATOR = "creator";
    private static final String COLUMN_IMAGE = "image";

    private static final String TABLE_LIKES = "likes";
    private static final String COLUMN_RECIPE = "recipe";

    // SQL sentence to create the tables
    private static final String CREATE_TABLE_RECIPES = "create table "
            + TABLE_RECIPES + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_NAME + " text not null, " +
            COLUMN_COUNTRY + " text not null, " +
            COLUMN_CATEGORY + " text not null, " +
            COLUMN_INGREDIENTS + " text not null, " +
            COLUMN_DESCRIPTION + " text not null, " +
            COLUMN_CREATOR + " text not null, " +
            COLUMN_IMAGE + " blob  " +
            ");";


    private static final String CREATE_TABLE_LIKES = "create table "
            + TABLE_LIKES + "(" +
            "username" + " text not null, " +
            COLUMN_RECIPE + " integer not null, " +
            "PRIMARY KEY (" + COLUMN_RECIPE + ", " + "username" + ")," +
            "FOREIGN KEY (" + COLUMN_RECIPE + ") " +
            "       REFERENCES " + TABLE_RECIPES + " (" + COLUMN_ID + ") " +
            ");";

    public SQLiteManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_RECIPES);
        db.execSQL(CREATE_TABLE_LIKES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("MoCookDatabase", "Upgrading database from version " + oldVersion + " to " + newVersion + ", deleting old data, creating empty table.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIKES);
        onCreate(db);
    }

    public void storeRecipe(Recipe recipe){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, recipe.getName());
        values.put(COLUMN_COUNTRY, recipe.getCountry());
        values.put(COLUMN_CATEGORY, recipe.getCategory());
        values.put(COLUMN_INGREDIENTS, recipe.getIngredients());
        values.put(COLUMN_DESCRIPTION, recipe.getDescription());
        values.put(COLUMN_CREATOR, recipe.getCreator());
        if (recipe.getPicture() != null) {
            values.put(COLUMN_IMAGE, recipe.pictureAsBytes());
        }
        db.insert(TABLE_RECIPES, null, values);
    }


    public long storeLike(String username, int recipeID){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("username", username);
        values.put(COLUMN_RECIPE, recipeID);

        return db.insert(TABLE_LIKES, null, values);
    }

    public void deleteLike(String username, int recipeID){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_LIKES, "recipe = " + recipeID + " and username = '" + username + "'", null);
    }

    public void deleteRecipe(Recipe recipe){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_RECIPES, "_id = " + recipe.getId(), null);
    }

    public void deleteAllRecipes(){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_RECIPES, null, null);
    }


    public void updateRecipe(Recipe recipe){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, recipe.getName());
        values.put(COLUMN_COUNTRY, recipe.getCountry());
        values.put(COLUMN_CATEGORY, recipe.getCategory());
        values.put(COLUMN_INGREDIENTS, recipe.getIngredients());
        values.put(COLUMN_DESCRIPTION, recipe.getDescription());
        values.put(COLUMN_CREATOR, recipe.getCreator());

        if (recipe.getPicture()!= null) {
            values.put(COLUMN_IMAGE, recipe.pictureAsBytes());
        }


        db.update(TABLE_RECIPES, values, "_id = " + recipe.getId(), null);
    }


    public boolean checkLike(String username, int recipeID){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_LIKES,
                null,
                "username = '" + username + "' and recipe = " + recipeID,
                null,
                null,
                null,
                null);

        cursor.moveToNext();
        while(!cursor.isAfterLast()){
           return true;
        }
        return false;
    }

    public ArrayList<Recipe> searchRecipe(String searchText){
        ArrayList<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_RECIPES + " WHERE " + COLUMN_NAME + " LIKE '%" + searchText + "%' OR " + COLUMN_INGREDIENTS + " LIKE '%" + searchText + "%' OR " + COLUMN_DESCRIPTION + " LIKE '%" + searchText + "%'";

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToNext();
        while(!cursor.isAfterLast()){
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            String country = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COUNTRY));
            String categoryDB = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
            String ingredients = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENTS));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            String creatorDB = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATOR));
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));

            byte [] image = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));

            recipes.add(new Recipe(id + "", name, country, categoryDB, ingredients, description, creatorDB, image));

            cursor.moveToNext();
        }
        return recipes;
    }

    public ArrayList<Recipe> retrieveAllRecipes(){
        ArrayList<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                null);

        cursor.moveToNext();
        while(!cursor.isAfterLast()){
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            String country = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COUNTRY));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
            String ingredients = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENTS));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            String creator = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATOR));
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));

            byte [] image = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));

            recipes.add(new Recipe(id + "", name, country, category, ingredients, description, creator, image));

            cursor.moveToNext();
        }
        return recipes;
    }

    public ArrayList<Recipe> retrieveRecipeID(int id){
        ArrayList<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_RECIPES,
                null,
                "_id = " + id,
                null,
                null,
                null,
                null);

        cursor.moveToNext();
        while(!cursor.isAfterLast()){
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            String country = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COUNTRY));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
            String ingredients = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENTS));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            String creatorDB = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATOR));
            int idDB = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));

            byte [] image = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));

            recipes.add(new Recipe(idDB + "", name, country, category, ingredients, description, creatorDB, image));

            cursor.moveToNext();
        }
        return recipes;
    }

    public ArrayList<Recipe> retrieveAllRecipesCreator(String creator){
        ArrayList<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String [] args = {creator};
        Cursor cursor = db.query(
                TABLE_RECIPES,
                null,
                "creator = ?",
                args,
                null,
                null,
                null);

        cursor.moveToNext();
        while(!cursor.isAfterLast()){
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            String country = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COUNTRY));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
            String ingredients = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENTS));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            String creatorDB = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATOR));
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));

            byte [] image = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));

            recipes.add(new Recipe(id + "", name, country, category, ingredients, description, creatorDB, image));

            cursor.moveToNext();
        }
        return recipes;
    }

    public ArrayList<Recipe> retrieveAllRecipesCategory(String category){
        ArrayList<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String [] args = {category};
        Cursor cursor = db.query(
                TABLE_RECIPES,
                null,
                "category = ?",
                args,
                null,
                null,
                null);

        cursor.moveToNext();
        while(!cursor.isAfterLast()){
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            String country = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COUNTRY));
            String categoryDB = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
            String ingredients = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENTS));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            String creatorDB = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATOR));
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));

            byte [] image = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));

            recipes.add(new Recipe(id + "", name, country, categoryDB, ingredients, description, creatorDB, image));

            cursor.moveToNext();
        }
        return recipes;
    }

    public ArrayList<Recipe> retrieveAllRecipesLikesUser(String username){
        ArrayList<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_LIKES + " L, " + TABLE_RECIPES + " R WHERE L.RECIPE = R._ID AND L.USERNAME = ? ";
        String [] args = {username};
        Cursor cursor = db.rawQuery(query, args);

        cursor.moveToNext();
        while(!cursor.isAfterLast()){
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            String country = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COUNTRY));
            String categoryDB = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
            String ingredients = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENTS));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            String creatorDB = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATOR));
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));

            byte [] image = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));

            recipes.add(new Recipe(id + "", name, country, categoryDB, ingredients, description, creatorDB, image));

            cursor.moveToNext();
        }
        return recipes;
    }
}
