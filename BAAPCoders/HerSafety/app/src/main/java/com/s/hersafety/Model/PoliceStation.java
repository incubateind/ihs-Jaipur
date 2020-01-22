package com.s.hersafety.Model;

public class PoliceStation {
    private String area, distance, phone, incharge;
    private PoliceStationLocation location;

    public PoliceStationLocation getLocation() {
        return location;
    }

    public void setLocation(PoliceStationLocation location) {
        this.location = location;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIncharge() {
        return incharge;
    }

    public void setIncharge(String incharge) {
        this.incharge = incharge;
    }

    public PoliceStation() {
    }

    public PoliceStation(String area, String distance, String phone, String incharge, PoliceStationLocation location) {
        this.area = area;
        this.distance = distance;
        this.phone = phone;
        this.incharge = incharge;
        this.location = location;
    }
}
