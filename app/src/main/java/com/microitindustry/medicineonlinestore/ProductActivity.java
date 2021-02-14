package com.microitindustry.medicineonlinestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.microitindustry.medicineonlinestore.Database.Database;
import com.microitindustry.medicineonlinestore.Model.ItemsListModel;
import com.microitindustry.medicineonlinestore.Model.Order;
import com.squareup.picasso.Picasso;

public class ProductActivity extends AppCompatActivity {

    TextView product_name,product_price,product_description;
    ImageView product_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;

    String productId="";
    FirebaseDatabase database;
    DatabaseReference product;

    ItemsListModel currentProduct;

    ImageButton qtyAdd,qtyMinus;
    EditText edtQty;
    CardView cardPlaceOrder;
    int i = 1;

    CounterFab fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // firebase
        database = FirebaseDatabase.getInstance();
        product = database.getReference("Items");

        //Init View
        btnCart = findViewById(R.id.btnCart);

        product_name = findViewById(R.id.product_name);
        product_description = findViewById(R.id.product_description);
        product_price = findViewById(R.id.product_price);
        product_image = findViewById(R.id.img_product);
        cardPlaceOrder = findViewById(R.id.cardPlaceOrder);


        collapsingToolbarLayout =findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        if(getIntent() !=null)
            productId =getIntent().getStringExtra("ItemId");
        if(!productId.isEmpty()){
            getDetailFood(productId);
        }

        qtyAdd = findViewById(R.id.qtyAdd);
        qtyMinus = findViewById(R.id.qtyMinus);
        edtQty = findViewById(R.id.qty);




        qtyAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                edtQty.setText(String.valueOf(i));
            }
        });

        qtyMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i--;
                if(i==0)
                {
                    i++;
                }
                edtQty.setText(String.valueOf(i));

            }
        });

        fab = findViewById(R.id.btnCart);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int check = Integer.parseInt(edtQty.getText().toString());
                if(check < 1 || check > 1000)
                {
                    Toast.makeText(ProductActivity.this, "Please enter quantity above than zero", Toast.LENGTH_SHORT).show();
                    edtQty.setText("1");
                }
                else{
                    boolean isExists = new Database(getBaseContext()).checkProductExists(productId);
                    if(!isExists)
                    {
                        new Database(getBaseContext()).addToCart(new Order(
                                productId,
                                currentProduct.getItemName(),
                                edtQty.getText().toString(),
                                currentProduct.getItemPrice(),
                                currentProduct.getImage()
                        ));
                        Toast.makeText(ProductActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                        Intent cartIntent = new Intent(ProductActivity.this,CartActivity.class);
                        startActivity(cartIntent);
                    }
                    else{

                        Toast.makeText(ProductActivity.this, "ID " + productId + " QTY" + edtQty.getText().toString(), Toast.LENGTH_SHORT).show();
                        System.out.println("ID " + productId + " QTY" + edtQty.getText().toString());
                        new Database(getBaseContext()).IncreaseCart(productId,edtQty.getText().toString());
                        Toast.makeText(ProductActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                        Intent cartIntent = new Intent(ProductActivity.this,CartActivity.class);
                        startActivity(cartIntent);
                    }

                }

            }
        });
        fab.setCount(new Database(this).getCountCart());

        cardPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int check = Integer.parseInt(edtQty.getText().toString());
                if(check < 1 || check > 1000)
                {
                    Toast.makeText(ProductActivity.this, "Please enter quantity above than zero", Toast.LENGTH_SHORT).show();
                    edtQty.setText("1");
                }
                else{
                    new Database(getBaseContext()).addToCart(new Order(
                            productId,
                            currentProduct.getItemName(),
                            edtQty.getText().toString(),
                            currentProduct.getItemPrice(),
                            currentProduct.getImage()
                    ));
                    Toast.makeText(ProductActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                    Intent cartIntent = new Intent(ProductActivity.this,CartActivity.class);
                    startActivity(cartIntent);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart());
    }

    private void getDetailFood(String productId) {
        product.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentProduct = dataSnapshot.getValue(ItemsListModel.class);

                Picasso.with(getBaseContext()).load(currentProduct.getImage())
                        .into(product_image);


                collapsingToolbarLayout.setTitle(currentProduct.getItemName());

                product_price.setText(currentProduct.getItemPrice());
                product_name.setText(currentProduct.getItemName());
                product_description.setText(currentProduct.getItemDescription());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}