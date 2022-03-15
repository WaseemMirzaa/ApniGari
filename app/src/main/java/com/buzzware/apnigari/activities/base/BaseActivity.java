package com.buzzware.apnigari.activities.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaseActivity extends AppCompatActivity {

    AlertDialog alertDialog, locationPermissionsDialog;

    ProgressDialog progressDialog;

    public void showErrorAlert(String msg) {

        if (BaseActivity.this.isFinishing() || BaseActivity.this.isDestroyed()) {
            finish();
            return;

        }
try {

    if (alertDialog != null && alertDialog.isShowing())

        alertDialog.dismiss();

    alertDialog = new AlertDialog.Builder(BaseActivity.this)
            .setMessage(msg)
            .setTitle("Alert")
            .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
            .create();


    alertDialog.show();

}catch (Exception e) {
    finish();
}


    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) BaseActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(BaseActivity.this);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showPermissionsDeniedError(String msg) {
//
//        if (alertDialog != null && alertDialog.isShowing())
//
//            return;
//
//        if (locationPermissionsDialog != null && locationPermissionsDialog.isShowing())
//
//            return;
//
//        locationPermissionsDialog = new AlertDialog.Builder(BaseActivity.this)
//                .setMessage(msg)
//                .setTitle("Alert")
//                .setCancelable(false)
//                .setPositiveButton("Finish", (dialog, which) -> {
//
//                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                            Uri.parse("package:com.buzzware.iride")));
//
//                    dialog.dismiss();
//
//
//                })
//                .create();
//
//        locationPermissionsDialog.show();
    }

    public void showEnableLocationDialog(String msg) {

//        if (alertDialog != null && alertDialog.isShowing())
//
//            alertDialog.dismiss();
//
//        if (locationPermissionsDialog != null && locationPermissionsDialog.isShowing())
//
//            return;
//
//        locationPermissionsDialog = new AlertDialog.Builder(BaseActivity.this)
//                .setMessage(msg)
//                .setTitle("Alert")
//                .setCancelable(false)
//                .setPositiveButton("Settings", (dialog, which) -> {
//
//                    dialog.dismiss();
//
//                    SimpleLocation.openSettings(BaseActivity.this);
//
//                })
//                .create();
//
//        locationPermissionsDialog.show();
    }

    public void showLoader() {

        if (BaseActivity.this.isFinishing() || BaseActivity.this.isDestroyed()) {
            finish();
            return;

        }
        if (progressDialog != null && progressDialog.isShowing())

            progressDialog.dismiss();

        progressDialog = new ProgressDialog(BaseActivity.this);
        progressDialog.setTitle("");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);

        progressDialog.show();
    }

    public void hideLoader() {

        if (BaseActivity.this.isDestroyed() || BaseActivity.this.isFinishing())

            return;

        if (progressDialog != null && progressDialog.isShowing())

            progressDialog.dismiss();
    }

    public static String getUserId() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            user.getUid();

            return user.getUid();

        }

        return "";
    }

}
