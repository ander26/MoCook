package es.deusto.androidapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import es.deusto.androidapp.R;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout inputName;
    private TextInputLayout inputEmail;
    private TextInputLayout inputPassword;
    private TextInputLayout inputVerifyPassword;

    private FirebaseAuth mAuth;

    /**
     * Variables related to the notifications
     */

    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    // Notification ID.
    private static final int NOTIFICATION_ID = 0;

    private NotificationManager mNotifyManager;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputName = findViewById(R.id.name_input);
        inputEmail = findViewById(R.id.email_input);
        inputPassword = findViewById(R.id.password_input);
        inputVerifyPassword = findViewById(R.id.verify_input);

        // Create the notification channel.
        createNotificationChannel();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mAuth = FirebaseAuth.getInstance();

    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    public void createNotificationChannel() {

        // Create a notification manager object.
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            getString(R.string.notification_channel_name),
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    (getString(R.string.notification_channel_description));

            mNotifyManager.createNotificationChannel(notificationChannel);
        }
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

        if (!validateEmail() | !validateName() | !validatePassword() | !validateVerifyPassword() ) {
            return;
        }

        final String name = inputName.getEditText().getText().toString();
        String email = inputEmail.getEditText().getText().toString();
        String password = inputPassword.getEditText().getText().toString();

        Toast.makeText(getBaseContext(),"Loading...", Toast.LENGTH_SHORT).show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();


                            mAuth.getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mAuth.signOut();
                                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, null);

                                        Intent intent = new Intent (RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);

                                        // Send a notification to the user if everything is correct
                                        sendNotification();
                                    } else {
                                        Toast.makeText(getBaseContext(),"There was an error creating profile", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            inputEmail.setError(getString(R.string.email_used));
                        }
                    }
                });

    }

    public void sendNotification() {

        // Build the notification with all of the parameters using helper
        // method.

        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();

        // Deliver the notification.
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());

    }

    private NotificationCompat.Builder getNotificationBuilder() {

        Intent notificationIntent = new Intent(this, LoginActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity
                (this, NOTIFICATION_ID, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification with all of the parameters.
        NotificationCompat.Builder notifyBuilder = new NotificationCompat
                .Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle(getString(R.string.register_notification_title))
                .setContentText(getString(R.string.register_notification_text))
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true).setContentIntent(notificationPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        return notifyBuilder;

    }

}
