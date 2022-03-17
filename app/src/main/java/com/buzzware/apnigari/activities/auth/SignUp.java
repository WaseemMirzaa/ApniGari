package com.buzzware.apnigari.activities.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.buzzware.apnigari.activities.select.SelectScreen;
import com.buzzware.apnigari.activities.base.BaseActivity;
import com.buzzware.apnigari.activities.auth.vm.AuthViewModel;
import com.buzzware.apnigari.activities.auth.vm.mo.User;
import com.buzzware.apnigari.databinding.ActivitySignUpBinding;
import com.buzzware.apnigari.generic.GenericModelLiveData;

public class SignUp extends BaseActivity {

    ActivitySignUpBinding binding;

    AuthViewModel model;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        init();
        
        setListeners();
    }

    private void setListeners() {

        binding.loginTV.setOnClickListener(view -> moveToLoginActivity());
        
        binding.btnContinue.setOnClickListener(view -> SelectScreen.start(SignUp.this));

    }

    private void signUp() {
        
        if(validate()) {

            User user = getUser();

            model.createUserWithEmailAndPassword(user, SignUp.this);

        }
        
    }

    private User getUser() {

        User user = new User();
        user.homeAddress = "";
        user.workAddress = "";
        user.email = binding.emailET.getText().toString();
        user.firstName = binding.fNameET.getText().toString();
        user.lastName = binding.lNameET.getText().toString();
        user.phoneNumber = binding.phoneET.getText().toString();
        user.password = binding.passwordET.getText().toString();
        user.city = "";
        user.state = "";
        user.zipcode = "";

        return user;
    }

    private boolean validate() {

        if (binding.fNameET.getText().toString().isEmpty()) {

            showErrorAlert("First Name Required");

            return false;
        }

        if (binding.lNameET.getText().toString().isEmpty()) {

            showErrorAlert("Last Name Required");

            return false;
        }

        if (binding.emailET.getText().toString().isEmpty()) {

            showErrorAlert("Email Required");

            return false;
        }

        if (binding.phoneET.getText().toString().isEmpty()) {

            showErrorAlert("Phone Required");

            return false;
        }

        if (binding.passwordET.getText().toString().isEmpty()) {

            showErrorAlert("Password Required");

            return false;
        }

        return true;
    }

    private void moveToLoginActivity() {

        startActivity(new Intent(SignUp.this, Login.class));

        finish();
    }

    void init() {

//        model = new ViewModelProvider(this).get(AuthViewModel.class);
//
//        model.getAuthLiveData().observe(SignUp.this, this::handleAuth);
    
    }

    private void handleAuth(GenericModelLiveData genericModelLiveData) {

        switch (genericModelLiveData.status) {

            case error:

                hideLoader();

                showErrorAlert(genericModelLiveData.errorMsg);

                break;

            case loading:

                showLoader();

                break;

            case success:

                hideLoader();

                onAuthSucceeded();

                break;
        }
    }

    private void onAuthSucceeded() {

        SelectScreen.start(SignUp.this);

    }

}
