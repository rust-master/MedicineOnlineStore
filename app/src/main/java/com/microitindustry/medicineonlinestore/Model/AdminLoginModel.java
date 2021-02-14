package com.microitindustry.medicineonlinestore.Model;

public class AdminLoginModel {
    String Name,AdminEmail,AdminPass;

    public AdminLoginModel() {
    }

    public AdminLoginModel(String name, String adminEmail, String adminPass) {
        Name = name;
        AdminEmail = adminEmail;
        AdminPass = adminPass;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAdminEmail() {
        return AdminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        AdminEmail = adminEmail;
    }

    public String getAdminPass() {
        return AdminPass;
    }

    public void setAdminPass(String adminPass) {
        AdminPass = adminPass;
    }
}
