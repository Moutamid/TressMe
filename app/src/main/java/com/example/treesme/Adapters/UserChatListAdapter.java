package com.example.treesme.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treesme.MessagesActivity;
import com.example.treesme.Model.Conversation;
import com.example.treesme.Model.User;
import com.example.treesme.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserChatListAdapter extends RecyclerView.Adapter<UserChatListAdapter.UserChatViewHolder>{

    private Context context;
    private List<User> userList;


    public UserChatListAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_list_row, parent, false);
        return new UserChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserChatViewHolder holder, int position) {
        User user = userList.get(position);
        holder.username.setText(user.getName());
        if (user.getImageUrl().equals("")){
            Picasso.with(context)
                    .load(R.drawable.profile)
                    .into(holder.avatar);
        }else{
            Picasso.with(context)
                    .load(user.getImageUrl())
                    .into(holder.avatar);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  clearUnreadChat(conversation.getChatWithId());
                Intent intent = new Intent(context, MessagesActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(MessagesActivity.EXTRAS_USER, user);
                intent.putExtra("userUid", user.getuId());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserChatViewHolder extends RecyclerView.ViewHolder{
        private TextView username;
        private TextView message;
        //private CircleImageView avatar,online,offline;
        private RelativeLayout layout;
        private TextView unreadCount;
        private TextView chatTime;
        private ImageView seenMsg,deliveredMsg,avatar;
        public UserChatViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.message);
            avatar = itemView.findViewById(R.id.profile);
            //online = itemView.findViewById(R.id.img_on);
            //offline = itemView.findViewById(R.id.img_off);
            layout = itemView.findViewById(R.id.layout_user_chat);
            //unreadCount = itemView.findViewById(R.id.arrival);
            chatTime = itemView.findViewById(R.id.msgTime);
            //seenMsg = itemView.findViewById(R.id.seen);
            //deliveredMsg = itemView.findViewById(R.id.delivered);
        }
    }
}
