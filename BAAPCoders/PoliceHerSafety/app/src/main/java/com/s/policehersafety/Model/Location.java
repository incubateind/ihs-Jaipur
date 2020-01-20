package com.s.policehersafety.Model;

public class Location {
    private String userName;
    private String latitude;
    private String longitude;
    private String address;
    private String userPhone;

    public Location(String userName, String latitude, String longitude, String address, String userPhone) {
        this.userName = userName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.userPhone = userPhone;
    }

    public Location() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
