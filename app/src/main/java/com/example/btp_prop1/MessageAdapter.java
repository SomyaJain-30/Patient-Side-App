package com.example.btp_prop1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.auth.User;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT =1;
    private Context context;
    private List<MessageModel> messageList;

    public MessageAdapter(Context context, List<MessageModel> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            return new MessageAdapter.UserViewHolder(LayoutInflater.from(context).inflate(R.layout.message_item_user,parent,false));
        }else{
            return new MessageAdapter.ChatBotViewHolder(LayoutInflater.from(context).inflate(R.layout.message_item_chatbot, parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageList.get(position);
        if(holder instanceof UserViewHolder)
        {
            UserViewHolder holder1 = (UserViewHolder) holder;
            if(!messageModel.getMessage().isEmpty()){
                holder1.message.setText(messageModel.getMessage());
            }
        }
        else
        {
            ChatBotViewHolder cvh = (ChatBotViewHolder) holder;
            cvh.message.setText(messageModel.getMessage());

            if(!cvh.message.getText().toString().equals(""))
            {
                cvh.message.setVisibility(View.VISIBLE);
                cvh.load.setVisibility(View.INVISIBLE);
            }
            else
            {
                cvh.message.setVisibility(View.INVISIBLE);
                cvh.load.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        TextView message;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chatbot_message);
        }
    }

    public class ChatBotViewHolder extends RecyclerView.ViewHolder {

        TextView message;
        ImageView load;

        public ChatBotViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chatbot_message);
            load = itemView.findViewById(R.id.gif);
            Glide.with(context).load(R.drawable.typingstatus).into(load);
        }
    }



    @Override
    public int getItemViewType(int position) {
        if(messageList.get(position).getSender().equals("user")){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}
