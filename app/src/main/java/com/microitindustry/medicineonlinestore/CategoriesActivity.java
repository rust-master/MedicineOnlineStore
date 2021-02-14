package com.microitindustry.medicineonlinestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CategoriesActivity extends AppCompatActivity {

    CardView btnGermanCard,btnAlmasoodCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        // Views
        btnGermanCard = findViewById(R.id.btnGermanCard);
        btnAlmasoodCard = findViewById(R.id.btnAlmasoodCard);

        btnGermanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent germanIntent = new Intent(CategoriesActivity.this,AdminCategory.class);
                germanIntent.putExtra("Categories","German");
                startActivity(germanIntent);
            }
        });
        btnAlmasoodCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent germanIntent = new Intent(CategoriesActivity.this,AdminCategory.class);
                germanIntent.putExtra("Categories","Al Masood");
                startActivity(germanIntent);
            }
        });
    }
}