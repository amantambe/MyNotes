package com.skywalker.mynotes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class Login extends Fragment {
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        SignInButton signInButton = view.findViewById(R.id.sign_in_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        signInButton.setOnClickListener(v -> signIn());

        Log.d(TAG, "onCreateView: Login Fragment View Created");

        return view;
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.d(TAG, "signIn: SignIn Intent Started");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: RequestCode=" + requestCode + " ResultCode=" + resultCode);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            saveUserToPreferences(account);
            Log.d(TAG, "handleSignInResult: SignIn Successful");
            Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_notesListFragment);
        } catch (ApiException e) {
            Log.w(TAG, "handleSignInResult: SignIn Failed. Status Code: " + e.getStatusCode(), e);
            switch (e.getStatusCode()) {
                case GoogleSignInStatusCodes.SIGN_IN_CANCELLED:
                    Log.w(TAG, "Sign in was cancelled by the user.");
                    break;
                case GoogleSignInStatusCodes.SIGN_IN_FAILED:
                    Log.w(TAG, "Sign in failed. Please try again.");
                    break;
                case GoogleSignInStatusCodes.NETWORK_ERROR:
                    Log.w(TAG, "A network error occurred. Please try again.");
                    break;
                case GoogleSignInStatusCodes.INVALID_ACCOUNT:
                    Log.w(TAG, "Invalid account. Please check the account being used.");
                    break;
                case GoogleSignInStatusCodes.DEVELOPER_ERROR:
                    Log.w(TAG, "Developer error. Check the configuration in the Google API Console.");
                    break;
                default:
                    Log.w(TAG, "An unknown error occurred. Status Code: " + e.getStatusCode());
                    break;
            }
        }
    }


    private void saveUserToPreferences(GoogleSignInAccount account) {
        SharedPreferences preferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_email", account.getEmail());
        editor.apply();
        Log.d(TAG, "saveUserToPreferences: User Email Saved");
    }
}
