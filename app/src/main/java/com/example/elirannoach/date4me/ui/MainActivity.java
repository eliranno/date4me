package com.example.elirannoach.date4me.ui;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
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
import com.example.elirannoach.date4me.async.DatingCursorLoader;
import com.example.elirannoach.date4me.data.Member;
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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ValueEventListener mMembersValueEventListener;
    private DatingCursorLoader mDatingCursorLoader;
    private List<Member> mMemberList;
    private List<Member> mFavoriteList;
    private BroadcastReceiver mBroadcastReceiver;
    private MemberFragment mMemberListFragment;
    private MemberFragment mFavoriteMemberListFragment;

    private static final int  DATING_CURSOR_LOADER_ID = 1;
    public static final String FAVORITE_DB_CHANGE_ACTION = "favorite_db_change_action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        requestAllMembersInfo();
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getLoaderManager().restartLoader(DATING_CURSOR_LOADER_ID,null,MainActivity.this);
            }
        };
        mMemberListFragment = new MemberFragment();
        mFavoriteMemberListFragment = new MemberFragment();
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), new ArrayList<Fragment>(){{add(mFavoriteMemberListFragment);add(mMemberListFragment);}});
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

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.FAVORITE_DB_CHANGE_ACTION);
        registerReceiver(mBroadcastReceiver,filter);
    }

    @Override
    public void onPause() {
        FireBaseUtils.removeEventListener(FireBaseUtils.MEMBER_DB_KEY,mMembersValueEventListener);
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(mBroadcastReceiver);
    }

    private void requestAllMembersInfo() {
        mMembersValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMemberList = new ArrayList<>();
                mFavoriteList = new ArrayList<>();
                String userGender = SharedPreferenceUtils.getInstance(MainActivity.this).getStringValue(SharedPreferenceUtils.GENDER);
                for (DataSnapshot member : dataSnapshot.getChildren()){
                    Member memberObj = member.getValue(Member.class);
                    String gender = memberObj.mGender;
                    // same sex not allowed. TODO : create search perferences and filter.
                    if (!gender.equalsIgnoreCase(userGender))
                        mMemberList.add(member.getValue(Member.class));
                }
                processMemberList(mMemberList);
                // now that we have the member list get information from favorite database
                getLoaderManager().initLoader(DATING_CURSOR_LOADER_ID,null,  MainActivity.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        FireBaseUtils.readFromDatabaseReference(FireBaseUtils.MEMBER_DB_KEY,mMembersValueEventListener);
    }

    private void processMemberList(List<Member> memberList) {
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
        while(data.moveToNext()){
            String favoriteID = data.getString(data.getColumnIndex(DateContract.FavoriteEntry.COLUMN_UID));
            for (Member member : mMemberList){
                if (member.mUid.equals(favoriteID)){
                    member.setFavorite(true);
                    mFavoriteList.add(member);
                }
            }
        }
        mFavoriteMemberListFragment.setMemberList(mFavoriteList);
        mMemberListFragment.setMemberList(mMemberList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void clearAllFavorites(){
        for (Member member : mMemberList){
            member.setFavorite(false);
        }
        mFavoriteList.clear();
    }
}
