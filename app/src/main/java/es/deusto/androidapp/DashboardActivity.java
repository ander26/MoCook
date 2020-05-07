package es.deusto.androidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        fragment = HomeFragment.newInstance();
                        break;
                    case R.id.navigation_liked:
                        break;
                    case R.id.navigation_user:
                        fragment = UserFragment.newInstance();
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
        fragmentTransaction.add(R.id.fragment_placeholder, HomeFragment.newInstance());
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
}
