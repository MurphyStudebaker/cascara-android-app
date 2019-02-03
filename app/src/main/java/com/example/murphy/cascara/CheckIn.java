package com.example.murphy.cascara;

/** A simple Java Object to define all of the variables of a Check In
 */
public class CheckIn {
    private String coffeeName;
    private int coffeeImg;
    private String order;
    private double coffeeScore;
    private double wifiScore;

    public CheckIn(String name, String order, int imageId) {
        this.coffeeName = name;
        this.order = order;
        this.coffeeImg = imageId;
    }

    public String getCoffeeName() {
        return coffeeName;
    }

    public void setCoffeeName(String coffeeName) {
        this.coffeeName = coffeeName;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
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

    public int getCoffeeImg() {
        return coffeeImg;
    }

    public void setCoffeeImg(int coffeeImg) {
        this.coffeeImg = coffeeImg;
    }
}
