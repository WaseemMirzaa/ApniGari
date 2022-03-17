package com.buzzware.apnigari.activities.messages.chatList.adapter.holder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.apnigari.databinding.ItemChatBinding;

public class ChatListViewHolder extends RecyclerView.ViewHolder {

    public ItemChatBinding binding;

    public ChatListViewHolder(@NonNull ItemChatBinding binding) {

        super(binding.getRoot());

        this.binding = binding;
    }
}
