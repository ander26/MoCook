package es.deusto.androidapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

import es.deusto.androidapp.R;
import es.deusto.androidapp.data.User;
import es.deusto.androidapp.manager.SQLiteManager;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout inputName;
    private TextInputLayout inputUsername;
    private TextInputLayout inputEmail;
    private TextInputLayout inputPassword;
    private TextInputLayout inputVerifyPassword;

    private SQLiteManager sqlite;

    /**
     * Variables related to the notifications
     */

    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    // Notification ID.
    private static final int NOTIFICATION_ID = 0;

    private NotificationManager mNotifyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputName = findViewById(R.id.name_input);
        inputUsername = findViewById(R.id.username_input);
        inputEmail = findViewById(R.id.email_input);
        inputPassword = findViewById(R.id.password_input);
        inputVerifyPassword = findViewById(R.id.verify_input);

        sqlite = new SQLiteManager(this);

        // Create the notification channel.
        createNotificationChannel();

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

        if (sqlite.storeUser(new User(username, name, email, password)) == -1) {
            inputUsername.setError(getString(R.string.username_used));
        } else {
            Intent intent = new Intent (this, LoginActivity.class);
            startActivity(intent);

            // Send a notification to the user if everything is correct
            sendNotification();
        }

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
