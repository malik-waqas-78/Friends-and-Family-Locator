package com.example.home_pc.tracker;

public class AdapterItems {
    public String PhoneNumber,UserNmae;

    public AdapterItems(String userNmae,String phoneNumber) {
        PhoneNumber = phoneNumber;
        UserNmae = userNmae;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getUserNmae() {
        return UserNmae;
    }

    public void setUserNmae(String userNmae) {
        UserNmae = userNmae;
    }
}
