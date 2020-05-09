package es.deusto.androidapp.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;

public class Recipe implements Parcelable {

    private int id;
    private String name;
    private String country;
    private String category;
    private String ingredients;
    private String description;
    private String creator;

    private Bitmap picture;

    public Recipe(String name, String country, String category, String ingredients, String description, String creator, Bitmap picture) {
        this.name = name;
        this.country = country;
        this.category = category;
        this.ingredients = ingredients;
        this.description = description;
        this.creator = creator;
        this.picture = picture;
    }

    public Recipe(int id, String name, String country, String category, String ingredients, String description, String creator, byte [] picture) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.category = category;
        this.ingredients = ingredients;
        this.description = description;
        this.creator = creator;
        BitmapFactory.decodeByteArray(picture, 0, picture.length);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public byte[] pictureAsBytes() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        getPicture().compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
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
        id = p.readInt();
        name = p.readString();
        country = p.readString();
        category = p.readString();
        ingredients = p.readString();
        description = p.readString();
        creator = p.readString();
        picture = p.readParcelable(Bitmap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeString(getName());
        dest.writeString(getCountry());
        dest.writeString(getCategory());
        dest.writeString(getIngredients());
        dest.writeString(getDescription());
        dest.writeString(getCreator());
        dest.writeParcelable(getPicture(), flags);
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
