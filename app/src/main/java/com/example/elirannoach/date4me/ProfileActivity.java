package com.example.elirannoach.date4me;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ValueEventListener mProfileEventListener;
    private  Member mMemberDetails;

    private final static String MEMBER_DB_KEY = "member";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        mProgressBar.setVisibility(View.INVISIBLE);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference(MEMBER_DB_KEY+"/"+mAuth.getCurrentUser().getUid());
        


        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchMemberProfileInfo();
            }
        });
        mProfileEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Member member = dataSnapshot.getValue(Member.class);
                if(member!=null)
                    updateUI(member);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this,getString(R.string.failed_loading_profile),LENGTH_SHORT).show();
            }
        };
        myRef.addListenerForSingleValueEvent(mProfileEventListener);
    }

    private void fetchMemberProfileInfo() {
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
                    Toast.makeText(ProfileActivity.this,getString(R.string.failed_updating_profile), LENGTH_SHORT).show();
                } else {
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
}
