package com.microitindustry.medicineonlinestore.Database;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;


import com.microitindustry.medicineonlinestore.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    private static final String DB_Name="CartDB.db";
    private static final int DB_VER=2;

    public Database(Context context) {
        super(context, DB_Name,null, DB_VER);
    }
    public List<Order> getCarts()
    {

        SQLiteDatabase db =getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect={"ID","ProductId","ProductName","Quantity","Price","Image"};
        String sqlTable="OrderDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db,sqlSelect,null,null,null,null,null);
        final List<Order> result = new ArrayList<>();
        if(c.moveToFirst()){
            do{
                result.add(new Order(
                        c.getInt(c.getColumnIndex("ID")),
                        c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Image"))

                        ));
            }while (c.moveToNext());
        }
        return  result;

    }

    public void addToCart(Order order){
        SQLiteDatabase db = getReadableDatabase();
        String query  = String.format("INSERT INTO OrderDetail(ProductId,ProductName,Quantity,Price,Image) VALUES('%s','%s','%s','%s','%s');",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getImage()
                );
        db.execSQL(query);
    }

    public void cleanCart(){
        SQLiteDatabase db = getReadableDatabase();
        String query  = String.format("DELETE FROM OrderDetail");
        db.execSQL(query);
    }

    public int getCountCart() {
        int count=0;

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetail");
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            do{
                count = cursor.getInt(0);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return count;
    }

    public void updateCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity = %s WHERE ID = %d",order.getQuantity(),order.getID());
        db.execSQL(query);
    }

    public void IncreaseCart(String productId,String QTY) {

        int mainQTY = getQTY(productId ,QTY ) ;
        String strQRY = String.valueOf(mainQTY);
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity = %s WHERE ProductId ='%s'",strQRY,productId);
        db.execSQL(query);
    }

    public void removeFromCart(String productId) {
        SQLiteDatabase db = getReadableDatabase();
        String query  = String.format("DELETE FROM OrderDetail WHERE ProductId ='%s' ",productId);
        db.execSQL(query);
    }

    private int getQTY(String productId , String enterQty) {
        int intqty = Integer.parseInt(enterQty);
        String qty = "0";
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT Quantity FROM OrderDetail  WHERE ProductId ='%s'",productId);
        Cursor  cursor = db.rawQuery(query,null);
        if (cursor.moveToFirst()) {
            do
            {
                qty =  cursor.getString(cursor.getColumnIndex("Quantity"));


            }while (cursor.moveToNext());
        }
        cursor.close();
        int getqty = Integer.parseInt(qty);
        int mainQty = intqty + getqty;
        return mainQty;
    }

    public boolean checkProductExists(String productId)
    {
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String query = String.format("SELECT * FROM OrderDetail WHERE ProductId='%s'",productId);
        cursor = db.rawQuery(query,null);
        if(cursor.getCount() > 0){
            flag = true;
        }
        else{
            flag = false;
        }
        cursor.close();
        return flag;
    }


}
