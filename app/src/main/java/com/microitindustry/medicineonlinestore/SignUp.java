package com.microitindustry.medicineonlinestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.microitindustry.medicineonlinestore.Model.RegisterUserModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseDatabase db;
    DatabaseReference dbRef;
    TextInputEditText edtName,edtEmail,edtPass,edtPhone,edtAddress;
    CardView btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        //Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("Users");
        //find view
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        edtPhone = findViewById(R.id.edtPhoneNo);
        edtAddress = findViewById(R.id.edtAddress);
        btnRegister = findViewById(R.id.registerBtnCard);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean valueBool = checktheFields();
                if(valueBool)
                {
                    String email = edtEmail.getText().toString();
                    String password = edtPass.getText().toString();
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                String uid = mAuth.getCurrentUser().getUid();
                                addData(uid);
                                sendVerificationEmail();
                                Intent intent = new Intent(SignUp.this,MainActivity.class);
                                startActivity(intent);
                                finish();

                            }
                            else{
                                Toast.makeText(SignUp.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showAlertDialog();
                            checkIfEmailVerified();
                        }
                        else
                        {
                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }
    private void checkIfEmailVerified()
    {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified())
        {
            // user is verified, so you can finish this activity or send user to activity which you want.
            finish();
            Toast.makeText(SignUp.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
        }
        else
        {

            FirebaseAuth.getInstance().signOut();


        }
    }
    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUp.this);
        alertDialog.setTitle("Verification");
        alertDialog.setMessage("Please Verify Your Email");

        final TextView edtCode = new TextView(SignUp.this);
        edtCode.setText("Goto to your email account and verify the email!");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        edtCode.setLayoutParams(lp);
        alertDialog.setView(edtCode);
        alertDialog.setIcon(R.drawable.ic_baseline_email_24);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void addData(String uid) {
        String name = edtName.getText().toString();
        String email = edtEmail.getText().toString();
        String phone = edtPhone.getText().toString();
        String address = edtAddress.getText().toString();
        RegisterUserModel model = new RegisterUserModel(name,email,phone,address ,"https://firebasestorage.googleapis.com/v0/b/pharmax-1a6f8.appspot.com/o/images%2Fuser.png?alt=media&token=8e17cf07-9e7b-4d8d-bb7f-e2398907c80a");
        dbRef.child(uid).setValue(model);
        Toast.makeText(SignUp.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
    }

    private Boolean checktheFields() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        if(name.isEmpty())
        {
            edtName.setError("Please Enter Name!");
            edtName.requestFocus();
            return false;
        }
        if(email.isEmpty())
        {
            edtEmail.setError("Please Enter Email!");
            edtEmail.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches())
        {
            edtEmail.setError("Please Enter Valid Email!");
            edtEmail.requestFocus();
            return false;
        }
        if(pass.isEmpty())
        {
            edtPass.setError("Please Enter Password!");
            edtPass.requestFocus();
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
}