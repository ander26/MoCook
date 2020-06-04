package es.deusto.androidapp.data;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Recipe implements Parcelable {

    private String id;
    private String name;
    private String country;
    private String category;
    private String ingredients;
    private String description;
    private String creator;

    private String picture;

    public Recipe() {}

    public Recipe(String name, String country, String category, String ingredients, String description, String creator) {
        this.name = name;
        this.country = country;
        this.category = category;
        this.ingredients = ingredients;
        this.description = description;
        this.creator = creator;
    }

    public Recipe(String name, String country, String category, String ingredients, String description, String creator, String picture) {
        this.name = name;
        this.country = country;
        this.category = category;
        this.ingredients = ingredients;
        this.description = description;
        this.creator = creator;
        this.picture = picture;
    }

    public Recipe(String id, String name, String country, String category, String ingredients, String description, String creator, String picture) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.category = category;
        this.ingredients = ingredients;
        this.description = description;
        this.creator = creator;
        this.picture = picture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @NonNull
    @Override
    public String toString() {
        String recipe = "Name: " + this.name + "\n";
        recipe = recipe +  "Country: " + this.country + "\n";
        recipe = recipe +  "Category: " + this.category + "\n";
        recipe = recipe +  "Ingredients: " + this.ingredients + "\n";
        recipe = recipe +  "Description: " + this.description + "\n";
        recipe = recipe +  "Creator: " + this.creator + "\n";
        recipe = recipe +  "Id: " + this.id + "\n";
        return recipe;
    }

    public Recipe (Parcel p){
        id = p.readString();
        name = p.readString();
        country = p.readString();
        category = p.readString();
        ingredients = p.readString();
        description = p.readString();
        creator = p.readString();
        picture = p.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getName());
        dest.writeString(getCountry());
        dest.writeString(getCategory());
        dest.writeString(getIngredients());
        dest.writeString(getDescription());
        dest.writeString(getCreator());
        dest.writeString(getPicture());
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>(){
        @Override
        public Recipe createFromParcel(Parcel parcel) {
            return new Recipe(parcel);
        }

        @Override
        public Recipe[] newArray(int s) {
            return new Recipe[s];
        }
    };
}
