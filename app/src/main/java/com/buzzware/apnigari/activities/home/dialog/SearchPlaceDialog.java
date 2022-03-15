package com.buzzware.apnigari.activities.home.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.apnigari.activities.home.dialog.adapter.SearchPlaceAdapter;
import com.buzzware.apnigari.activities.home.vm.HomeViewModel;
import com.buzzware.apnigari.databinding.DialogSearchLocationBinding;

public class SearchPlaceDialog extends Dialog {

    DialogSearchLocationBinding binding;

    public SearchPlaceDialog(@NonNull Context context, HomeViewModel model) {


        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        binding = DialogSearchLocationBinding.inflate(LayoutInflater.from(context));

        setContentView(binding.getRoot());

        binding.searchRL.setLayoutManager(new LinearLayoutManager(context));

        binding.searchRL.setAdapter(new SearchPlaceAdapter());

        setFullScreen();

        model

    }

    private void setFullScreen() {

        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;

        window.setAttributes(wlp);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

}
