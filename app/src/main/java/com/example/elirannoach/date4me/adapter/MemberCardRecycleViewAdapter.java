package com.example.elirannoach.date4me.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.elirannoach.date4me.R;
import com.example.elirannoach.date4me.data.Member;

import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MemberCardRecycleViewAdapter extends RecyclerView.Adapter<MemberCardRecycleViewAdapter.CardViewHolder> {

    List<Member> mMemberList;
    Context mContext;

    public MemberCardRecycleViewAdapter(List<Member> mMemberList, Context mContext) {
        this.mMemberList = mMemberList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View memberCardView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_card, parent, false);
        CardViewHolder vh = new CardViewHolder(memberCardView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.mMemberFullName.append(mMemberList.get(position).mName);
        holder.mAge.append(mMemberList.get(position).mDob);
        holder.mOccupation.append(mMemberList.get(position).mOccupation);
    }

    @Override
    public int getItemCount() {
        return mMemberList.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView mCardMemberImage;
        private TextView mMemberFullName;
        private TextView mAge;
        private TextView mOccupation;
        private Button mViewMoreButton;
        private Button mMessageButton;

        public CardViewHolder(View itemView) {
            super(itemView);
            mCardMemberImage = itemView.findViewById(R.id.member_card_image);
            mMemberFullName = itemView.findViewById(R.id.card_member_fullname);
            mAge = itemView.findViewById(R.id.member_card_age);
            mOccupation = itemView.findViewById(R.id.member_card_occupation);
            mViewMoreButton = itemView.findViewById(R.id.member_card_view_more_button);
            mMessageButton = itemView.findViewById(R.id.member_card_text_button);
        }
    }
}
