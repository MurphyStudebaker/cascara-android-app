package com.example.murphy.cascara;

import java.util.ArrayList;
/** Simple Java Object for a Coffee Shop */
public class CoffeeShop {
    private int houseID;
    private String houseName;
    private int imgID;
    private double coffeeScore;
    private double wifiScore;
    private String website;
    private String phone;
    private String address;
    private String city;
    private String state;
    private ArrayList<String> atmosphere;
    private ArrayList<String> amenities;
    private ArrayList<String> menuItems;
    private String bestOrder;

    public CoffeeShop() {
        houseID = 0;
        houseName = "";
        coffeeScore = 0;
        wifiScore = 0;
        atmosphere = new ArrayList<>();
        amenities = new ArrayList<>();
        bestOrder = "";
    }

    public CoffeeShop(int houseID, String houseName, double coffeeScore, double wifiScore, int imgID) {
        this.houseID = houseID;
        this.houseName = houseName;
        this.coffeeScore = coffeeScore;
        this.wifiScore = wifiScore;
        this.imgID = imgID;
        atmosphere = new ArrayList<>();
        amenities = new ArrayList<>();
        menuItems = new ArrayList<>();
    }

    public int getHouseID() {
        return houseID;
    }

    public void setHouseID(int houseID) {
        this.houseID = houseID;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public double getCoffeeScore() {
        return coffeeScore;
    }

    public void setCoffeeScore(double coffeeScore) {
        this.coffeeScore = coffeeScore;
    }

    public double getWifiScore() {
        return wifiScore;
    }

    public void setWifiScore(double wifiScore) {
        this.wifiScore = wifiScore;
    }

    public ArrayList<String> getAtmosphere() {
        return atmosphere;
    }

    public void setAtmosphere(ArrayList<String> atmosphere) {
        this.atmosphere = atmosphere;
    }

    public int getImgID() {
        return imgID;
    }

    public void setImgID(int imgID) {
        this.imgID = imgID;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ArrayList<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(ArrayList<String> amenities) {
        this.amenities = amenities;
    }

    public ArrayList<String> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(ArrayList<String> menuItems) {
        this.menuItems = menuItems;
    }

    public String getBestOrder() {
        return bestOrder;
    }

    public void setBestOrder(String bestOrder) {
        this.bestOrder = bestOrder;
    }
}
