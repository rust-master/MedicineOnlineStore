package com.microitindustry.medicineonlinestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.microitindustry.medicineonlinestore.Common.Common;
import com.microitindustry.medicineonlinestore.Database.Database;
import com.microitindustry.medicineonlinestore.Helper.RecyclerItemTouchHelper;
import com.microitindustry.medicineonlinestore.Interface.RecyclerItemTouchHelperListener;
import com.microitindustry.medicineonlinestore.Model.Order;
import com.microitindustry.medicineonlinestore.Model.RegisterUserModel;
import com.microitindustry.medicineonlinestore.Model.Request;
import com.microitindustry.medicineonlinestore.Remote.GPSTracker;
import com.microitindustry.medicineonlinestore.Remote.IGoogleService;
import com.microitindustry.medicineonlinestore.ViewHolder.CartAdapter;
import com.microitindustry.medicineonlinestore.ViewHolder.CartViewHolder;


import java.text.NumberFormat;

import java.util.ArrayList;

import java.util.List;
import java.util.Locale;



public class CartActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests,dbGetUser;
    private FirebaseAuth mAuth;

    public TextView txtTotalPrice;
    Button btnPlace;
    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;


    EditText edtShippingAddress;

    IGoogleService mGoogleMapService;
    int MY_PERMISSION_ACCESS_COURSE_LOCATION =1;
    GPSTracker gpsTracker;
    String Uid;
    String Name = null;
    String PhoneNo = null;
    String Address = null;

    RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Init
        mGoogleMapService = Common.getGoogleMapAPI();
        txtTotalPrice = findViewById(R.id.total);

        checkLocationPermission();

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getCurrentUser().getUid();
        database =  FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");
        dbGetUser = database.getReference("Users").child(Uid);

        //Init
        recyclerView = (RecyclerView) findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout = findViewById(R.id.rootLayout);

        // Swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        btnPlace = (Button) findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create new request
                if(cart.size() > 0)
                {
                    showAlertDialog();
                }
                else{
                    Toast.makeText(CartActivity.this, "Your Cart is Empty !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadListFood();

        getUserData();

    }

    private void checkLocationPermission() {
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    MY_PERMISSION_ACCESS_COURSE_LOCATION );
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Location On", Toast.LENGTH_SHORT).show();
        }
    }

    private void getUserData() {

        dbGetUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RegisterUserModel model = snapshot.getValue(RegisterUserModel.class);
                Name = model.getName();
                PhoneNo = model.getPhone();
                Address = model.getAddress();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter your address");

        String COD = "Cash On Delivery";

        LayoutInflater inflater = this.getLayoutInflater();
        View add_address_layout = inflater.inflate(R.layout.address_layout,null);

        edtShippingAddress = add_address_layout.findViewById(R.id.edtShippingAddress);
        RadioButton rdShipToAddress = add_address_layout.findViewById(R.id.rdShipToAddress);
        RadioButton rdHomeAddress = add_address_layout.findViewById(R.id.rdHomeAddress);
        RadioButton rdCOD = add_address_layout.findViewById(R.id.rdCashOnDelivery);


        rdShipToAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    gpsTracker = new GPSTracker(getApplicationContext());
                    if (gpsTracker.getIsGPSTrackingEnabled())
                    {

                        String stringLatitude = String.valueOf(gpsTracker.latitude);
                        String stringLongitude = String.valueOf(gpsTracker.longitude);
                        String addressLine = gpsTracker.getAddressLine(getApplicationContext());
                        edtShippingAddress.setText(addressLine);
//                        mGoogleMapService.getAddressName()

                    }
                    else
                    {

                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        showSettingsAlert();

                    }


                }
            }
        });

        rdHomeAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked)
               {
                   edtShippingAddress.setText(Address);
               }
            }
        });


        alertDialog.setView(add_address_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_location_on_24);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Request request = new Request(
                        PhoneNo,
                        Name,
                        edtShippingAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        "0",
                        COD,
                        cart
                );

                if(edtShippingAddress.getText().toString().equals(""))
                {
                    Toast.makeText(CartActivity.this, "Please enter shipping address", Toast.LENGTH_SHORT).show();

                }
                else if(!rdCOD.isChecked())
                {
                    Toast.makeText(CartActivity.this, "Please select payment method", Toast.LENGTH_SHORT).show();
                }
                else {

                        requests.child(String.valueOf(System.currentTimeMillis()))
                                .setValue(request);
                        // delete cart
                        new Database(getBaseContext()).cleanCart();
                        Toast.makeText(CartActivity.this, "Thank you , Order Place", Toast.LENGTH_SHORT).show();
                        finish();


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



    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);

        //Setting Dialog Title
        alertDialog.setTitle(R.string.GPSAlertDialogTitle);

        //Setting Dialog Message
        alertDialog.setMessage(R.string.GPSAlertDialogMessage);

        //On Pressing Setting button
        alertDialog.setPositiveButton(R.string.action_settings, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        //On pressing cancel button
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void loadListFood() {
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);


        int total = 0;
        for(Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        Locale locale = new Locale("en" ,"US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(fmt.format(total));
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof CartViewHolder)
        {
            String name = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();
            Order deleteItem = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            int deleteIndex = viewHolder.getAdapterPosition();
            adapter.removeItem(deleteIndex);

            new Database(getBaseContext()).removeFromCart(deleteItem.getProductId());

            // Update
            int total = 0;
            List<Order> orders = new Database(getBaseContext()).getCarts();
            for(Order item : orders)
                total+=(Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
            Locale locale = new Locale("en" ,"US");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
            txtTotalPrice.setText(fmt.format(total));

            // Snackbar
            Snackbar snackbar = Snackbar.make(rootLayout,name + " removed from cart!",Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 adapter.restoreItem(deleteItem,deleteIndex);
                 new Database(getBaseContext()).addToCart(deleteItem);

                    // Update
                    int total = 0;
                    List<Order> orders = new Database(getBaseContext()).getCarts();
                    for(Order item : orders)
                        total+=(Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
                    Locale locale = new Locale("en" ,"US");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                    txtTotalPrice.setText(fmt.format(total));


                }
            });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE))
        {
           deleteItem(item.getOrder());
        }

        return super.onContextItemSelected(item);
    }

    private void deleteItem(int position) {
        cart.remove(position);
        new Database(this).cleanCart();
        for(Order item:cart)
        {
            new Database(this).addToCart(item);
        }
        loadListFood();
    }
}