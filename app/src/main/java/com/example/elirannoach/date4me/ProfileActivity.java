package com.example.elirannoach.date4me;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
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

    // Google Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ValueEventListener mProfileEventListener;
    private FirebaseStorage mStorage;
    private DatabaseReference.CompletionListener mDatabaseListerner;

    private  Member mMemberDetails;
    // static members
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
        mStorage = FirebaseStorage.getInstance();
        mEditProfilePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectProfileImage();

            }
        });

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                setControlButtonsEnabled(false);
                uploadProfieImage();
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
        // get user profile info from realtime database
        myRef.addListenerForSingleValueEvent(mProfileEventListener);
    }

    private void selectProfileImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), RESULT_LOAD_IMG);
    }

    private void updateProfileInfo(Member member) {
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

    private void uploadProfieImage() {
        // Create a storage reference from our app
        StorageReference storageRef = mStorage.getReference();

        // Create a reference to 'images/uid'
        final StorageReference storageReference = storageRef.child("images/"+mAuth.getCurrentUser().getUid()+".jpg");

        // Get the data from an ImageView as bytes
        mProfileImage.setDrawingCacheEnabled(true);
        mProfileImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) mProfileImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ProfileActivity.this,getString(R.string.failed_updating_profile), LENGTH_SHORT).show();
                setControlButtonsEnabled(true);

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String url = taskSnapshot.getMetadata().getPath();
                String gender = mGenderRadioGroup.getCheckedRadioButtonId() == R.id.radio_male ? "Male" : "Female";
                Member.MemberBuilder memberBuilder = new Member.MemberBuilder(mAuth.getCurrentUser().getUid().toString(),mFullName.getText().toString(),gender,
                        mDateOfBirth.getText().toString());
                memberBuilder.city(mCity.getText().toString());
                memberBuilder.state(mStateSpinner.getSelectedItem().toString());
                memberBuilder.religion(mReligionSpinner.getSelectedItem().toString());
                memberBuilder.occupation(mOccupation.getText().toString());
                memberBuilder.description(mDescription.getText().toString());
                memberBuilder.profileImageUrl(url);
                Member member = memberBuilder.build();
               updateProfileInfo(member);


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
        String url = member.mProfileImageUrl;
        StorageReference gsReference = mStorage.getReference().child(url);
        final long ONE_MEGABYTE = 1024 * 1024;
        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                mProfileImage.setImageBitmap(imageBitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
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
                        mProfileImage.setRotation(90);
                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                    break;
            }
        }
    }
}
