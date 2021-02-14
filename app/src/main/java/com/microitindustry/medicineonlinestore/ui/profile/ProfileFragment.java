package com.microitindustry.medicineonlinestore.ui.profile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.microitindustry.medicineonlinestore.AdminCategory;
import com.microitindustry.medicineonlinestore.Common.Common;
import com.microitindustry.medicineonlinestore.Model.CategoryModel;
import com.microitindustry.medicineonlinestore.Model.RegisterUserModel;
import com.microitindustry.medicineonlinestore.R;
import com.microitindustry.medicineonlinestore.SignUp;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    TextInputEditText edtName,edtEmail,edtPhone,edtAddress;
    TextView tv_name;
    CardView btnSave;
    ImageView selectImage;
    CircleImageView userImage;
    View root;

    private FirebaseAuth mAuth;
    FirebaseDatabase db;
    DatabaseReference dbGetUser;
    String Uid;
    FirebaseStorage storage;
    StorageReference storageReference;
    Uri saveUri;
    String ImageUrl;
    Button btnUpload,btnSelect;
    RelativeLayout layoutRel;

    String uploadUrl = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_profile, container, false);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getCurrentUser().getUid();
        db =  FirebaseDatabase.getInstance();
        dbGetUser = db.getReference("Users").child(Uid);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //find view
        edtName = root.findViewById(R.id.edtName);
        edtEmail = root.findViewById(R.id.edtEmail);
        edtPhone = root.findViewById(R.id.edtPhoneNo);
        edtAddress = root.findViewById(R.id.edtAddress);
        tv_name = root.findViewById(R.id.tv_name);
        userImage = root.findViewById(R.id.userImage);
        layoutRel = root.findViewById(R.id.profileRel);
        btnSave = root.findViewById(R.id.btnSave);
        selectImage = root.findViewById(R.id.selectImage);

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog();
            }
        });

        loadData();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean valueBool = checktheFields();
                if(valueBool)
                {
                    String name = edtName.getText().toString();
                    String email = edtEmail.getText().toString();
                    String phone = edtPhone.getText().toString();
                    String address = edtAddress.getText().toString();
                    RegisterUserModel model = new RegisterUserModel(name,email,phone,address,ImageUrl);
                    dbGetUser.setValue(model);
                    Toast.makeText(getActivity(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();

                }
            }
        });



        return root;
    }

    private void showImageDialog() {

        AlertDialog.Builder alertDialog  =  new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Update Profile Photo");
        alertDialog.setMessage("Please select and upload the photo");

        LayoutInflater inflater = this.getLayoutInflater();
        View update_layout = inflater.inflate(R.layout.update_photo_layout,null);

        btnSelect = update_layout.findViewById(R.id.btnSelect);
        btnUpload = update_layout.findViewById(R.id.btnUpload);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();

            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        alertDialog.setView(update_layout);
        alertDialog.setIcon(R.drawable.ic_identity_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                    if(uploadUrl == null)
                    {
                        Toast.makeText(getActivity(), "Please Select and Upload Photo", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        dbGetUser.child("image").setValue(uploadUrl);
                        Snackbar.make(layoutRel,"Photo is updated",Snackbar.LENGTH_SHORT)
                                .show();
                    }


            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void uploadImage() {
        if(saveUri !=null)
        {
            final ProgressDialog mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("users/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(getActivity(), "Uploaded !!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    uploadUrl = uri.toString();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded " + progress + "%");
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null
        )
        {
            saveUri = data.getData();
            btnSelect.setText("Image Selected !");
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    private Boolean checktheFields() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        if(name.isEmpty())
        {
            edtName.setError("Please Enter Name!");
            edtName.requestFocus();
            return false;
        }
        if(phone.isEmpty())
        {
            edtPhone.setError("Please Enter Phone!");
            edtPhone.requestFocus();
            return false;
        }
        if(address.isEmpty())
        {
            edtAddress.setError("Please Enter Address!");
            edtAddress.requestFocus();
            return false;
        }
        return true;
    }

    private void loadData() {
        dbGetUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RegisterUserModel model = snapshot.getValue(RegisterUserModel.class);
                edtName.setText(model.getName());
                tv_name.setText(model.getName());
                edtEmail.setText(model.getEmail());
                edtPhone.setText(model.getPhone());
                edtAddress.setText(model.getAddress());
                ImageUrl = model.getImage();
                Picasso.with(getActivity()).load(model.getImage()).into(userImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}