package com.example.elirannoach.date4me.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.elirannoach.date4me.R;
import com.example.elirannoach.date4me.adapter.MessageRecycleViewAdapter;
import com.example.elirannoach.date4me.data.Member;
import com.example.elirannoach.date4me.service.MessageService;
import com.example.elirannoach.date4me.utils.FireBaseUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static com.example.elirannoach.date4me.service.MessageService.NEW_MESSAGE_ACTION;


public class ChatActivity extends AppCompatActivity {
    private EditText mMessageEditText;
    private Button mSendButton;
    private String conversationId;
    private Member mMyProfile;
    private Member mMember;
    private List<com.example.elirannoach.date4me.data.Message> mMessageList;
    private MessageRecycleViewAdapter mRecycleViewAdapter;
    private RecyclerView mRecycleView;
    private MessageService.ServiceBinder mBinder;
    private BroadcastReceiver mBroadcastReceiver;
    private boolean mIsBind = false;
    private ServiceConnection mConnectionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mMessageEditText = findViewById(R.id.edittext_chatbox);
        mSendButton = findViewById(R.id.button_chatbox_send);
        mRecycleView = findViewById(R.id.reyclerview_message_list);
        if (savedInstanceState == null) {
            conversationId = getIntent().getStringExtra("conversationID");
            mMember = getIntent().getParcelableExtra("member");
            mMyProfile = getIntent().getParcelableExtra("myProfile");
        }
        else{
            conversationId = savedInstanceState.getString("conversationID");
            mMember = savedInstanceState.getParcelable("member");
            mMyProfile = savedInstanceState.getParcelable("myProfile");
        }
        mRecycleViewAdapter = new MessageRecycleViewAdapter(this,mMessageList,mMyProfile,mMember,conversationId);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));

        mConnectionService = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBinder = (MessageService.ServiceBinder) service;
                mMessageList = mBinder.getConversationMap().get(conversationId);
                mRecycleViewAdapter = new MessageRecycleViewAdapter(ChatActivity.this,mMessageList,mMyProfile,mMember,conversationId);
                mRecycleView.setAdapter(mRecycleViewAdapter);
                mRecycleViewAdapter.notifyDataSetChanged();
                mIsBind = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        bindService(new Intent(this,MessageService.class), mConnectionService,BIND_AUTO_CREATE);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = mMessageEditText.getText().toString();
                new AsyncTask<Void, Void, Void>(){

                    @Override
                    protected Void doInBackground(Void... voids) {
                        String key = FireBaseUtils.getKeyForNewChild("conversations/"+ conversationId);
                        com.example.elirannoach.date4me.data.Message newMessage = new com.example.elirannoach.date4me.data.Message(mMyProfile.mUid,mMember.mUid,key,message);
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("conversations").child(conversationId).child(key).setValue(newMessage);
                        mMessageEditText.setText("");
                        return null;
                    }

                }.execute();
            }
        });

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (conversationId.equals(intent.getStringExtra("ConversationID"))){
                    mRecycleViewAdapter.notifyDataSetChanged();
                }
            }
        };

        FireBaseUtils.getUserProfileImage(mMyProfile.mProfileImageUrl,new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                mMyProfile.setProfilePhoto(bytes);
                mRecycleViewAdapter.notifyDataSetChanged();
            }
        },new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // place holder is already set in xml so don't do anything
            }
        });

        FireBaseUtils.getUserProfileImage(mMember.mProfileImageUrl,new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                mMember.setProfilePhoto(bytes);
                mRecycleViewAdapter.notifyDataSetChanged();
            }
        },new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // place holder is already set in xml so don't do anything
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this,MainActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("conversationID",conversationId);
        outState.putParcelable("myProfile",mMyProfile);
        outState.putParcelable("member",mMember);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filer = new IntentFilter();
        filer.addAction(NEW_MESSAGE_ACTION);
        registerReceiver(mBroadcastReceiver,filer);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mIsBind){
            unbindService(mConnectionService);
            mIsBind = false;
        }
        unregisterReceiver(mBroadcastReceiver);
    }
}
