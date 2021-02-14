package com.microitindustry.medicineonlinestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.microitindustry.medicineonlinestore.Model.AdminLoginModel;

public class AdminLogin extends AppCompatActivity {

    CheckBox remember;
    CardView btnAdminLogin;
    TextInputEditText edtEmail;
    TextInputEditText edtPass;
    FirebaseDatabase db;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
        String checkbox = preferences.getString("remember","");
        if(checkbox.equals("true"))
        {
            Intent intent = new Intent(AdminLogin.this,AdminHome.class);
            startActivity(intent);
            finish();
        }
        else if(checkbox.equals("false"))
        {
            Toast.makeText(this, "Please Sign In", Toast.LENGTH_SHORT).show();
        }

        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("Admin");

        remember = findViewById(R.id.remember);
        btnAdminLogin = findViewById(R.id.loginAdminCard);
        edtEmail =  findViewById(R.id.edtAdminEmail);
        edtPass = findViewById(R.id.edtAdminPass);
        btnAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean valueBool = checktheFields();
                if(valueBool)
                {
                    String email = edtEmail.getText().toString();
                    String pass = edtPass.getText().toString();
                    checkUserNameAndPassword(email,pass);
                }
            }
        });
        
    }

    private void checkUserNameAndPassword(String email, String pass) {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AdminLoginModel model = snapshot.getValue(AdminLoginModel.class);
                if(model.getAdminPass().equals(pass) && model.getAdminEmail().equals(email))
                {
                    Toast.makeText(AdminLogin.this, "Login Success", Toast.LENGTH_SHORT).show();
                    if(remember.isChecked())
                    {
                        SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("remember","true");
                        editor.apply();
                        Toast.makeText(AdminLogin.this, "Remember", Toast.LENGTH_SHORT).show();

                    }else if(!remember.isChecked()){
                        SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("remember","false");
                        editor.apply();
                        Toast.makeText(AdminLogin.this, "Not Remember", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(AdminLogin.this,AdminHome.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(AdminLogin.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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