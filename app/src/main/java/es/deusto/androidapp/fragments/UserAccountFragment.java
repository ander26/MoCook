package es.deusto.androidapp.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import es.deusto.androidapp.R;

public class UserAccountFragment extends Fragment {

    private TextInputLayout inputName;
    private TextInputLayout inputEmail;
    private TextInputLayout inputPassword;

    private LinearLayout googleText;
    private LinearLayout editAccount;

    private FirebaseUser user;

    private FirebaseAnalytics mFirebaseAnalytics;

    public UserAccountFragment() {
        // Required empty public constructor
    }

    public static UserAccountFragment newInstance(FirebaseUser user) {
        UserAccountFragment fragment = new UserAccountFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_account,
                container, false);

        googleText = view.findViewById(R.id.google_account);
        editAccount = view.findViewById(R.id.user_account);

        checkGoogleAccount();

        AppCompatButton updateButton = view.findViewById(R.id.update_button);
        inputName = view.findViewById(R.id.name_input);
        inputEmail = view.findViewById(R.id.email_input);
        inputPassword = view.findViewById(R.id.password_input);

        inputName.getEditText().setText(user.getDisplayName());
        inputEmail.getEditText().setText(user.getEmail());

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        updateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateUser();
            }
        });

        AppCompatButton deleteButton = view.findViewById(R.id.delete_button);

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

    private void updateUser() {

        if (!validateName() | !validateEmail() | !validatePassword()) {
            return;
        }

        final String name = inputName.getEditText().getText().toString();
        String email = inputEmail.getEditText().getText().toString();
        final String password = inputPassword.getEditText().getText().toString();

        Toast.makeText(getContext(),"Loading...", Toast.LENGTH_SHORT).show();

        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                if (!password.trim().isEmpty()) {
                                                    user.updatePassword(password)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        CharSequence text = "User updated";
                                                                        int duration = Toast.LENGTH_SHORT;

                                                                        Toast toast = Toast.makeText(getContext(), text, duration);
                                                                        toast.show();
                                                                    } else {
                                                                        Toast.makeText(getContext(),"Error updating user", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    CharSequence text = "User updated";
                                                    int duration = Toast.LENGTH_SHORT;

                                                    Toast toast = Toast.makeText(getContext(), text, duration);
                                                    toast.show();
                                                }
                                            } else {
                                                Toast.makeText(getContext(),"Error updating user", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(getContext(),"Email already in use", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        mFirebaseAnalytics.logEvent("update_account", null);

        UserFragment parentFrag = (UserFragment) getActivity().getSupportFragmentManager().getFragments().get(1);
        parentFrag.changeName(name);

    }

    private void deleteUser() {

        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.deleting_user_title))
                .setMessage(getString(R.string.deleting_user_text))
                .setPositiveButton( getString(R.string.yes), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mFirebaseAnalytics.logEvent("delete_account", null);
                                            getActivity().finish();
                                        } else {
                                            Toast.makeText(getContext(),"Error deleting the user", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }

                })
                .setNegativeButton( getString(R.string.no), null)
                .show();
    }

    private void checkGoogleAccount () {
        if (user.getProviderData().get(1).getProviderId().equals("google.com")) {
            editAccount.setVisibility(View.GONE);
            googleText.setVisibility(View.VISIBLE);
        }
    }
}
