package com.gx.worings.Entry;

public class Location {
    public String phone;
    public String lat;
    public String lng;
    public String lng_lat;
    public String time;

    public Location(String phone, String lat, String lng, String lng_lat, String time) {
        this.phone = phone;
        this.lat = lat;
        this.lng = lng;
        this.lng_lat = lng_lat;
        this.time = time;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLng_lat() {
        return lng_lat;
    }

    public void setLng_lat(String lng_lat) {
        this.lng_lat = lng_lat;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
