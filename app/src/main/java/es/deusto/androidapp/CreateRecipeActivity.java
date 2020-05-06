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

    private Bitmap bitmapRecipe;

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

        builder.setTitle(getString(R.string.title_dialog_image));

        if (checkCameraHardware()) {
            final String [] options = {getString(R.string.take_photo), getString(R.string.choose_gallery), getString(R.string.load_url), getString(R.string.cancel_button)};

            builder.setItems(options, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int item) {

                    if (options[item].equals(getString(R.string.take_photo))) {

                        if (ContextCompat.checkSelfPermission(CreateRecipeActivity.this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(CreateRecipeActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    0);
                        } else {
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 0);
                        }


                    } else if (options[item].equals(getString(R.string.choose_gallery))) {

                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 1);

                    } else if (options[item].equals(getString(R.string.cancel_button))) {
                        dialog.dismiss();
                    } else if (options[item].equals(getString(R.string.load_url))) {
                        showTextDialog();
                    }
                }
            });
        } else {

            final String [] options = {getString(R.string.choose_gallery), getString(R.string.load_url), getString(R.string.cancel_button)};

            builder.setItems(options, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals(getString(R.string.choose_gallery))) {

                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 1);

                    } else if (options[item].equals(getString(R.string.cancel_button))) {
                        dialog.dismiss();
                    } else if (options[item].equals(getString(R.string.load_url))) {
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
                        bitmapRecipe = (Bitmap) data.getExtras().get("data");
                        recipeImage.setImageBitmap(bitmapRecipe);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {

                        try {
                             Uri imageUri = data.getData();
                             InputStream imageStream = getContentResolver().openInputStream(imageUri);
                             bitmapRecipe= BitmapFactory.decodeStream(imageStream);
                             recipeImage.setImageBitmap(bitmapRecipe);
                        } catch (Exception e) {
                            showDialog(getString(R.string.error_dialog_title), getString(R.string.error_dialog_message));
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
        builder.setTitle(getString(R.string.url_dialog_title));

        builder.setMessage(getString(R.string.url_dialog_message));

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(getString(R.string.url_input_hint));

        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 60;
        params.rightMargin = 60;
        input.setLayoutParams(params);

        container.addView(input);

        builder.setView(container);

        builder.setPositiveButton(getString(R.string.url_dialog_add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String imageURL = input.getText().toString();

                Glide.with(CreateRecipeActivity.this)
                        .asBitmap()
                        .load(imageURL)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                bitmapRecipe = resource;
                                recipeImage.setImageBitmap(bitmapRecipe);
                            }
                            @Override
                            public void onLoadCleared( Drawable placeholder) {
                            }

                            @Override
                            public void onLoadFailed ( Drawable errorDrawable) {
                                showDialog(getString(R.string.error_dialog_title), getString(R.string.error_dialog_url));
                            }
                        });

            }
        });

        builder.setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
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

    }


}
