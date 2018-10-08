package com.example.elirannoach.date4me.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.elirannoach.date4me.Database.DateContract;
import com.example.elirannoach.date4me.R;
import com.example.elirannoach.date4me.data.Member;
import com.example.elirannoach.date4me.ui.ChatActivity;
import com.example.elirannoach.date4me.ui.MainActivity;
import com.example.elirannoach.date4me.ui.ProfileActivity;
import com.example.elirannoach.date4me.utils.FireBaseUtils;
import com.example.elirannoach.date4me.utils.MemberHelper;
import com.example.elirannoach.date4me.utils.SharedPreferenceUtils;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberCardRecycleViewAdapter extends RecyclerView.Adapter<MemberCardRecycleViewAdapter.CardViewHolder> {

    private List<Member> mMemberList;
    private Context mContext;
    private Member mMyProfile;

    public MemberCardRecycleViewAdapter(List<Member> mMemberList, Context mContext) {
        this.mMemberList = mMemberList;
        this.mContext = mContext;

    }

    public MemberCardRecycleViewAdapter(Context mContext) {
        this.mContext = mContext;
        this.mMemberList = new ArrayList<>();
    }

    public void updateMemberList(List<Member> memberList,Member myProfile){
        mMemberList = memberList;
        mMyProfile = myProfile;
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
        holder.mLocation.setText(mContext.getString(R.string.location)+" " +mMemberList.get(position).mCity + " " + mMemberList.get(position).mState);
        String dob = mMemberList.get(position).mDob;
        String[] tokens = dob.split("\\.");
        holder.mAge.setText(mContext.getString(R.string.age)+" "+ MemberHelper.getAge(Integer.parseInt(tokens[2]),Integer.parseInt(tokens[0]),Integer.parseInt(tokens[1])));
        holder.mOccupation.setText(mContext.getString(R.string.occupaion)+" "+mMemberList.get(position).mOccupation);
        holder.mFavoriteButton.setBackgroundResource(mMemberList.get(position).isFavorite()? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_black_24dp );
        String url = mMemberList.get(position).mProfileImageUrl;
        final long ONE_MEGABYTE = 1024 * 1024;
        FireBaseUtils.getUserProfileImage(url,new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                holder.mCardMemberImage.setImageBitmap(imageBitmap);
                // size is too big need to compress
                //mMemberList.get(position).setProfilePhoto(bs.toByteArray());
            }
        },new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // place holder is already set in xml so don't do anything
            }
        });
        holder.mViewMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra("profile",mMemberList.get(position));
                mContext.startActivity(intent);
            }
        });
        holder.mMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // we will need this to get the conversation history between the logged in user and this user
                mContext.startActivity(new Intent(mContext, ChatActivity.class).putExtra("UUID",mMemberList.get(position).mUid));
            }
        });
        holder.mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add to Favorite Database
                if (holder.mFavoriteButton.getBackground().getConstantState() == mContext.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp).getConstantState())
                {
                    String uid = mMemberList.get(position).mUid;
                    ContentValues values = new ContentValues();
                    values.put(DateContract.FavoriteEntry.COLUMN_UID, uid);
                    Uri uri = mContext.getContentResolver().insert(DateContract.FavoriteEntry.CONTENT_URI, values);
                    holder.mFavoriteButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                }
                // Remove from Favorite Database
                else{
                    String uid = mMemberList.get(position).mUid;
                    Uri uri = DateContract.FavoriteEntry.CONTENT_URI;
                    int rowNumbers = mContext.getContentResolver().delete(uri,"uid  = ?",new String[]{uid});
                    if (rowNumbers > 0) {
                        holder.mFavoriteButton.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                        mMemberList.get(position).setFavorite(false);
                    }
                }
                Intent intent = new Intent();
                intent.setAction(MainActivity.FAVORITE_DB_CHANGE_ACTION);
                mContext.sendBroadcast(intent);
            }
        });
        holder.mMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Member member = mMemberList.get(position);
                String conversationID = member.getConversationID();
                if (conversationID==null){
                    conversationID = FireBaseUtils.getKeyForNewChild("conversations");
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("user-conversation/" + FireBaseUtils.getFireBaseUserUid()+ "/" + member.mUid, new UserConversation(conversationID,member.mUid));
                    childUpdates.put("user-conversation/" + member.mUid + "/" + FireBaseUtils.getFireBaseUserUid(),new UserConversation(conversationID,FireBaseUtils.getFireBaseUserUid()));
                    FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
                }
                Intent intent = new Intent(mContext,ChatActivity.class);
                intent.putExtra("conversationID",conversationID);
                intent.putExtra("member",mMemberList.get(position));
                intent.putExtra("myProfile",mMyProfile);
                mContext.startActivity(intent);
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
        private  Button mFavoriteButton;

        public CardViewHolder(View itemView) {
            super(itemView);
            mCardMemberImage = itemView.findViewById(R.id.member_card_image);
            mMemberFullName = itemView.findViewById(R.id.card_member_fullname);
            mAge = itemView.findViewById(R.id.member_card_age);
            mOccupation = itemView.findViewById(R.id.member_card_occupation);
            mLocation = itemView.findViewById(R.id.member_card_location);
            mViewMoreButton = itemView.findViewById(R.id.member_card_view_more_button);
            mMessageButton = itemView.findViewById(R.id.member_card_text_button);
            mFavoriteButton = itemView.findViewById(R.id.favorite_button);
        }
    }

    public static class UserConversation{
        public String conversationID;
        public String memberID;
        public UserConversation(){

        }

        public UserConversation(String conversationID,String memberID) {
            this.conversationID = conversationID;
            this.memberID = memberID;
        }
    }
}
