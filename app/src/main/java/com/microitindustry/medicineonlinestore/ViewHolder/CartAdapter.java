package com.microitindustry.medicineonlinestore.ViewHolder;




import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;



import com.microitindustry.medicineonlinestore.CartActivity;
import com.microitindustry.medicineonlinestore.Database.Database;
import com.microitindustry.medicineonlinestore.Model.Order;
import com.microitindustry.medicineonlinestore.R;
import com.squareup.picasso.Picasso;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

    private List<Order> listData = new ArrayList<>();
    private CartActivity cartActivity;
    int i = 1;

    public CartAdapter(List<Order> listData, CartActivity cartActivity) {
        this.listData = listData;
        this.cartActivity = cartActivity;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(cartActivity);
        View itemView = inflater.inflate(R.layout.cart_layout,parent,false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {

        Picasso.with(cartActivity.getBaseContext())
                .load(listData.get(position).getImage())
                .resize(70,70)
                .centerCrop()
                .into(holder.cart_Image);

        holder.qtyAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = Integer.parseInt(holder.img_cart_count.getText().toString());
                i++;
                holder.img_cart_count.setText(String.valueOf(i));

                Order order = listData.get(position);
                order.setQuantity(String.valueOf(holder.img_cart_count.getText().toString()));
                new Database(cartActivity).updateCart(order);

                int total = 0;
                List<Order> orders = new Database(cartActivity).getCarts();
                for(Order item : orders)
                    total+=(Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
                Locale locale = new Locale("en" ,"US");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                cartActivity.txtTotalPrice.setText(fmt.format(total));

                int price = (Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));
                holder.txt_price.setText(fmt.format(price));

            }
        });

        holder.qtyMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = Integer.parseInt(holder.img_cart_count.getText().toString());
                i--;
                if(i==0)
                {
                    i++;
                }
                holder.img_cart_count.setText(String.valueOf(i));
                Order order = listData.get(position);
                order.setQuantity(String.valueOf(holder.img_cart_count.getText().toString()));
                new Database(cartActivity).updateCart(order);

                int total = 0;
                List<Order> orders = new Database(cartActivity).getCarts();
                for(Order item : orders)
                    total+=(Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
                Locale locale = new Locale("en" ,"US");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                cartActivity.txtTotalPrice.setText(fmt.format(total));

                int price = (Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));
                holder.txt_price.setText(fmt.format(price));

            }
        });

        holder.img_cart_count.setText(listData.get(position).getQuantity());

        Locale locale = new Locale("en" ,"US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));
        holder.txt_price.setText(fmt.format(price));
        holder.txt_cart_name.setText(listData.get(position).getProductName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public Order getItem(int position)
    {
        return listData.get(position);
    }

    public void removeItem(int position)
    {
        listData.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreItem(Order item , int position)
    {
        listData.add(position,item);
        notifyItemInserted(position);
    }
}
