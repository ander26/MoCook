package es.deusto.androidapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import es.deusto.androidapp.R;
import es.deusto.androidapp.data.User;
import es.deusto.androidapp.fragments.HomeFragment;
import es.deusto.androidapp.fragments.MyListFragment;
import es.deusto.androidapp.fragments.UserFragment;

public class DashboardActivity extends AppCompatActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = getIntent().getParcelableExtra("user");

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
                        fragment = MyListFragment.newInstance();
                        break;
                    case R.id.navigation_user:
                        fragment = UserFragment.newInstance(user);
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
       intent.putExtra("user", user);
       startActivity(intent);
    }
}
