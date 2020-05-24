package es.deusto.androidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import es.deusto.androidapp.activities.LoginActivity;

public class MainActivity extends AppCompatActivity {

    // Time to change to the login screen

    private  static final int TIME_CHANGE = 3000;

    private ImageView logo;

    private LinearLayout textGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_loader_animation);
        Animation textAnimation = AnimationUtils.loadAnimation(this, R.anim.text_loader_animation);

        logo = findViewById(R.id.logo_loader);
        textGroup = findViewById(R.id.text_group_loader);

        // Assigning animations to the elements retrieved before

        logo.setAnimation(logoAnimation);
        textGroup.setAnimation(textAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent (MainActivity.this, LoginActivity.class);

                Pair [] pairs = new Pair[2];
                pairs[0] = new Pair <View, String> (logo, getString(R.string.transition_image));
                pairs[1] = new Pair <View, String>(textGroup, getString(R.string.transition_text));

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                startActivity(intent, options.toBundle());
                finish();
            }
        }, TIME_CHANGE);
    }

}
