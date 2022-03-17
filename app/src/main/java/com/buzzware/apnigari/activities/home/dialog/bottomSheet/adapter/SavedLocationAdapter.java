package com.buzzware.apnigari.activities.home.dialog.bottomSheet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.autocomplete.Prediction;
import com.buzzware.apnigari.databinding.ItemSearchPlaceBinding;

import java.util.List;

public class SavedLocationAdapter extends RecyclerView.Adapter<SavedLocationHolder> {

    public Context context;

    List<Prediction> savedLocationModels;

    OnItemTappedListener onItemTappedListener;

    public SavedLocationAdapter(Context context, List<Prediction> savedLocationModels) {

        this.context = context;

        this.savedLocationModels = savedLocationModels;
    }

    @NonNull
    @Override
    public SavedLocationHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemSearchPlaceBinding binding = ItemSearchPlaceBinding.inflate(LayoutInflater.from(context),parent,false);

        return new SavedLocationHolder(binding);
    }

    @Override
    public void onBindViewHolder(final SavedLocationHolder holder, final int position) {

        Prediction prediction = savedLocationModels.get(position);

        holder.binding.titleTV.setText(prediction.structured_formatting.main_text);

        holder.binding.descTV.setText(prediction.description);

        holder.binding.getRoot().setOnClickListener(v -> onItemTappedListener.onLocationSelected(prediction));
    }

    @Override
    public int getItemCount() {
        return savedLocationModels.size();
    }

    public void setOnItemTappedListener(OnItemTappedListener onItemTappedListener) {
        this.onItemTappedListener = onItemTappedListener;
    }

    public interface OnItemTappedListener {
        void onLocationSelected(Prediction prediction);
    }
}

