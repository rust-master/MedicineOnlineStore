package com.microitindustry.medicineonlinestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class AdminHome extends AppCompatActivity {

    CardView logoutBtnCard,btnProductCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        logoutBtnCard = findViewById(R.id.logoutBtnCard);
        btnProductCard=findViewById(R.id.btnProductCard);
        btnProductCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this,CategoriesActivity.class);
                startActivity(intent);
            }
        });
        logoutBtnCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember","false");
                editor.apply();
                Intent intent = new Intent(AdminHome.this,AdminLogin.class);
                startActivity(intent);
                finish();
            }
        });
    }
}