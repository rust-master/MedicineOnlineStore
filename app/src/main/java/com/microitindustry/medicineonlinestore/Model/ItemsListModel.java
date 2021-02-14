package com.microitindustry.medicineonlinestore.Model;

public class ItemsListModel {
    String Image,ItemName,ItemDescription,ItemPrice,CategoryId;

    public ItemsListModel() {
    }

    public ItemsListModel(String image, String itemName, String itemDescription, String itemPrice, String categoryId) {
        Image = image;
        ItemName = itemName;
        ItemDescription = itemDescription;
        ItemPrice = itemPrice;
        CategoryId = categoryId;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getItemDescription() {
        return ItemDescription;
    }

    public void setItemDescription(String itemDescription) {
        ItemDescription = itemDescription;
    }

    public String getItemPrice() {
        return ItemPrice;
    }

    public void setItemPrice(String itemPrice) {
        ItemPrice = itemPrice;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }
}
