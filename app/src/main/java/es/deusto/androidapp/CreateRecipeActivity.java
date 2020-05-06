package es.deusto.androidapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.textfield.TextInputLayout;

import java.io.InputStream;

public class CreateRecipeActivity extends AppCompatActivity {

    private static final String[] COUNTRIES = new String[] {"Meat", "Fish", "Desserts", "Salads"};

    private ImageView recipeImage;
    private AutoCompleteTextView dropdown;

    private TextInputLayout inputName;
    private TextInputLayout inputCountry;
    private TextInputLayout inputIngredients;
    private TextInputLayout inputDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        ArrayAdapter <String> adapter = new ArrayAdapter <String> (this,
                R.layout.dropdown_item,
                COUNTRIES);

        dropdown = findViewById(R.id.dropdown);
        recipeImage = findViewById(R.id.recipe_image);
        inputName = findViewById(R.id.recipe_name_input);
        inputCountry = findViewById(R.id.recipe_country_input);
        inputIngredients = findViewById(R.id.recipe_ingredients_input);
        inputDescription = findViewById(R.id.recipe_description_input);

        dropdown.setAdapter(adapter);

        dropdown.setKeyListener(null);

        dropdown.setText(COUNTRIES[0], false);


    }

    public void selectImage(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Choose source for the image");

        if (checkCameraHardware()) {
            final String [] options = {"Take Photo", "Choose from Gallery", "Load picture from URL", "Cancel"};

            builder.setItems(options, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int item) {

                    if (options[item].equals("Take Photo")) {

                        if (ContextCompat.checkSelfPermission(CreateRecipeActivity.this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                           Log.i("RECIPE", "PERMISSION NOT GRANTED");
                            ActivityCompat.requestPermissions(CreateRecipeActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    0);
                        } else {
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 0);
                        }


                    } else if (options[item].equals("Choose from Gallery")) {

                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 1);

                    } else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    } else if (options[item].equals("Load picture from URL")) {
                        showTextDialog();
                    }
                }
            });
        } else {

            final String [] options = {"Choose from Gallery", "Load picture from URL", "Cancel"};

            builder.setItems(options, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals("Choose from Gallery")) {

                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 1);

                    } else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    } else if (options[item].equals("Load picture from URL")) {
                        showTextDialog();
                    }
                }
            });

        }

        builder.show();
    }

    private boolean checkCameraHardware() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            return true;
        } else {
            return false;
        }
    }

    public void goBack(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        recipeImage.setImageBitmap(selectedImage);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {

                        try {
                             Uri imageUri = data.getData();
                             InputStream imageStream = getContentResolver().openInputStream(imageUri);
                             Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            recipeImage.setImageBitmap(selectedImage);
                        } catch (Exception e) {
                            showDialog("Error", "There was an error obtaining the image");
                        }

                    }
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                }

                return;
            }

        }
    }

    private void showDialog (String title, String text) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);

        builder.setMessage(text);

        builder.show();

    }

    private void showTextDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Obtaining image from URL");

        builder.setMessage("Introduce the URL of the picture you want to add:");

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("URL");

        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 60;
        params.rightMargin = 60;
        input.setLayoutParams(params);

        container.addView(input);

        builder.setView(container);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String imageURL = input.getText().toString();

                Glide.with(CreateRecipeActivity.this)
                        .asBitmap()
                        .load(imageURL)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                recipeImage.setImageBitmap(resource);
                            }
                            @Override
                            public void onLoadCleared( Drawable placeholder) {
                            }

                            @Override
                            public void onLoadFailed ( Drawable errorDrawable) {
                                showDialog("Error", "Error trying to retrieve the image, check the URL is correct");
                            }
                        });

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    private Boolean validateInput (TextInputLayout input) {
        String name = input.getEditText().getText().toString();

        if (name.trim().isEmpty()) {
            input.setError(getString(R.string.field_empty));
            return false;
        } else {
            input.setError(null);
            input.setErrorEnabled(false);
        }
        return true;
    }



    public void createRecipe(View view) {

        if (!validateInput(inputName) | !validateInput(inputCountry) | !validateInput(inputIngredients) | !validateInput(inputDescription) ) {
            return;
        }

        String name = inputName.getEditText().getText().toString();
        String country = inputCountry.getEditText().getText().toString();
        String category = dropdown.getText().toString();
        String ingredients = inputIngredients.getEditText().getText().toString();
        String description = inputDescription.getEditText().getText().toString();

        //Visualize data to see that its correct

        Log.i("RECIPE", "Name: " + name);
        Log.i("RECIPE", "Country: " + country);
        Log.i("RECIPE", "Category: " + category);
        Log.i("RECIPE", "Ingredients: " + ingredients);
        Log.i("RECIPE", "Description: " + description);


        //TODO: Store the recipe

        finish();

        // Send a notification to the user if everything is correct
        //sendNotification();
    }

}
