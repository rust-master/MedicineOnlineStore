package com.microitindustry.medicineonlinestore.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.microitindustry.medicineonlinestore.AdminCategory;
import com.microitindustry.medicineonlinestore.AdminItemsList;
import com.microitindustry.medicineonlinestore.Model.CategoryModel;
import com.microitindustry.medicineonlinestore.R;
import com.microitindustry.medicineonlinestore.UserItemList;
import com.microitindustry.medicineonlinestore.ViewHolder.CategoryViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    View root;
    FirebaseDatabase db;
    DatabaseReference dbRef;
    RecyclerView recyclerViewCategory;
    LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<CategoryModel, CategoryViewHolder> adapter;

    // search functionality
    FirebaseRecyclerAdapter<CategoryModel,CategoryViewHolder> searchAdapter;
    List<String> suggestList =  new ArrayList<>();
    MaterialSearchBar materialSearchBar;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);

        // Firebase
        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("Category");

        recyclerViewCategory = root.findViewById(R.id.recyclerViewCategory);
        layoutManager = new LinearLayoutManager(getActivity());
//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);
        recyclerViewCategory.setLayoutManager(layoutManager);

        loadData();

        //search
        materialSearchBar = root.findViewById(R.id.searchBar);
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
                    recyclerViewCategory.setAdapter(adapter);
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

        return root;
    }

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<CategoryModel, CategoryViewHolder>(
                CategoryModel.class,
                R.layout.main_items,
                CategoryViewHolder.class,
                dbRef.orderByChild("itemName").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(CategoryViewHolder viewHolder, CategoryModel model, int position) {
                viewHolder.ctName.setText(model.getItemName());
                viewHolder.ctDes.setText(model.getItemDescription());
                Picasso.with(getActivity()).load(model.getImage())
                        .into(viewHolder.ctImage);


            }
        };
        recyclerViewCategory.setAdapter(searchAdapter);
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

    private void loadData() {
        adapter = new FirebaseRecyclerAdapter<CategoryModel, CategoryViewHolder>(
                CategoryModel.class,
                R.layout.main_items,
                CategoryViewHolder.class,
                dbRef
        ) {
            @Override
            protected void populateViewHolder(CategoryViewHolder viewHolder, CategoryModel model, int position) {

                viewHolder.ctName.setText(model.getItemName());
                viewHolder.ctDes.setText(model.getItemDescription());
                Picasso.with(getActivity()).load(model.getImage())
                        .into(viewHolder.ctImage);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent foodList =  new Intent(getActivity(), UserItemList.class);
                        foodList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });


            }
        };
        adapter.notifyDataSetChanged();
        recyclerViewCategory.setAdapter(adapter);
    }
}