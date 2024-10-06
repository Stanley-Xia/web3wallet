package com.example.web3wallet;

public class Token {
    private String name;
    private String price;
    private int iconResId;
    private double priceChange24h;

    public Token(String name, String price, int iconResId) {
        this.name = name;
        this.price = price;
        this.iconResId = iconResId;
    }

    public Token(String name, String price, int iconResId, double priceChange24h) {
        this.name = name;
        this.price = price;
        this.iconResId = iconResId;
        this.priceChange24h = priceChange24h;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getIconResId() {
        return iconResId;
    }

    public double getPriceChange24h() {
        return priceChange24h;
    }

    public void setPriceChange24h(double priceChange24h) {
        this.priceChange24h = priceChange24h;
    }

}
