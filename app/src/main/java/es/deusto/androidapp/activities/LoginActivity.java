package es.deusto.androidapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import es.deusto.androidapp.R;
import es.deusto.androidapp.data.User;
import es.deusto.androidapp.manager.SQLiteManager;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout inputUsername;
    private TextInputLayout inputPassword;

    private SQLiteManager sqlite;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputUsername = findViewById(R.id.username_input);
        inputPassword = findViewById(R.id.password_input);

        sqlite = new SQLiteManager(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    }

    public Boolean validateLogin () {
        String username = inputUsername.getEditText().getText().toString();
        String password = inputPassword.getEditText().getText().toString();
        String noWhiteSpace = "[^\\s]+";

        if (username.trim().isEmpty()) {
            inputUsername.setError(getString(R.string.field_empty));
            return false;
        } else {
            inputUsername.setError(null);
            inputUsername.setErrorEnabled(false);
        }

        if (!username.matches(noWhiteSpace)) {
            inputUsername.setError(getString(R.string.white_spaces));
            return false;
        } else {
            inputUsername.setError(null);
            inputUsername.setErrorEnabled(false);
        }

        if (password.trim().isEmpty()) {
            inputPassword.setError(getString(R.string.field_empty));
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

        if (!validateLogin()) {
            return;
        }

        String username = inputUsername.getEditText().getText().toString();
        String password = inputPassword.getEditText().getText().toString();

        ArrayList<User> users = sqlite.loginUser(username, password);

        if (users.size() == 0) {
            CharSequence text = "No user found";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }  else {
            Bundle params = new Bundle();
            params.putString(FirebaseAnalytics.Param.METHOD, "email");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, params);
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("user", users.get(0));
            startActivity(intent);
        }

    }
}
