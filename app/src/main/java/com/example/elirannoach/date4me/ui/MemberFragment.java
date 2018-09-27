package com.example.elirannoach.date4me.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.elirannoach.date4me.Database.DateContract;
import com.example.elirannoach.date4me.R;
import com.example.elirannoach.date4me.adapter.MemberCardRecycleViewAdapter;
import com.example.elirannoach.date4me.async.DatingCursorLoader;
import com.example.elirannoach.date4me.data.Member;
import com.example.elirannoach.date4me.utils.FireBaseUtils;
import com.example.elirannoach.date4me.utils.SharedPreferenceUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MemberFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private ValueEventListener mMembersValueEventListener;
    private RecyclerView mMemberCardRecycleView;
    private DatingCursorLoader mDatingCursorLoader;
    private List<Member> mMemberList;
    private MemberCardRecycleViewAdapter mMemberCardRecycleViewAdapter;
    private BroadcastReceiver mBroadcastReceiver;

    private static final int  DATING_CURSOR_LOADER_ID = 1;


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.activity_main, container, false);
        mMemberCardRecycleView = fragmentView.findViewById(R.id.members_rv);
        mMemberCardRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        return fragmentView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestAllMembersInfo();
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getLoaderManager().restartLoader(DATING_CURSOR_LOADER_ID,null,MemberFragment.this);
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.FAVORITE_DB_CHANGE_ACTION);
        getActivity().registerReceiver(mBroadcastReceiver,filter);
    }

    private void requestAllMembersInfo() {
        mMembersValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMemberList = new ArrayList<>();
                String userGender = SharedPreferenceUtils.getInstance(getContext()).getStringValue(SharedPreferenceUtils.GENDER);
                for (DataSnapshot member : dataSnapshot.getChildren()){
                    Member memberObj = member.getValue(Member.class);
                    String gender = memberObj.mGender;
                    // same sex not allowed. TODO : create search perferences and filter.
                    if (!gender.equalsIgnoreCase(userGender))
                        mMemberList.add(member.getValue(Member.class));
                }
                processMemberList(mMemberList);
                // get information from favorite database
                getLoaderManager().initLoader(DATING_CURSOR_LOADER_ID,null,  MemberFragment.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        FireBaseUtils.readFromDatabaseReference(FireBaseUtils.MEMBER_DB_KEY,mMembersValueEventListener);
    }

    private void processMemberList(List<Member> memberList) {
        mMemberCardRecycleViewAdapter = new MemberCardRecycleViewAdapter(memberList,getContext());
        mMemberCardRecycleView.setAdapter(mMemberCardRecycleViewAdapter);
    }

    @Override
    public void onPause() {
        FireBaseUtils.removeEventListener(FireBaseUtils.MEMBER_DB_KEY,mMembersValueEventListener);
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    @NonNull
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        mDatingCursorLoader = new DatingCursorLoader(getContext(), DateContract.FavoriteEntry.CONTENT_URI,null,null,null,null);
        return mDatingCursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        clearAllFavorites();
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

    private void clearAllFavorites(){
        for (Member member : mMemberList){
            member.setFavorite(false);
        }
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {

    }
}
