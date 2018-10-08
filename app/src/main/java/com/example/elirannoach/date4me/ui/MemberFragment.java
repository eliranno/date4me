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

public class MemberFragment extends Fragment {

    private ValueEventListener mMembersValueEventListener;
    private RecyclerView mMemberCardRecycleView;
    private List<Member> mMemberList;
    private MemberCardRecycleViewAdapter mMemberCardRecycleViewAdapter;
    private Member mMyProfile;


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.activity_main, container, false);
        mMemberCardRecycleView = fragmentView.findViewById(R.id.members_rv);
        mMemberCardRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMemberCardRecycleViewAdapter = new MemberCardRecycleViewAdapter(getContext());
        mMemberCardRecycleView.setAdapter(mMemberCardRecycleViewAdapter);
        if (savedInstanceState!=null){
            this.mMyProfile = savedInstanceState.getParcelable("myProfile");
            this.mMemberList = savedInstanceState.getParcelableArrayList("memberList");
            mMemberCardRecycleViewAdapter.updateMemberList(mMemberList,mMyProfile);
            mMemberCardRecycleViewAdapter.notifyDataSetChanged();
        }

        return fragmentView;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setData(List<Member> memberList, Member myProfile) {
        this.mMyProfile = myProfile;
        this.mMemberList = memberList;
        if(mMemberCardRecycleViewAdapter!=null)
            mMemberCardRecycleViewAdapter.updateMemberList(memberList,myProfile);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("memberList",(ArrayList<Member>)mMemberList);
        outState.putParcelable("myProfile",this.mMyProfile);
        super.onSaveInstanceState(outState);
    }

    public void updateData(){
        if(mMemberCardRecycleViewAdapter!=null)
            mMemberCardRecycleViewAdapter.notifyDataSetChanged();
    }


}
