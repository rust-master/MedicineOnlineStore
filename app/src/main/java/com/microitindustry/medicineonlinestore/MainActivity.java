package com.microitindustry.medicineonlinestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    ImageButton hideShowPass;
    Boolean flag = true;
    TextView signUp,adminPanelBtn,forgetPassword;
    TextInputEditText edtEmail,edtPass;
    CardView btnCard;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            checkIfEmailVerified();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
        String checkbox = preferences.getString("remember","");
        if(checkbox.equals("true"))
        {
            Intent intent = new Intent(MainActivity.this,AdminHome.class);
            startActivity(intent);
            finish();
        }



        //Firebase
        mAuth = FirebaseAuth.getInstance();

        adminPanelBtn = findViewById(R.id.adminPanelbtn);
        hideShowPass = findViewById(R.id.hideShowPass);
        edtPass = findViewById(R.id.edtPass);
        hideShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag)
                {
                    edtPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    hideShowPass.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
                    flag = false;
                }
                else{
                    edtPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    hideShowPass.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                    flag = true;
                }

            }
        });

        adminPanelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AdminLogin.class);
                startActivity(intent);
            }
        });

        signUp = findViewById(R.id.signUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(MainActivity.this,SignUp.class);
                startActivity(signUpIntent);
            }
        });

        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        btnCard = findViewById(R.id.loginBtnCard);
        btnCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean valueBool = checktheFields();
                if(valueBool)
                {
                    String email = edtEmail.getText().toString();
                    String password = edtPass.getText().toString();
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                checkIfEmailVerified();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Please Try Again. Login Failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    private void checkIfEmailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified())
        {
            String email = user.getEmail();
            Toast.makeText(this, "Email " + email, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this,Home.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(MainActivity.this, "Goto your email inbox and verify the email", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();

        }
    }
    private Boolean checktheFields() {
        String email = edtEmail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();
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
        return true;
    }
}