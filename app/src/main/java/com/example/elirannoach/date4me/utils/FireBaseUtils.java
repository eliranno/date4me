package com.example.elirannoach.date4me.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.example.elirannoach.date4me.data.Member;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class FireBaseUtils {

    public final static String MEMBER_DB_KEY = "member";



    public synchronized static void UploadProfileImage(ImageView profileImage, OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListener, OnFailureListener onFailureListener) {
        // Create Firebase Storage and Authentication instances
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference storageReference = storageRef.child("images/"+auth.getCurrentUser().getUid()+".jpg");
        // Get the data from an ImageView as bytes
        profileImage.setDrawingCacheEnabled(true);
        profileImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        // now that we serialized the image to array of bytes let's create an upload task
        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(onFailureListener);
        uploadTask.addOnSuccessListener(onSuccessListener);
    }

    public synchronized static void UploadProfileInfo(Member member, DatabaseReference.CompletionListener completionListener){
        // Create Firebase Real Database instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference memberDatabaseReference = database.getReference(MEMBER_DB_KEY+"/"+auth.getCurrentUser().getUid());
        memberDatabaseReference.setValue(member,completionListener);
    }

    public synchronized static void readFromDatabaseReference(String referencePath, ValueEventListener valueEventListener){
        // Create Firebase Real Database instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference memberDatabaseReference = database.getReference(referencePath);
        memberDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    public synchronized static void readAndListenFromDatabaseReference(String referencePath, ValueEventListener valueEventListener){
        // Create Firebase Real Database instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference memberDatabaseReference = database.getReference(referencePath);
        memberDatabaseReference.addValueEventListener(valueEventListener);
    }

    public synchronized static void addChildEventListener(String referencePath, ChildEventListener childEventListener){
        // Create Firebase Real Database instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference memberDatabaseReference = database.getReference(referencePath);
        memberDatabaseReference.addChildEventListener(childEventListener);
    }

    public static synchronized String getFireBaseUserUid(){
        return FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
    }

    public static synchronized void getUserProfileImage(String imageUrl,OnSuccessListener onSuccessListener,OnFailureListener onFailureListener){
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference gsReference = firebaseStorage.getReference().child(imageUrl);
        final long ONE_MEGABYTE = 1024 * 1024;
        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);

    }

    public static synchronized void removeEventListener(String referencePath,ValueEventListener valueEventListener){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference memberDatabaseReference = database.getReference(referencePath);
        memberDatabaseReference.removeEventListener(valueEventListener);
    }

    public static synchronized String getKeyForNewChild(String referencePath){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference memberDatabaseReference = database.getReference();
        return memberDatabaseReference.child(referencePath).push().getKey();
    }

}
