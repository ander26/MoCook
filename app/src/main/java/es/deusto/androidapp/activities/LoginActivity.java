package es.deusto.androidapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import es.deusto.androidapp.R;
import es.deusto.androidapp.services.FCMService;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();

    private TextInputLayout inputUsername;
    private TextInputLayout inputPassword;

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private SignInButton mButtonSignInGoogle;
    private GoogleApiClient mGoogleApiClient;
    private static final int REQ_CODE_SIGN_IN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputUsername = findViewById(R.id.username_input);
        inputPassword = findViewById(R.id.password_input);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mButtonSignInGoogle = findViewById(R.id.google_sign_in);
        mButtonSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonSignInGoogle.setEnabled(false);
                signInGoogle();
            }
        });

        configureGoogleSignInAndCreateClient();

        initFirebaseAuth ();

        FCMService.printToken(this);

        if (getIntent().getBundleExtra("message") != null) {
            checkFCMMessage(getIntent().getBundleExtra("message"));
        }


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

        Toast.makeText(getBaseContext(),"Loading...", Toast.LENGTH_SHORT).show();

        mFirebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Bundle params = new Bundle();
                            params.putString(FirebaseAnalytics.Param.METHOD, "email");
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, params);

                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);

                        } else {
                            CharSequence text = "No user found";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(LoginActivity.this, text, duration);
                            toast.show();
                        }
                    }
                });

    }

    private void initFirebaseAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
        }
    }

    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi .getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, REQ_CODE_SIGN_IN);
    }

    private void configureGoogleSignInAndCreateClient() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN) .requestIdToken(getString(R.string.default_web_client_id)) .requestEmail()
            .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this) .enableAutoManage(
                this ,
                new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                public void onConnectionFailed(
                        @NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getBaseContext(),"Google Play Services error.", Toast.LENGTH_SHORT).show();
                } })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso) .build();
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case REQ_CODE_SIGN_IN:
                if (resultCode != RESULT_CANCELED) {
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    // Google SignIn successful, authenticate with Firebase
                    mButtonSignInGoogle.setEnabled(true);
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                } else {
                    // Google SignIn failed
                    Toast.makeText(getBaseContext(),"Google sign in failed", Toast.LENGTH_SHORT).show();
                    mButtonSignInGoogle.setEnabled(true);
                }
            } else {
                // Google SignIn cancelled
                mButtonSignInGoogle.setEnabled(true);
                }
            break;
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider
                .getCredential(account.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Auth. failed",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);

                            Bundle params = new Bundle();
                            params.putString(FirebaseAnalytics.Param.METHOD, "google");
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, params);
                        }
                    }
                });
    }

    private void checkFCMMessage (Bundle fcmmessageData) {
        if (fcmmessageData != null) {
            String title = fcmmessageData.getString(FCMService.MSG_DATA_KEY_TITLE);
            String description = fcmmessageData.getString(FCMService.MSG_DATA_KEY_DESC);
            String recipe = fcmmessageData.getString(FCMService.MSG_DATA_KEY_RECIPE);

            Log.d (TAG, "checkFCMMessage: Title = " + title);
            Log.d (TAG, "checkFCMMessage: Description = " + description);
            Log.d (TAG, "checkFCMMessage: Recipe = " + recipe);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage(Html.fromHtml(description + "<br/> <br/> " + "<b>"+"New recipe: "+"</b>" + recipe));

            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });



            builder.show();

        }
    }

}
