package com.example.mobilemarket;

public class Item {
    private int itemId;
    private String name;
    private String description;
    private double price;
    private double rating;
    private String datePosted;

    public Item(int itemId, String name, String description, double price, double rating, String datePosted) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.rating = rating;
        this.datePosted = datePosted;
    }

    public int getId() { return itemId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public double getRating() { return rating; }
    public String getDatePosted() { return datePosted; }
}