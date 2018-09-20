package com.example.elirannoach.date4me.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.elirannoach.date4me.R;
import com.example.elirannoach.date4me.adapter.MemberCardRecycleViewAdapter;
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

public class MainActivity extends AppCompatActivity {

    ValueEventListener mMembersValueEventListener;
    RecyclerView mMemberCardRecycleView;


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
                List<Member> memberList = new ArrayList<>();
                for (DataSnapshot member : dataSnapshot.getChildren()){
                    memberList.add(member.getValue(Member.class));
                }
                processMemberList(memberList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        FireBaseUtils.readFromDatabaseReference(FireBaseUtils.MEMBER_DB_KEY,mMembersValueEventListener);
    }

    private void processMemberList(List<Member> memberList) {
        mMemberCardRecycleView.setAdapter(new MemberCardRecycleViewAdapter(memberList,this));
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
}
