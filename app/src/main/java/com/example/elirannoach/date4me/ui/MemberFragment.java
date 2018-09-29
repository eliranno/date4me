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



    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.activity_main, container, false);
        mMemberCardRecycleView = fragmentView.findViewById(R.id.members_rv);
        mMemberCardRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMemberCardRecycleView.setAdapter(mMemberCardRecycleViewAdapter);
        return fragmentView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMemberCardRecycleViewAdapter = new MemberCardRecycleViewAdapter(getContext());
    }


    public void setMemberList(List<Member> memberList) {
        mMemberCardRecycleViewAdapter.updateMemberList(memberList);
    }



}
