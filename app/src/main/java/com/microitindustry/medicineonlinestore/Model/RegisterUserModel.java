package com.microitindustry.medicineonlinestore.Model;

public class RegisterUserModel {
    String Name,Email,Phone,Address,Image;

    public RegisterUserModel() {
    }


    public RegisterUserModel(String name, String email, String phone, String address, String image) {
        Name = name;
        Email = email;
        Phone = phone;
        Address = address;
        Image = image;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
