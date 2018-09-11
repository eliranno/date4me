package com.example.elirannoach.date4me;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.widget.Toast.LENGTH_SHORT;

public class ProfileActivity extends AppCompatActivity {
    @BindView(R.id.ev_full_name)
    EditText mFullName;
    @BindView(R.id.gender_radio_group)
    RadioGroup mGenderRadioGroup;
    @BindView(R.id.ev_date_of_birth)
    EditText mDateOfBirth;
    @BindView(R.id.ev_city)
    EditText mCity;
    @BindView(R.id.spinner_states)
    Spinner mStateSpinner;
    @BindView(R.id.spinner_religion)
    Spinner mReligionSpinner;
    @BindView(R.id.ev_occupation)
    EditText mOccupation;
    @BindView(R.id.ev_description)
    EditText mDescription;
    @BindView(R.id.fab_update_profile)
    FloatingActionButton mUpdateButton;
    @BindView(R.id.fab_editPhoto)
    FloatingActionButton mEditProfilePhotoButton;
    @BindView(R.id.profile_activity_progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.profile_image)
    CircleImageView mProfileImage;


    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ValueEventListener mProfileEventListener;
    private  Member mMemberDetails;

    private final static String MEMBER_DB_KEY = "member";
    private final static int RESULT_LOAD_IMG = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        mProgressBar.setVisibility(View.INVISIBLE);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference(MEMBER_DB_KEY+"/"+mAuth.getCurrentUser().getUid());
        mEditProfilePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectProfileImage();

            }
        });

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
        mProfileEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Member member = dataSnapshot.getValue(Member.class);
                if(member!=null)
                    updateUI(member);
                else
                    Toast.makeText(ProfileActivity.this,getString(R.string.profile_not_exists),LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this,getString(R.string.failed_loading_profile),LENGTH_SHORT).show();
            }
        };
        myRef.addListenerForSingleValueEvent(mProfileEventListener);
    }

    private void selectProfileImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    private void updateProfile() {
        mProgressBar.setVisibility(View.VISIBLE);
        setControlButtonsEnabled(false);
        String gender = mGenderRadioGroup.getCheckedRadioButtonId() == R.id.radio_male ? "Male" : "Female";
        Member.MemberBuilder memberBuilder = new Member.MemberBuilder(mAuth.getCurrentUser().getUid().toString(),mFullName.getText().toString(),gender,
                mDateOfBirth.getText().toString());
        memberBuilder.city(mCity.getText().toString());
        memberBuilder.state(mStateSpinner.getSelectedItem().toString());
        memberBuilder.religion(mReligionSpinner.getSelectedItem().toString());
        memberBuilder.occupation(mOccupation.getText().toString());
        memberBuilder.description(mDescription.getText().toString());
        Member member = memberBuilder.build();
        myRef.setValue(member,new DatabaseReference.CompletionListener(){

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    setControlButtonsEnabled(true);
                    Toast.makeText(ProfileActivity.this,getString(R.string.failed_updating_profile), LENGTH_SHORT).show();
                } else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    setControlButtonsEnabled(true);
                    Toast.makeText(ProfileActivity.this,getString(R.string.profile_saved_successfully), LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUI(Member member){
        mFullName.setText(member.mName);
        mGenderRadioGroup.check(member.mGender == "male" ? R.id.radio_male : R.id.radio_female);
        mDateOfBirth.setText(member.mDob);
        mCity.setText(member.mCity);
        int index = 0;
        for (int i=0;i<mStateSpinner.getCount();i++){
            if (mStateSpinner.getItemAtPosition(i).equals(member.mState)){
                index = i;
                break;
            }
        }
        mStateSpinner.setSelection(index);
        for (int i=0;i<mReligionSpinner.getCount();i++){
            if (mReligionSpinner.getItemAtPosition(i).equals(member.mReligion)){
                index = i;
                break;
            }
        }
        mReligionSpinner.setSelection(index);
        mOccupation.setText(member.mOccupation);
        mDescription.setText(member.mDescription);
    }

    private void setControlButtonsEnabled(boolean state){
        mFullName.setEnabled(state);
        mGenderRadioGroup.setEnabled(state);
        mDateOfBirth.setEnabled(state);
        mCity.setEnabled(state);
        mStateSpinner.setEnabled(state);
        mReligionSpinner.setEnabled(state);
        mOccupation.setEnabled(state);
        mDescription.setEnabled(state);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case RESULT_LOAD_IMG:
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(ProfileActivity.this.getContentResolver(), selectedImage);
                        mProfileImage.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                    break;
            }
        }
    }
}
