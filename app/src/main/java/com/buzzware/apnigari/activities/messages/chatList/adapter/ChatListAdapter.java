package com.buzzware.apnigari.activities.messages.chatList.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.apnigari.activities.messages.chat.Chat;
import com.buzzware.apnigari.databinding.ItemChatBinding;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    Context c;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (c == null)
            c = parent.getContext();
        ItemChatBinding binding = ItemChatBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.getRoot().setOnClickListener(view -> {

            c.startActivity(new Intent(c, Chat.class));

        });

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemChatBinding binding;

        public ViewHolder(@NonNull ItemChatBinding binding) {

            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
