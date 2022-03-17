package com.buzzware.apnigari.activities.home.dialog.bottomSheet;

import static com.buzzware.apnigari.retrofit.Controller.Base_Url;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.*;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.apnigari.activities.home.dialog.bottomSheet.adapter.SavedLocationAdapter;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.OnPredictedEvent;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.OnTextChangedEvent;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.autocomplete.AutoCompleteResponse;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.autocomplete.Prediction;
import com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.placedetails.PlaceDetailResponse;
import com.buzzware.apnigari.databinding.FragmentExpandablePlacesListBinding;
import com.buzzware.apnigari.retrofit.Controller;
import com.buzzware.apnigari.utils.AppConstants;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import im.delight.android.location.SimpleLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpandablePlacesListFragment extends BottomSheetDialogFragment implements SavedLocationAdapter.OnItemTappedListener {

    List<Prediction> locationModelList;

    SimpleLocation location;

    FragmentExpandablePlacesListBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentExpandablePlacesListBinding.inflate(inflater, container, false);

        setSheetBehaviour();

        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }


//    public float dpFromPx(final Context context, final float px) {
//        return px / context.getResources().getDisplayMetrics().density;
//    }

    private void setSheetBehaviour() {

        BottomSheetBehavior<LinearLayout> sheetBehavior = from(binding.bottomSheetLayout);

        sheetBehavior.setState(STATE_EXPANDED);

        sheetBehavior.setDraggable(true);

        sheetBehavior.setHideable(true);

        sheetBehavior.setPeekHeight(Float.valueOf(pxFromDp(Objects.requireNonNull(getActivity()), 150)).intValue());

    }


    @Override
    public void onStart() {

        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {

        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @org.greenrobot.eventbus.Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OnTextChangedEvent event) {

        getData(event.data, event.latLng);
    }

    ;

    @org.greenrobot.eventbus.Subscribe(threadMode = ThreadMode.MAIN)
    public void showBottomSheet(ShowBottomSheetMsg showBottomSheet) {

        BottomSheetBehavior<LinearLayout> sheetBehavior = from(binding.bottomSheetLayout);

        sheetBehavior.setState(STATE_EXPANDED);

    }

    ;


    @org.greenrobot.eventbus.Subscribe(threadMode = ThreadMode.MAIN)
    public void hideBottomSheet(HideBottomSheet showBottomSheet) {

        BottomSheetBehavior<LinearLayout> sheetBehavior = from(binding.bottomSheetLayout);

        sheetBehavior.setState(STATE_HIDDEN);

    }

    Call<String> retrofitCallback;

    @Subscribe
    private void getData(String data, String latLng) {

        if (retrofitCallback != null) {

            retrofitCallback.cancel();

            retrofitCallback = null;
        }

        String url = "/maps/api/place/autocomplete/json?input=" + data + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY + "&location=" + latLng + "&radius=50000&strictbounds=true";

        if (data == null) {

            url = "/maps/api/place/autocomplete/json?input=" + data + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY + "&location=" + latLng + "&radius=50000&strictbounds=true";
        }

        retrofitCallback = Controller.getApi(Base_Url).getPlaces(url, "asdasd");

        retrofitCallback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                retrofitCallback = null;

                Gson gson = new Gson();

                if (response.body() != null && response.isSuccessful()) {

                    AutoCompleteResponse autoCompleteResponse = gson.fromJson(response.body(), AutoCompleteResponse.class);

                    locationModelList = new ArrayList<>();

                    locationModelList.addAll(autoCompleteResponse.predictions);

                    setAdapter();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });

    }

    private void setAdapter() {

        SavedLocationAdapter savedLocationAdapter = new SavedLocationAdapter(getActivity(), locationModelList);

        binding.listPlacesRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.listPlacesRV.setAdapter(savedLocationAdapter);

        savedLocationAdapter.setOnItemTappedListener(this);
    }

    public float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onLocationSelected(Prediction prediction) {

        getPlaceDetail(prediction);

    }

    private void getPlaceDetail(Prediction prediction) {

        String url = "/maps/api/place/details/json?place_id=" + prediction.place_id + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        Controller.getApi(Base_Url).getPlaces(url, "asdasd")
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                        Gson gson = new Gson();

                        if (response.body() != null && response.isSuccessful()) {

                            PlaceDetailResponse placeDetail = gson.fromJson(response.body(), PlaceDetailResponse.class);

                            EventBus.getDefault().post(new OnPredictedEvent(placeDetail.result));

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                    }
                });

    }

    public static class ShowBottomSheetMsg {

    }

    public static class HideBottomSheet {

    }
}
