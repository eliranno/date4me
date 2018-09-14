package com.example.elirannoach.date4me.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elirannoach.date4me.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailAddress;
    private EditText mPassword;
    private Button mSignInButton;
    private TextView mSignUpButton;
    private FirebaseAuth mAuth;

    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private static final String TAG = "LoginActivity:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailAddress = findViewById(R.id.login_email_et);
        mPassword = findViewById(R.id.login_password_ev);
        mSignUpButton = findViewById(R.id.login_signup_tv);
        mSignInButton = findViewById(R.id.login_signin_button);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null)
            startActivity(new Intent(LoginActivity.this,MainActivity.class));

        if(savedInstanceState!=null){
            mEmailAddress.setText(savedInstanceState.getString(EMAIL_KEY));
            mPassword.setText(savedInstanceState.getString(PASSWORD_KEY));
        }

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
            }
        });
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInRequest();
            }
        });

    }

    private void onSignInRequest() {
        mSignInButton.setEnabled(false);
        mAuth.signInWithEmailAndPassword(mEmailAddress.getText().toString(), mPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, getString(R.string.signin_failed_msg),Toast.LENGTH_SHORT).show();
                            mSignInButton.setEnabled(true);
                        }
                    }
                });
    }
}
