package com.example.elirannoach.date4me.ui;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.elirannoach.date4me.Database.DateContract;
import com.example.elirannoach.date4me.R;
import com.example.elirannoach.date4me.adapter.MemberCardRecycleViewAdapter;
import com.example.elirannoach.date4me.async.DatingCursorLoader;
import com.example.elirannoach.date4me.data.Member;
import com.example.elirannoach.date4me.receivers.AutoStart;
import com.example.elirannoach.date4me.service.MessageService;
import com.example.elirannoach.date4me.utils.FireBaseUtils;
import com.example.elirannoach.date4me.utils.SharedPreferenceUtils;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.elirannoach.date4me.service.MessageService.NEW_CONVERSATION_ADDED;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ValueEventListener mMembersValueEventListener;
    private DatingCursorLoader mDatingCursorLoader;
    private List<Member> mMemberList;
    private List<Member> mFavoriteList;
    private Member mMyProfile;
    private BroadcastReceiver mBroadcastReceiver;
    private BroadcastReceiver mUserConversationBroadcastReceiver;
    private MemberFragment mMemberListFragment;
    private MemberFragment mFavoriteMemberListFragment;
    private MessageService.ServiceBinder mMessageServiceBound;
    private ServiceConnection mServiceConnection;
    private boolean mIsBound = false;

    private static final int  DATING_CURSOR_LOADER_ID = 1;
    public static final String FAVORITE_DB_CHANGE_ACTION = "favorite_db_change_action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getLoaderManager().restartLoader(DATING_CURSOR_LOADER_ID,null,MainActivity.this);
            }
        };
        //startService(new Intent(this, MessageService.class));
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mMessageServiceBound = (MessageService.ServiceBinder)service;
                mIsBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(new Intent(this, MessageService.class), mServiceConnection,Context.BIND_AUTO_CREATE);

        mUserConversationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String conversationID = intent.getStringExtra("ConversationID");
                String memberID = intent.getStringExtra("memberID");
                for (Member member : mMemberList)
                    if (member.mUid.equals(memberID)){
                        member.setmConversationID(conversationID);
                    }
            }
        };
        if (savedInstanceState == null) {
            requestAllMembersInfo();
            mMemberList = new ArrayList<>();
            mFavoriteList = new ArrayList<>();
            mMemberListFragment = new MemberFragment();
            mFavoriteMemberListFragment = new MemberFragment();
            final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            final PagerAdapter adapter = new PagerAdapter
                    (getSupportFragmentManager(), new ArrayList<Fragment>() {{
                        add(mMemberListFragment);
                        add(mFavoriteMemberListFragment);
                    }});
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
        else {
            mMemberList = savedInstanceState.getParcelableArrayList("memberList");
            mFavoriteList = savedInstanceState.getParcelableArrayList("favoriteList");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.FAVORITE_DB_CHANGE_ACTION);
        registerReceiver(mBroadcastReceiver,filter);
        IntentFilter filter2 =  new IntentFilter();
        filter2.addAction(NEW_CONVERSATION_ADDED);
        registerReceiver(mUserConversationBroadcastReceiver,filter2);
    }

    @Override
    public void onPause() {
        if (mMembersValueEventListener!=null)
            FireBaseUtils.removeEventListener(FireBaseUtils.MEMBER_DB_KEY,mMembersValueEventListener);
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mIsBound) {
            this.unbindService(mServiceConnection);
            mIsBound = false;
        }
        unregisterReceiver(mBroadcastReceiver);
        unregisterReceiver(mUserConversationBroadcastReceiver);
    }

    private void requestAllMembersInfo() {
        mMembersValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userGender = SharedPreferenceUtils.getInstance(MainActivity.this).getStringValue(SharedPreferenceUtils.GENDER);
                for (DataSnapshot member : dataSnapshot.getChildren()){
                    Member memberObj = member.getValue(Member.class);
                    String gender = memberObj.mGender;
                    // same sex not allowed. TODO : create search perferences and filter.
                    if (!gender.equalsIgnoreCase(userGender)) {
                        Member newMember = member.getValue(Member.class);
                        if (mMessageServiceBound !=null)
                            newMember.setmConversationID(mMessageServiceBound.getUserConversationMap().get(newMember.mUid));
                        mMemberList.add(newMember);
                    }
                    if (memberObj.mUid.equals(FireBaseUtils.getFireBaseUserUid()))
                        mMyProfile = memberObj;
                }
                mFavoriteMemberListFragment.setData(mFavoriteList,mMyProfile);
                mMemberListFragment.setData(mMemberList,mMyProfile);
                // last step get favorites from database
                getLoaderManager().initLoader(DATING_CURSOR_LOADER_ID,null,  MainActivity.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        FireBaseUtils.readFromDatabaseReference(FireBaseUtils.MEMBER_DB_KEY,mMembersValueEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_action_bar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_signout:
                onSignoutClicked();
                break;
            case R.id.action_my_profile:
                startActivity(new Intent(MainActivity.this,ProfileActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void onSignoutClicked() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        mDatingCursorLoader = new DatingCursorLoader(MainActivity.this, DateContract.FavoriteEntry.CONTENT_URI,null,null,null,null);
        return mDatingCursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        clearAllFavorites();
        while (data.moveToNext()) {
            String favoriteID = data.getString(data.getColumnIndex(DateContract.FavoriteEntry.COLUMN_UID));
            for (Member member : mMemberList) {
                if (member.mUid.equals(favoriteID)) {
                    member.setFavorite(true);
                    mFavoriteList.add(member);
                }
            }
        }
        mMemberListFragment.updateData();
        mFavoriteMemberListFragment.updateData();
        AppWidget.updateWidget(this, mFavoriteList.size());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("memberList",(ArrayList)mMemberList);
        outState.putParcelableArrayList("favoriteList",(ArrayList)mFavoriteList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void clearAllFavorites(){
        for (Member member : mMemberList){
            member.setFavorite(false);
        }
        mFavoriteList.clear();
    }

}
