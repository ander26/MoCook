package es.deusto.androidapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.deusto.androidapp.R;
import es.deusto.androidapp.fragments.HomeFragment;
import es.deusto.androidapp.fragments.MyListFragment;
import es.deusto.androidapp.fragments.UserFragment;

public class DashboardActivity extends AppCompatActivity {


    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        initFirebaseAuth();
        initGoogleApiClient();

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        fragment = HomeFragment.newInstance(mFirebaseUser);
                        mFirebaseAnalytics.logEvent("check_home", null);
                        break;
                    case R.id.navigation_liked:
                        fragment = MyListFragment.newInstance(mFirebaseUser);
                        mFirebaseAnalytics.logEvent("check_my_list", null);
                        break;
                    case R.id.navigation_user:
                        fragment = UserFragment.newInstance(mFirebaseUser, mGoogleApiClient);
                        mFirebaseAnalytics.logEvent("check_user_profile", null);
                        break;
                }
                replaceFragment(fragment);
                return true;
            }
        });

        setInitialFragment();

    }

    private void setInitialFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_placeholder, HomeFragment.newInstance(mFirebaseUser));
        fragmentTransaction.commit();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_placeholder, fragment);
        fragmentTransaction.commit();
    }


    public void addRecipe(View view) {
       Intent intent = new Intent(this, CreateRecipeActivity.class);
       startActivity(intent);
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

    private void initGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, null)
                .addApi(Auth.GOOGLE_SIGN_IN_API).build();
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void onBackPressed() {
       mFirebaseAuth.signOut();
       Auth.GoogleSignInApi.signOut(mGoogleApiClient);
       super.onBackPressed();
    }
}
