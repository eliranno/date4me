package com.example.elirannoach.date4me.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.elirannoach.date4me.Database.DateContract;
import com.example.elirannoach.date4me.R;
import com.example.elirannoach.date4me.adapter.MemberCardRecycleViewAdapter;
import com.example.elirannoach.date4me.async.DatingCursorLoader;
import com.example.elirannoach.date4me.data.Member;
import com.example.elirannoach.date4me.utils.FireBaseUtils;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ValueEventListener mMembersValueEventListener;
    private RecyclerView mMemberCardRecycleView;
    private CursorLoader mDatingCursorLoader;
    private List<Member> mMemberList;
    private MemberCardRecycleViewAdapter mMemberCardRecycleViewAdapter;

    private static final int  DATING_CURSOR_LOADER_ID = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMemberCardRecycleView = findViewById(R.id.members_rv);
        mMemberCardRecycleView.setLayoutManager(new LinearLayoutManager(this));
        requestAllMembersInfo();
    }

    private void requestAllMembersInfo() {
        mMembersValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMemberList = new ArrayList<>();
                for (DataSnapshot member : dataSnapshot.getChildren()){
                    mMemberList.add(member.getValue(Member.class));
                }
                processMemberList(mMemberList);
                // get information from favorite database
                getLoaderManager().initLoader(DATING_CURSOR_LOADER_ID,null,MainActivity.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        FireBaseUtils.readFromDatabaseReference(FireBaseUtils.MEMBER_DB_KEY,mMembersValueEventListener);
    }

    private void processMemberList(List<Member> memberList) {
        mMemberCardRecycleViewAdapter = new MemberCardRecycleViewAdapter(memberList,this);
        mMemberCardRecycleView.setAdapter(mMemberCardRecycleViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_action_bar_menu, menu);

        return super.onCreateOptionsMenu(menu);
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

    @Override
    protected void onPause() {
        FireBaseUtils.removeEventListener(FireBaseUtils.MEMBER_DB_KEY,mMembersValueEventListener);
        super.onPause();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mDatingCursorLoader = new DatingCursorLoader(this, DateContract.FavoriteEntry.CONTENT_URI,null,null,null,null);
        return mDatingCursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        while(data.moveToNext()){
            String favoriteID = data.getString(data.getColumnIndex(DateContract.FavoriteEntry.COLUMN_UID));
            for (Member member : mMemberList){
                if (member.mUid.equals(favoriteID)){
                    member.setFavorite(true);
                }
            }
        }
        mMemberCardRecycleViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
