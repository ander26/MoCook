package es.deusto.androidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout inputUsername;
    private TextInputLayout inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputUsername = findViewById(R.id.username_input);
        inputPassword = findViewById(R.id.password_input);

    }

    public Boolean validateLogin () {
        String username = inputUsername.getEditText().getText().toString();
        String password = inputPassword.getEditText().getText().toString();
        String noWhiteSpace = "[^\\s]+";

        if (username.trim().isEmpty()) {
            inputUsername.setError("Field cannot be empty");
            return false;
        } else {
            inputUsername.setError(null);
            inputUsername.setErrorEnabled(false);
        }

        if (!username.matches(noWhiteSpace)) {
            inputUsername.setError("White spaces are not allowed");
            return false;
        } else {
            inputUsername.setError(null);
            inputUsername.setErrorEnabled(false);
        }

        if (password.trim().isEmpty()) {
            inputPassword.setError("Field cannot be empty");
            return false;
        } else{
            inputPassword.setError(null);
            inputPassword.setErrorEnabled(false);
        }
        return true;
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        // TODO: Check if user is correct
        if (!validateLogin()) {
            return;
        }

        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }
}
