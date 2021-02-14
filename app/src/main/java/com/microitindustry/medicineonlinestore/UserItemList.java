package com.microitindustry.medicineonlinestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.microitindustry.medicineonlinestore.Model.CategoryModel;
import com.microitindustry.medicineonlinestore.Model.ItemsListModel;
import com.microitindustry.medicineonlinestore.ViewHolder.CategoryAdminViewHolder;
import com.microitindustry.medicineonlinestore.ViewHolder.CategoryViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserItemList extends AppCompatActivity {

    FirebaseDatabase db;
    DatabaseReference dbRef;
    RecyclerView recyclerViewItem;
    LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<ItemsListModel, CategoryViewHolder> adapter;

    String categoryId="";
    // search functionality
    FirebaseRecyclerAdapter<ItemsListModel, CategoryViewHolder> searchAdapter;
    List<String> suggestList =  new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_item_list);

        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("Items");

        recyclerViewItem = findViewById(R.id.recyclerUserItems);
        layoutManager = new LinearLayoutManager(this);
//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);
        recyclerViewItem.setLayoutManager(layoutManager);

        if(getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty())
            loadData(categoryId);

        //search
        materialSearchBar = findViewById(R.id.searchBar);
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> suggest = new ArrayList<String>();
                for(String search:suggestList)
                {
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> suggest = new ArrayList<String>();
                for(String search:suggestList)
                {
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

                if(!enabled){
                    recyclerViewItem.setAdapter(adapter);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<ItemsListModel, CategoryViewHolder>(
                ItemsListModel.class,
                R.layout.product_items,
                CategoryViewHolder.class,
                dbRef.orderByChild("itemName").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(CategoryViewHolder viewHolder, ItemsListModel model, int position) {
                viewHolder.ctName.setText(model.getItemName());
                viewHolder.ctDes.setText(model.getItemDescription());
                viewHolder.ctPrice.setText(model.getItemPrice());
                Picasso.with(getApplicationContext()).load(model.getImage())
                        .into(viewHolder.ctImage);


            }
        };
        recyclerViewItem.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        dbRef.orderByChild("itemName")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            CategoryModel item = postSnapshot.getValue(CategoryModel.class);
                            suggestList.add(item.getItemName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void loadData(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<ItemsListModel, CategoryViewHolder>(
                ItemsListModel.class,
                R.layout.product_items,
                CategoryViewHolder.class,
                dbRef.orderByChild("categoryId").equalTo(categoryId)
        ) {
            @Override
            protected void populateViewHolder(CategoryViewHolder viewHolder, ItemsListModel model, int position) {

                viewHolder.ctName.setText(model.getItemName());
                viewHolder.ctDes.setText(model.getItemDescription());
                viewHolder.ctPrice.setText(model.getItemPrice());
                Picasso.with(getApplicationContext()).load(model.getImage())
                        .into(viewHolder.ctImage);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent foodList =  new Intent(UserItemList.this, ProductActivity.class);
                        foodList.putExtra("ItemId",adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
        recyclerViewItem.setAdapter(adapter);
    }
}