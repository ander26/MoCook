package es.deusto.androidapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import es.deusto.androidapp.R;
import es.deusto.androidapp.activities.RecipeActivity;
import es.deusto.androidapp.data.User;
import es.deusto.androidapp.manager.SQLiteManager;

public class CreatedRecipesFragment extends Fragment {

    private User user;
    private SQLiteManager sqlite;

    public CreatedRecipesFragment() {
        // Required empty public constructor
    }

    public static CreatedRecipesFragment newInstance(User user) {
        CreatedRecipesFragment fragment = new CreatedRecipesFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqlite = new SQLiteManager(getContext());
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_created_recipes,
                container, false);

        Button button = view.findViewById(R.id.test_button);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), RecipeActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("recipe", 5);
                startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}
