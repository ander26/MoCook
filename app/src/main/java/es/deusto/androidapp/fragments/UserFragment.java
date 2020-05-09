package es.deusto.androidapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import es.deusto.androidapp.R;
import es.deusto.androidapp.adapter.UserPagerAdapter;
import es.deusto.androidapp.data.User;

public class UserFragment extends Fragment {

    private ViewPager2 viewPager;
    private UserPagerAdapter userPagerAdapter;
    private TabLayout tabLayout;

    private TextView userName;
    private TextView usernameText;

    private User user;

    public UserFragment() {
        // Required empty public constructor
    }

    public static UserFragment newInstance(User user) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }

    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        userPagerAdapter = new UserPagerAdapter(this, user);
        viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(userPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText(R.string.account_tab_text);
                        break;
                    case 1:
                        tab.setText(R.string.recipes_tab_text);
                        break;
                }
            }
        }
        ).attach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user,
                container, false);

        tabLayout = view.findViewById(R.id.tabs);
        userName = view.findViewById(R.id.name_text);
        usernameText = view.findViewById(R.id.username_text);

        userName.setText(user.getFullName());
        usernameText.setText("@" + user.getUsername());

        return view;
    }


    public void changeName (String name) {
        userName.setText(name);
    }

}
