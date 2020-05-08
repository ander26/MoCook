package es.deusto.androidapp;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class UserPagerAdapter extends FragmentStateAdapter {

    private static final int SIZE = 2;

    public UserPagerAdapter(Fragment fragment){
        super(fragment);
    }

    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;

        switch (position) {
            case 0:
                fragment = UserAccountFragment.newInstance();
                break;
            default:
                fragment = CreatedRecipesFragment.newInstance();
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return SIZE;
    }

}
