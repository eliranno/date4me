package com.example.elirannoach.date4me.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.elirannoach.date4me.R;
import com.example.elirannoach.date4me.data.Member;
import com.example.elirannoach.date4me.ui.ChatActivity;
import com.example.elirannoach.date4me.utils.FireBaseUtils;
import com.example.elirannoach.date4me.utils.MemberHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MemberCardRecycleViewAdapter extends RecyclerView.Adapter<MemberCardRecycleViewAdapter.CardViewHolder> {

    private List<Member> mMemberList;
    private Context mContext;

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
    public void onBindViewHolder(@NonNull final CardViewHolder holder, final int position) {
        holder.mMemberFullName.setText(mMemberList.get(position).mName);
        holder.mLocation.append(" " +mMemberList.get(position).mCity + " " + mMemberList.get(position).mState);
        String dob = mMemberList.get(position).mDob;
        String[] tokens = dob.split("\\.");
        holder.mAge.append(" "+ MemberHelper.getAge(Integer.parseInt(tokens[2]),Integer.parseInt(tokens[0]),Integer.parseInt(tokens[1])));
        holder.mOccupation.append(" "+mMemberList.get(position).mOccupation);
        String url = mMemberList.get(position).mProfileImageUrl;
        final long ONE_MEGABYTE = 1024 * 1024;
        FireBaseUtils.getUserProfileImage(url,new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                holder.mCardMemberImage.setImageBitmap(imageBitmap);
            }
        },new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // place holder is already set in xml so don't do anything
            }
        });
        holder.mMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // we will need this to get the conversation history between the logged in user and this user
                mContext.startActivity(new Intent(mContext, ChatActivity.class).putExtra("UUID",mMemberList.get(position).mUid));
            }
        });
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
        private TextView mLocation;
        private Button mViewMoreButton;
        private Button mMessageButton;

        public CardViewHolder(View itemView) {
            super(itemView);
            mCardMemberImage = itemView.findViewById(R.id.member_card_image);
            mMemberFullName = itemView.findViewById(R.id.card_member_fullname);
            mAge = itemView.findViewById(R.id.member_card_age);
            mOccupation = itemView.findViewById(R.id.member_card_occupation);
            mLocation = itemView.findViewById(R.id.member_card_location);
            mViewMoreButton = itemView.findViewById(R.id.member_card_view_more_button);
            mMessageButton = itemView.findViewById(R.id.member_card_text_button);
        }
    }
}
