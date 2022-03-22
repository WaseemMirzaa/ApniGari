package com.buzzware.apnigari.activities.auth.vm;

import static com.buzzware.apnigari.activities.base.BaseActivity.getUserId;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.buzzware.apnigari.Firebase.FirebaseInstances;
import com.buzzware.apnigari.activities.auth.vm.mo.User;
import com.buzzware.apnigari.generic.GenericModelLiveData;
import com.google.firebase.auth.FirebaseAuth;

public class AuthViewModel extends ViewModel {

    private MutableLiveData<GenericModelLiveData> data, forgetPasswordLiveData;

    public LiveData<GenericModelLiveData> getAuthLiveData() {

        if (data == null) {

            data = new MutableLiveData<>();

        }

        return data;
    }

    public LiveData<GenericModelLiveData> getForgetPasswordLiveData() {

        if (forgetPasswordLiveData == null) {

            forgetPasswordLiveData = new MutableLiveData<>();

        }

        return forgetPasswordLiveData;
    }

    public void authenticateWithEmailAndPassword(String email, String password, Activity c) {

        data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.loading, null));

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.trim(), password)

                .addOnCompleteListener(c, task -> {

                    if (task.isSuccessful()) {

                        getUserModel();

                    } else {

                        if (task.getException() != null) {

                            data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, task.getException().getLocalizedMessage()));

                        } else {

                            data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, "Unable to Login Please try again"));

                        }


                    }

                });
    }

    private void getUserModel() {

        if (FirebaseAuth.getInstance().getCurrentUser() == null)

            return;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseInstances.usersCollection.document(userId)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        User user = task.getResult().toObject(User.class);

                        if (user != null) {

                            data.postValue(new GenericModelLiveData(user, GenericModelLiveData.Status.success, null));

                        } else {

                            data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, "Unable to find user data. This user has been deleted by admin."));

                        }

                    } else {

                        if (task.getException() != null)

                            data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, task.getException().getLocalizedMessage()));

                    }

                });

    }

    public void sendForgetPasswordEmail(String input) {

        forgetPasswordLiveData.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.loading, null));

        FirebaseAuth.getInstance()
                .sendPasswordResetEmail(input)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        forgetPasswordLiveData.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.success, null));

                    } else {

                        if (task.getException() != null)

                            forgetPasswordLiveData.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, task.getException().getLocalizedMessage()));

                    }
                });
    }

    public void createUserWithEmailAndPassword(User user, Activity c) {

        data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.loading, null));

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.email.trim(), user.password.trim())

                .addOnCompleteListener(c, task -> {

                    if (task.isSuccessful()) {

                        setUser(user);

                    } else {

                        if (task.getException() != null) {

                            data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, task.getException().getLocalizedMessage()));

                        } else {

                            data.postValue(new GenericModelLiveData(null, GenericModelLiveData.Status.error, "Unable to Login Please try again"));

                        }


                    }

                });
    }

    private void setUser(User user) {

        FirebaseInstances.usersCollection.document(getUserId())
                .set(user);

        data.postValue(new GenericModelLiveData(user, GenericModelLiveData.Status.success, null));

    }


}
