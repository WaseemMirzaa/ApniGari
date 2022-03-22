package com.buzzware.apnigari.activities.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.apnigari.R;
import com.buzzware.apnigari.activities.auth.vm.mo.User;
import com.buzzware.apnigari.activities.base.BaseActivity;
import com.buzzware.apnigari.databinding.ActivityProfileBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileActivity extends BaseActivity {

    ActivityProfileBinding binding;

    Uri imageUri = null;

    final int ACCESS_Gallery = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setView();

        setListener();

        setUserData();

    }

    private void setListener() {

        binding.backIV.setOnClickListener(v -> {
            finish();
        });

        binding.confirmBTN.setOnClickListener(v -> {
            updateUserDataToFirestore();
        });

        binding.editIV.setOnClickListener(v -> {
            checkPermissions();
        });

    }

    private void setView() {
        binding.appBarTitle.setText("Edit profile");

    }

    private void setUserData() {

        DocumentReference users = FirebaseFirestore.getInstance().collection("Users").document(getUserId());

        users.addSnapshotListener((value, error) -> {

            if (value != null) {

                User user = value.toObject(User.class);

                if (user.image != null) {

                    Glide.with(ProfileActivity.this).load(user.image).apply(new RequestOptions().centerCrop()).into(binding.userIV);

                }

                binding.nameTV.setText(user.firstName + " " + user.lastName);

                binding.phoneTV.setText(String.valueOf(user.phoneNumber));

                binding.emailTV.setText(String.valueOf(user.email));

                binding.passwordTV.setText(String.valueOf(user.password));

            }


        });

    }

    private void checkPermissions() {

        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};


        Permissions.check(this/*context*/, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {

                showImagePickerDialog();

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {

                showPermissionsDeniedError(getString(R.string.camera_permissions_denied_string));

            }
        });
    }

    public void showImagePickerDialog() {

        // setup the alert builder

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose");

        // add a list

        String[] animals = {"Camera", "Gallery"};

        builder.setItems(animals, (dialog, which) -> {
            switch (which) {

                case 0:

                    dispatchTakePictureIntent();
                    break;

                case 1:

                    openGallery();
                    break;

            }
        });

        // create and show the alert dialog

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        dispatchTakePictureLauncher.launch(takePictureIntent);

    }

    private void openGallery() {

        Intent intent = new Intent();

        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), ACCESS_Gallery);

    }

    ActivityResultLauncher<Intent> dispatchTakePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    Bitmap photo = null;

                    if (result.getData() != null) {

                        photo = (Bitmap) result.getData().getExtras().get("data");

                        imageUri = getImageUri(ProfileActivity.this, photo);

                        binding.userIV.setImageBitmap(photo);

                        UploadImage();

                    }

                }
            });

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACCESS_Gallery && resultCode == Activity.RESULT_OK) {

            imageUri = data.getData();

            binding.userIV.setImageURI(imageUri);

            UploadImage();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, new Date().toString(), null);

        return Uri.parse(path);
    }


    private void UploadImage() {

        showLoader();

        String randomKey = UUID.randomUUID().toString();

        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("userThumbnail/" + randomKey);

        reference.
                putFile(imageUri).addOnSuccessListener(taskSnapshot -> {

            reference.getDownloadUrl().addOnSuccessListener(uri1 -> {

                hideLoader();

                FirebaseFirestore.getInstance().collection("Users").document(getUserId()).update("image", uri1.toString());

                imageUri = null;

            });
        }).addOnFailureListener(e -> {

            hideLoader();

            showErrorAlert(e.getLocalizedMessage());

        });
    }


    private void updateUserDataToFirestore() {

        Map<String, Object> userData = new HashMap<>();

        if (binding.nameTV.getText().toString().contains(" ")) {

            String[] nameData = binding.nameTV.getText().toString().split(" ");
            userData.put("firstName", nameData[0]);
            userData.put("lastName", nameData[1]);

        } else {
            userData.put("firstName", binding.nameTV.getText().toString());
        }

        userData.put("phoneNumber", binding.phoneTV.getText().toString());

        FirebaseFirestore.getInstance().collection("Users")
                .document(getUserId())
                .update(userData)
                .addOnCompleteListener(task -> {

                    Toast.makeText(ProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });


    }




}