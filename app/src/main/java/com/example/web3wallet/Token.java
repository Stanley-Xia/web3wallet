package com.example.web3wallet;

public class Token {
    private String name;
    private String price;
    private int iconResId;

    public Token(String name, String price, int iconResId) {
        this.name = name;
        this.price = price;
        this.iconResId = iconResId;
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
}
