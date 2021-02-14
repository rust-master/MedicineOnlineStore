package com.microitindustry.medicineonlinestore.Common;


import com.microitindustry.medicineonlinestore.Remote.IGoogleService;
import com.microitindustry.medicineonlinestore.Remote.RetrofitClient;

public class Common {


    public static final String UPDATE= "Update";
    public static final String DELETE= "Delete";
    public static final int PICK_IMAGE_REQUEST =71;

    public static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static IGoogleService getGoogleMapAPI()
    {
        return RetrofitClient.getGoogleClient(GOOGLE_API_URL).create(IGoogleService.class);
    }

    public static String convertCodeToStatus(String code)
    {
        if(code.equals("0"))
        {
            return "Placed";
        }
        else
        {
            return "Shipped";
        }

    }
}
