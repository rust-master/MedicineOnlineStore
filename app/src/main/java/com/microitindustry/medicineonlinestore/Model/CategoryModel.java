package com.microitindustry.medicineonlinestore.Model;

public class CategoryModel {

    String Image,ItemName,ItemDescription,Categories;

    public CategoryModel() {
    }


    public CategoryModel(String image, String itemName, String itemDescription, String categories) {
        Image = image;
        ItemName = itemName;
        ItemDescription = itemDescription;
        Categories = categories;
    }

    public String getCategories() {
        return Categories;
    }

    public void setCategories(String categories) {
        Categories = categories;
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

}
