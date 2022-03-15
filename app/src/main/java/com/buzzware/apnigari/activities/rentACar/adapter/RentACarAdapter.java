package com.buzzware.apnigari.activities.rentACar.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.apnigari.activities.messages.chat.Chat;
import com.buzzware.apnigari.databinding.ItemChatBinding;
import com.buzzware.apnigari.databinding.ItemRentACarBinding;

public class RentACarAdapter extends RecyclerView.Adapter<RentACarAdapter.ViewHolder> {

    Context c;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (c == null)
            c = parent.getContext();

        ItemRentACarBinding binding = ItemRentACarBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.getRoot().setOnClickListener(view -> {

//            c.startActivity(new Intent(c, Chat.class));

        });

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemRentACarBinding binding;

        public ViewHolder(@NonNull ItemRentACarBinding binding) {

            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
