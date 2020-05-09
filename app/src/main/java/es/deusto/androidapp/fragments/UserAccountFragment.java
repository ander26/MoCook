package es.deusto.androidapp.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputLayout;

import es.deusto.androidapp.activities.LoginActivity;
import es.deusto.androidapp.R;

public class UserAccountFragment extends Fragment {

    private AppCompatButton updateButton;
    private AppCompatButton deleteButton;

    private TextInputLayout inputName;
    private TextInputLayout inputEmail;
    private TextInputLayout inputPassword;

    public UserAccountFragment() {
        // Required empty public constructor
    }

    public static UserAccountFragment newInstance() {
        UserAccountFragment fragment = new UserAccountFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_account,
                container, false);

        updateButton = view.findViewById(R.id.update_button);
        inputName = view.findViewById(R.id.name_input);
        inputEmail = view.findViewById(R.id.email_input);
        inputPassword = view.findViewById(R.id.password_input);

        updateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateUser();
            }
        });

        deleteButton = view.findViewById(R.id.delete_button);

        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                deleteUser();
            }
        });
        return view;
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

        if (!password.trim().isEmpty()) {
            if (!password.matches(pattern)) {
                inputPassword.setError(getString(R.string.incorrect_password));
                return false;
            } else {
                inputPassword.setError(null);
                inputPassword.setErrorEnabled(false);
            }
        }
        return true;
    }

    public void updateUser() {

        if (!validateName() | !validateEmail() | !validatePassword()) {
            return;
        }

        String name = inputName.getEditText().getText().toString();
        String email = inputEmail.getEditText().getText().toString();
        String password = inputPassword.getEditText().getText().toString();


        Log.i("RECIPE", "Name: " + name);
        Log.i("RECIPE", "Email: " + email);
        Log.i("RECIPE", "Password: " + password);

        //TODO: Update user in the DB

        if (!password.trim().isEmpty()) {
            //Change password and data
            Log.i("RECIPE", "CHANGE PASSWORD AND PERSONAL INFO");
        } else {
            Log.i("RECIPE", "CHANGE ONLY PERSONAL INFO");
        }

        UserFragment parentFrag = (UserFragment) getActivity().getSupportFragmentManager().getFragments().get(0);
        parentFrag.changeName(name);

    }

    public void deleteUser() {

        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.deleting_user_title))
                .setMessage(getString(R.string.deleting_user_text))
                .setPositiveButton( getString(R.string.yes), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: Delete the user
                        Intent intent = new Intent (getContext(), LoginActivity.class);
                        startActivity(intent);
                    }

                })
                .setNegativeButton( getString(R.string.no), null)
                .show();
    }
}
