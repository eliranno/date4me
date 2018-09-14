package com.example.elirannoach.date4me.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elirannoach.date4me.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mEmailAddressEditText;
    private EditText mPasswordEditText;
    private EditText mPasswordConfirmEditText;
    private Button mCreateAccountButton;
    private ProgressBar mProgressBar;
    private Toast mRegisterationToast;
    private FirebaseAuth mAuth;

    private final static String EMAIL_KEY = "email";
    private final static String PASSWORD_KEY = "password";
    private final static String PASSWORD_CONFIRM_KEY = "password_confirm";
    private final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
    private final String TAG = "Registration";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mEmailAddressEditText = findViewById(R.id.registration_email_ev);
        mPasswordEditText = findViewById(R.id.registration_password_ev);
        mPasswordConfirmEditText = findViewById(R.id.registration_confirm_password_ev);
        mCreateAccountButton = findViewById(R.id.create_account_button);
        mProgressBar = findViewById(R.id.registration_progressBar);
        mAuth = FirebaseAuth.getInstance();


        mProgressBar.setVisibility(View.INVISIBLE);
        mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateUserInputs())
                    processNewAccountRequest();
                else
                    Toast.makeText(RegistrationActivity.this, getString(R.string.registration_wrong_input),
                            Toast.LENGTH_SHORT).show();
            }
        });

        if(savedInstanceState!=null){
            mEmailAddressEditText.setText(savedInstanceState.getString(EMAIL_KEY), TextView.BufferType.EDITABLE);
            mPasswordEditText.setText(savedInstanceState.getString(PASSWORD_KEY), TextView.BufferType.EDITABLE);
            mPasswordConfirmEditText.setText(savedInstanceState.getString(PASSWORD_CONFIRM_KEY), TextView.BufferType.EDITABLE);
        }
    }



    private void setViewButtonsEnabled(boolean state){
        mEmailAddressEditText.setEnabled(state);
        mPasswordEditText.setEnabled(state);
        mPasswordConfirmEditText.setEnabled(state);
        mCreateAccountButton.setEnabled(state);
    }

    private void processNewAccountRequest() {
        mProgressBar.setVisibility(View.VISIBLE);
        setViewButtonsEnabled(false);
        mAuth.createUserWithEmailAndPassword(mEmailAddressEditText.getText().toString(), mPasswordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            onRegistrationCompleted(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            onRegistrationCompleted(null);
                        }
                    }
                });

    }

    private void onRegistrationCompleted(FirebaseUser user){
        if(user!=null){
            startActivity(new Intent(this,ProfileActivity.class));
        }
        else {
            Toast.makeText(RegistrationActivity.this, getString(R.string.registration_failed), Toast.LENGTH_SHORT).show();
            setViewButtonsEnabled(true);
        }
    }

    private boolean validateUserInputs() {
        return (!TextUtils.isEmpty(mEmailAddressEditText.getText()) &&
                mPasswordEditText.getText().toString().length()>=8 &&
                mPasswordConfirmEditText.getText().toString().equals(mPasswordEditText.getText().toString()) &&
                isValidPassword(mPasswordEditText.getText().toString()));
    }

    private static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(EMAIL_KEY,mEmailAddressEditText.getText().toString());
        outState.putString(PASSWORD_KEY,mPasswordEditText.getText().toString());
        outState.putString(PASSWORD_CONFIRM_KEY,mPasswordConfirmEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }
}
