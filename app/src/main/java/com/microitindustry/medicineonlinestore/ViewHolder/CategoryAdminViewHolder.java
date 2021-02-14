package com.microitindustry.medicineonlinestore.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.microitindustry.medicineonlinestore.Common.Common;
import com.microitindustry.medicineonlinestore.Interface.ItemClickListener;
import com.microitindustry.medicineonlinestore.R;

public class CategoryAdminViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener {

    public TextView ctName,ctDes,ctPrice;
    public ImageView ctImage;

    private ItemClickListener itemClickListener;

    public CategoryAdminViewHolder(@NonNull View itemView) {
        super(itemView);
        ctName = itemView.findViewById(R.id.itemTitle);
        ctDes = itemView.findViewById(R.id.itemDescribe);
        ctPrice = itemView.findViewById(R.id.itemPrice);
        ctImage = itemView.findViewById(R.id.itemImage);

        itemView.setOnCreateContextMenuListener(this);

        //itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select the action");
        menu.add(0,0,getAdapterPosition(), Common.UPDATE);
        menu.add(0,1,getAdapterPosition(), Common.DELETE);
    }
}
