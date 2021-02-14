package com.microitindustry.medicineonlinestore.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.microitindustry.medicineonlinestore.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder {

    public TextView ctName,ctDes,ctPrice;
    public ImageView ctImage;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        ctName = itemView.findViewById(R.id.itemTitle);
        ctDes = itemView.findViewById(R.id.itemDescribe);
        ctPrice = itemView.findViewById(R.id.itemPrice);
        ctImage = itemView.findViewById(R.id.itemImage);
    }
}
