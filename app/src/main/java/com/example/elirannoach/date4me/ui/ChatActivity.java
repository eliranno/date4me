package com.example.elirannoach.date4me.ui;

import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.elirannoach.date4me.R;
import com.example.elirannoach.date4me.adapter.MessageRecycleViewAdapter;
import com.example.elirannoach.date4me.data.Member;
import com.example.elirannoach.date4me.utils.FireBaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends AppCompatActivity {
    private EditText mMessageEditText;
    private Button mSendButton;
    String conversationId;
    Member mMyProfile;
    Member mMember;
    List<com.example.elirannoach.date4me.data.Message> mMessageList;
    MessageRecycleViewAdapter mRecycleViewAdapter;
    RecyclerView mRecycleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mMessageEditText = findViewById(R.id.edittext_chatbox);
        mSendButton = findViewById(R.id.button_chatbox_send);
        mRecycleView = findViewById(R.id.reyclerview_message_list);
        if (savedInstanceState == null) {
            conversationId = getIntent().getStringExtra("conversationID");
            mMember = getIntent().getParcelableExtra("member");
            mMyProfile = getIntent().getParcelableExtra("myProfile");
        }
        mMessageList = new ArrayList<>();
        mRecycleViewAdapter = new MessageRecycleViewAdapter(this,mMessageList,mMyProfile,mMember,conversationId);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setAdapter(mRecycleViewAdapter);
/*
        FireBaseUtils.readFromDatabaseReference("conversations/" + conversationId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot message : dataSnapshot.getChildren()){
                    com.example.elirannoach.date4me.data.Message msgObj = message.getValue(com.example.elirannoach.date4me.data.Message.class);
                    mMessageList.add(msgObj);
                }
                mRecycleViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        */


        FireBaseUtils.addChildEventListener("conversations/" + conversationId, new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                com.example.elirannoach.date4me.data.Message msgObj = dataSnapshot.getValue(com.example.elirannoach.date4me.data.Message.class);
                mMessageList.add(msgObj);
                mRecycleViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mMessageEditText.getText().toString();
                String key = FireBaseUtils.getKeyForNewChild("conversations/"+ conversationId);
                com.example.elirannoach.date4me.data.Message newMessage = new com.example.elirannoach.date4me.data.Message(mMyProfile.mUid,mMember.mUid,key,message);
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("conversations").child(conversationId).child(key).setValue(newMessage);
                mMessageEditText.setText("");
            }
        });

    }
}
