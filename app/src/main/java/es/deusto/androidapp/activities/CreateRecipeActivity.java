package es.deusto.androidapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import es.deusto.androidapp.R;
import es.deusto.androidapp.data.Recipe;
import es.deusto.androidapp.manager.UserPropertyManager;

public class CreateRecipeActivity extends AppCompatActivity {

    public static final String RECIPES_CHILD = "recipes";
    public static final String IMAGES_FOLDER = "images";
    public static final String PICTURE_FIELD = "picture";

    private static final String TAG = CreateRecipeActivity.class.getName();

    private ImageView recipeImage;
    private AutoCompleteTextView dropdown;

    private TextInputLayout inputName;
    private TextInputLayout inputCountry;
    private TextInputLayout inputIngredients;
    private TextInputLayout inputDescription;

    private Bitmap bitmapRecipe;

    private Recipe recipe;

    private TextView activityTitle;
    private AppCompatButton createButton;

    private FirebaseAnalytics mFirebaseAnalytics;

    private UserPropertyManager mUserPropertyManager;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference mFirebaseDatabaseRef;
    private String recipeID;

    private StorageReference mFirebaseStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        String[] COUNTRIES = new String[] {getString(R.string.meat),
                getString(R.string.fish), getString(R.string.desserts), getString(R.string.salads),
                getString(R.string.soups), getString(R.string.breads), getString(R.string.breakfast),
                getString(R.string.vegetables), getString(R.string.beverages)};

        ArrayAdapter <String> adapter = new ArrayAdapter <> (this,
                R.layout.dropdown_item,
                COUNTRIES);

        dropdown = findViewById(R.id.dropdown);
        recipeImage = findViewById(R.id.recipe_image);
        inputName = findViewById(R.id.recipe_name_input);
        inputCountry = findViewById(R.id.recipe_country_input);
        inputIngredients = findViewById(R.id.recipe_ingredients_input);
        inputDescription = findViewById(R.id.recipe_description_input);
        activityTitle = findViewById(R.id.activity_title);
        createButton = findViewById(R.id.create_button);

        dropdown.setAdapter(adapter);

        dropdown.setKeyListener(null);

        dropdown.setText(COUNTRIES[0], false);

        recipeID = getIntent().getStringExtra("recipe");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mUserPropertyManager = UserPropertyManager.getInstance();
        initFirebaseAuth ();
        initFirebaseDatabaseReference();
        initFirebaseCloudStorage();
        if (recipeID != null) {
            retrieveRecipe(recipeID);
        }

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
                        addImageAnalytic("camera");
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {

                        try {
                             Uri imageUri = data.getData();
                             InputStream imageStream = getContentResolver().openInputStream(imageUri);
                             bitmapRecipe= BitmapFactory.decodeStream(imageStream);
                             recipeImage.setImageBitmap(bitmapRecipe);
                             addImageAnalytic("gallery");
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
        if (requestCode == 0) {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
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
                        .apply(new RequestOptions().override(600, 300))
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                bitmapRecipe = resource;
                                recipeImage.setImageBitmap(bitmapRecipe);
                                addImageAnalytic("internet");
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

        Recipe recipe = new Recipe(name, country, category, ingredients, description, mFirebaseUser.getUid());

        Bundle params = new Bundle();
        params.putString("recipe_name", recipe.getName());
        params.putString("recipe_category", recipe.getCategory());
        mFirebaseAnalytics.logEvent("create_recipe", params);

        mUserPropertyManager.registerUserAsCreator(this);

        CharSequence text = "Loading...";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(CreateRecipeActivity.this, text, duration);
        toast.show();

        mFirebaseDatabaseRef.child(RECIPES_CHILD).push().setValue(recipe, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    if (bitmapRecipe != null) {
                        String key = databaseReference.getKey();
                        StorageReference newImageRef = mFirebaseStorageRef.child(IMAGES_FOLDER).child(key);
                        putImageInStorage(newImageRef, bitmapRecipe, key);
                    } else {
                        CharSequence text = "Recipe created";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(CreateRecipeActivity.this, text, duration);
                        toast.show();

                        finish();
                    }
                } else {
                    CharSequence text = "Error creating the recipe";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(CreateRecipeActivity.this, text, duration);
                    toast.show();
                }
            }
        });


    }

    private void putImageInStorage(final StorageReference newImageRef, Bitmap bitmap,
                                   final String recipeKey) {

        Log.d(TAG, "Image uploading to " + newImageRef.toString());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        newImageRef.putBytes(data)
                .addOnCompleteListener(this,
                        new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull
                                                           Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    mFirebaseDatabaseRef.child(RECIPES_CHILD)
                                            .child(recipeKey)
                                            .child(PICTURE_FIELD)
                                            .setValue(newImageRef.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    CharSequence text = "Recipe created";
                                                    int duration = Toast.LENGTH_SHORT;

                                                    Toast toast = Toast.makeText(CreateRecipeActivity.this, text, duration);
                                                    toast.show();

                                                    finish();
                                        }
                                    });


                                } else {
                                    CharSequence text = "Error uploading the image";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(CreateRecipeActivity.this, text, duration);
                                    toast.show();
                                    finish();
                                }
                            }
                        })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double prog = (100.0 * taskSnapshot.getBytesTransferred())
                                / taskSnapshot.getTotalByteCount();
                        Log.i(TAG, "Upload is " + prog + "% done");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CharSequence text = "Error uploading the image";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(CreateRecipeActivity.this, text, duration);
                        toast.show();
                        finish();;
                    }
                });
    }

    private void editMode() {

        inputName.getEditText().setText(recipe.getName());
        inputCountry.getEditText().setText(recipe.getCountry());
        dropdown.setText(recipe.getCategory(), false);
        inputIngredients.getEditText().setText(recipe.getIngredients());
        inputDescription.getEditText().setText(recipe.getDescription());

        activityTitle.setText(getString(R.string.edit_recipe_title));

        createButton.setText(getString(R.string.edit_recipe_title));
        createButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                updateRecipe();
            }
        });

        if (recipe.getPicture() != null) {
            if (recipe.getPicture().startsWith("gs://") ||
                    recipe.getPicture().startsWith("https://firebasestorage.googleapis.com/"))
            {

                StorageReference storageRef = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(recipe.getPicture());

                storageRef.getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();
                                    Glide.with(recipeImage.getContext())
                                            .load(downloadUrl)
                                            .into(recipeImage);
                                } else {
                                    recipeImage.setImageDrawable(getDrawable(R.drawable.loader_background));
                                }
                            }
                        });
            }
        }

    }

    private void updateRecipe() {

        if (!validateInput(inputName) | !validateInput(inputCountry) | !validateInput(inputIngredients) | !validateInput(inputDescription) ) {
            return;
        }

        recipe.setName(inputName.getEditText().getText().toString());
        recipe.setCountry(inputCountry.getEditText().getText().toString());
        recipe.setCategory(dropdown.getText().toString());
        recipe.setIngredients(inputIngredients.getEditText().getText().toString());
        recipe.setDescription(inputDescription.getEditText().getText().toString());

        //recipe.setPicture(bitmapRecipe);

        recipe.setId(null);

        CharSequence text = "Loading...";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(CreateRecipeActivity.this, text, duration);
        toast.show();

        mFirebaseDatabaseRef.setValue(recipe, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                if (bitmapRecipe != null) {
                    String key = databaseReference.getKey();
                    StorageReference newImageRef = mFirebaseStorageRef.child(IMAGES_FOLDER).child(key);
                    putImageInStorage(newImageRef, bitmapRecipe, key);
                } else {
                    CharSequence text = "Recipe updated";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(CreateRecipeActivity.this, text, duration);
                    toast.show();

                    finish();
                }

                Bundle params = new Bundle();
                params.putString(FirebaseAnalytics.Param.ITEM_ID, recipe.getId());
                mFirebaseAnalytics.logEvent("edit_recipe", params);
            }
        });

    }

    private void addImageAnalytic (String type) {
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.METHOD, type);
        mFirebaseAnalytics.logEvent("add_image", params);
    }


    private void initFirebaseAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initFirebaseDatabaseReference () {
        mFirebaseDatabaseRef = FirebaseDatabase.getInstance().getReference();
    }

    private void initFirebaseCloudStorage () {
        mFirebaseStorageRef = FirebaseStorage.getInstance().getReference();
    }

    private void retrieveRecipe(final String recipeID) {
        mFirebaseDatabaseRef = mFirebaseDatabaseRef.child(CreateRecipeActivity.RECIPES_CHILD).child(recipeID);

        ValueEventListener mRecipeValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    recipe = dataSnapshot.getValue(Recipe.class);
                    recipe.setId(recipeID);
                    editMode();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mFirebaseDatabaseRef.addListenerForSingleValueEvent(mRecipeValueEventListener);
    }
}
