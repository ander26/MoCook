package es.deusto.androidapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;
import es.deusto.androidapp.R;
import es.deusto.androidapp.adapter.UserPagerAdapter;

public class UserFragment extends Fragment {

    private TabLayout tabLayout;

    private TextView userName;

    private FirebaseUser user;

    private CircleImageView civAvatar;

    private ImageView closeSession;

    private FirebaseAuth mFirebaseAuth;

    private GoogleApiClient mGoogleApiClient;


    public UserFragment(GoogleApiClient mGoogleApiClient) {
        // Required empty public constructor
        this.mGoogleApiClient = mGoogleApiClient;
    }

    public static UserFragment newInstance(FirebaseUser user, GoogleApiClient mGoogleApiClient) {
        UserFragment fragment = new UserFragment(mGoogleApiClient);
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
        UserPagerAdapter userPagerAdapter = new UserPagerAdapter(this, user);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager);
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
        TextView usernameText = view.findViewById(R.id.username_text);

        userName.setText(user.getDisplayName());
        usernameText.setText(user.getEmail());

        civAvatar = view.findViewById(R.id.civ_avatar);

        if (user.getPhotoUrl() != null ) {
            civAvatar.setVisibility(View.VISIBLE);
            Glide.with(civAvatar.getContext())
                    .load(user.getPhotoUrl())
                    .into(civAvatar);
        }

        closeSession = view.findViewById(R.id.close_session);

        closeSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSessionApp();
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();

        return view;
    }


    public void changeName (String name) {
        userName.setText(name);
    }

    private void closeSessionApp() {
        mFirebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        getActivity().finish();
    }

}
