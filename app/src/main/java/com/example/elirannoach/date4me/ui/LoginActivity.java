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
import com.example.elirannoach.date4me.data.Member;
import com.example.elirannoach.date4me.utils.FireBaseUtils;
import com.example.elirannoach.date4me.utils.SharedPreferenceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailAddress;
    private EditText mPassword;
    private Button mSignInButton;
    private TextView mSignUpButton;
    private FirebaseAuth mAuth;
    private ValueEventListener mReceiveProfileEventListener;

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

        mReceiveProfileEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Member myProfile = dataSnapshot.child(mAuth.getCurrentUser().getUid()).getValue(Member.class);
                if(myProfile!=null){
                    SharedPreferenceUtils sharedPreferenceUtils = SharedPreferenceUtils.getInstance(LoginActivity.this);
                    sharedPreferenceUtils.setValue(SharedPreferenceUtils.UID_TAG,myProfile.mUid);
                    sharedPreferenceUtils.setValue(SharedPreferenceUtils.DOB_TAG,myProfile.mDob);
                    sharedPreferenceUtils.setValue(SharedPreferenceUtils.GENDER,myProfile.mGender);
                    // we are all done with loging in the user. let's go to the main activity
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                }
                else{
                    showFailedLogingNotification();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

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
                            Query query = FirebaseDatabase.getInstance().getReference(FireBaseUtils.MEMBER_DB_KEY)
                                    .orderByChild("uid").equalTo(user.getUid());
                            query.addListenerForSingleValueEvent(mReceiveProfileEventListener);

                        } else {
                            // If sign in fails, display a message to the user.
                            showFailedLogingNotification();
                        }
                    }
                });
    }

    private void showFailedLogingNotification(){
        Log.w(TAG, "signInWithEmail:failure");
        Toast.makeText(LoginActivity.this, getString(R.string.signin_failed_msg),Toast.LENGTH_SHORT).show();
        mSignInButton.setEnabled(true);
    }
}
