package com.crincongtz.myphoneauth;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "MyLogin";
    private static final int RC_SIGN_IN = 123;

    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            Log.d(TAG, "Teléfono: " + auth.getCurrentUser().getPhoneNumber());
            openMainActivity();
        }

    }

    public void signInPhone(View view) {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.FirebaseAuthTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()))
                        .build(),
                RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != data) {
            Log.d(TAG, "onActivityResult: " + requestCode + " , " + resultCode + ", " + data.toString());
        }

        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
        }
    }

    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (resultCode == RESULT_OK) {
            openMainActivity();
        } else {
            if (response == null) {
                // User pressed back button
                Log.e(TAG, "Login canceled by User");
                showSnackbar(R.string.sign_in_cancelled);
                return;
            }
            if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                Log.e(TAG, "No Internet Connection");
                showSnackbar(R.string.no_internet_connection);
                return;
            }
            if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                Log.e(TAG, "Unknown Error");
                showSnackbar(R.string.unknown_error);
                return;
            }
        }

        Log.e(TAG, "Unknown sign in response");
        showSnackbar(R.string.unknown_sign_in_response);
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showSnackbar(int errorMessageRes) {
        Snackbar.make(coordinatorLayout, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

}
