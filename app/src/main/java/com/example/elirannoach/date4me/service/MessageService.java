package com.example.elirannoach.date4me.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.elirannoach.date4me.adapter.MemberCardRecycleViewAdapter;
import com.example.elirannoach.date4me.data.Message;
import com.example.elirannoach.date4me.utils.FireBaseUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageService extends Service {

    private Map<String,List<Message>> mConversationMap;
    private Map<String,String> mUserConversationMap;
    private IBinder mBinder;
    private boolean mStartup;

    public static final String NEW_MESSAGE_ACTION = "new_message_action";
    public static final String NEW_CONVERSATION_ADDED = "new_conversation_added";

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new ServiceBinder();
        mConversationMap = new HashMap<>();
        mUserConversationMap = new HashMap<>();
        getConversations();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void getConversations() {
        FireBaseUtils.addChildEventListener("user-conversation/"+FireBaseUtils.getFireBaseUserUid(), new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    MemberCardRecycleViewAdapter.UserConversation userConversation= dataSnapshot.getValue(MemberCardRecycleViewAdapter.UserConversation.class);
                    final String conversationID = userConversation.conversationID;
                    final String memberID = userConversation.memberID;
                mUserConversationMap.put(memberID,conversationID);
                    Intent newConversationIntent = new Intent(NEW_CONVERSATION_ADDED);
                    newConversationIntent.putExtra("ConversationID",conversationID);
                newConversationIntent.putExtra("memberID",memberID);
                    sendBroadcast(newConversationIntent);
                    FireBaseUtils.addChildEventListener("conversations/" + conversationID, new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                Message msg = dataSnapshot.getValue(Message.class);
                                if (mConversationMap.containsKey(conversationID)){
                                    mConversationMap.get(conversationID).add(msg);
                                }
                                else{
                                    List<Message> messageList = new ArrayList();
                                    messageList.add(msg);
                                    mConversationMap.put(conversationID,messageList);
                                }
                                Intent newMessageIntent = new Intent(NEW_MESSAGE_ACTION);
                                newMessageIntent.putExtra("ConversationID",conversationID);
                                sendBroadcast(newMessageIntent);
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
    }

    public class ServiceBinder extends Binder {
        public Map<String,List<Message>> getConversationMap() {
            return mConversationMap;
        }
        public Map<String,String> getUserConversationMap(){
            return mUserConversationMap;
        }
    }
}
