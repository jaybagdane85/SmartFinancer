package com.example.smartfinancer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        if (message.getType() == ChatMessage.TYPE_USER) {
            holder.layoutUserMessage.setVisibility(View.VISIBLE);
            holder.layoutBotMessage.setVisibility(View.GONE);
            holder.tvUserMessage.setText(message.getMessage());
        } else {
            holder.layoutUserMessage.setVisibility(View.GONE);
            holder.layoutBotMessage.setVisibility(View.VISIBLE);
            holder.tvBotMessage.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layoutUserMessage, layoutBotMessage;
        TextView tvUserMessage, tvBotMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutUserMessage = itemView.findViewById(R.id.layout_user_message);
            layoutBotMessage = itemView.findViewById(R.id.layout_bot_message);
            tvUserMessage = itemView.findViewById(R.id.tv_user_message);
            tvBotMessage = itemView.findViewById(R.id.tv_bot_message);
        }
    }
}

