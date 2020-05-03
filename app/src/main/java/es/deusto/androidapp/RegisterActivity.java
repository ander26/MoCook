package es.deusto.androidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout inputName;
    private TextInputLayout inputUsername;
    private TextInputLayout inputEmail;
    private TextInputLayout inputPassword;
    private TextInputLayout inputVerifyPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputName = findViewById(R.id.name_input);
        inputUsername = findViewById(R.id.username_input);
        inputEmail = findViewById(R.id.email_input);
        inputPassword = findViewById(R.id.password_input);
        inputVerifyPassword = findViewById(R.id.verify_input);

    }

    public void goBack(View view) {
        finish();
    }

    private Boolean validateName () {
        String name = inputName.getEditText().getText().toString();

        if (name.trim().isEmpty()) {
            inputName.setError(getString(R.string.field_empty));
            return false;
        } else {
            inputName.setError(null);
            inputName.setErrorEnabled(false);
        }
        return true;
    }

    private Boolean validateUsername () {
        String username = inputUsername.getEditText().getText().toString();
        String noWhiteSpace = "[^\\s]+";

        //TODO: Check if the user already exists

        if (username.trim().isEmpty()) {
            inputUsername.setError(getString(R.string.field_empty));
            return false;
        } else if (!username.matches(noWhiteSpace)) {
            inputUsername.setError(getString(R.string.white_spaces));
            return false;
        } else{
            inputUsername.setError(null);
            inputUsername.setErrorEnabled(false);
        }
        return true;
    }

    private Boolean validateEmail () {
        String email = inputEmail.getEditText().getText().toString();
        String pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email.trim().isEmpty()) {
            inputEmail.setError(getString(R.string.field_empty));
            return false;
        } else if (!email.matches(pattern)) {
            inputEmail.setError(getString(R.string.invalid_email));
            return false;
        } else {
            inputEmail.setError(null);
            inputEmail.setErrorEnabled(false);
        }
        return true;
    }

    private Boolean validatePassword () {
        String password = inputPassword.getEditText().getText().toString();
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";

        if (password.trim().isEmpty()) {
            inputPassword.setError(getString(R.string.field_empty));
            return false;
        } else if (!password.matches(pattern)) {
            inputPassword.setError(getString(R.string.incorrect_password));
            return false;
        }  else {
            inputPassword.setError(null);
            inputPassword.setErrorEnabled(false);
        }
        return true;
    }

    private Boolean validateVerifyPassword () {
        String password = inputVerifyPassword.getEditText().getText().toString();
        String originalPassword = inputPassword.getEditText().getText().toString();

        if (password.trim().isEmpty()) {
            inputVerifyPassword.setError(getString(R.string.field_empty));
            return false;
        } else if (!password.equals(originalPassword)) {
            inputVerifyPassword.setError(getString(R.string.password_match));
            return false;
        }  else {
            inputVerifyPassword.setError(null);
            inputVerifyPassword.setErrorEnabled(false);
        }
        return true;
    }


    public void register(View view) {

        if (!validateEmail() | !validateName() | !validatePassword() | !validateUsername() | !validateVerifyPassword() ) {
            return;
        }

        String name = inputName.getEditText().getText().toString();
        String username = inputUsername.getEditText().getText().toString();
        String email = inputEmail.getEditText().getText().toString();
        String password = inputPassword.getEditText().getText().toString();

        //TODO: Store the user

        Intent intent = new Intent (this, LoginActivity.class);
        startActivity(intent);

        //TODO: Create notification of successful register
    }

}
