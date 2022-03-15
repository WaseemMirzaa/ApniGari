package com.buzzware.apnigari.activities.carBookings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.apnigari.databinding.ItemCarBookingBinding;

public class CarBookingsAdapter extends RecyclerView.Adapter<CarBookingsAdapter.ViewHolder> {

    Context c;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (c == null)
            c = parent.getContext();

        ItemCarBookingBinding binding = ItemCarBookingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

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

        ItemCarBookingBinding binding;

        public ViewHolder(@NonNull ItemCarBookingBinding binding) {

            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
