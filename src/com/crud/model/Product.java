package com.crud.model;

public class Product {

    private long   id;
    private String name;
    private double price;
    private int    quantity;

    public Product() {}

    public Product(long id, String name, double price, int quantity) {
        this.id       = id;
        this.name     = name;
        this.price    = price;
        this.quantity = quantity;
    }

    public long   getId()       { return id; }
    public String getName()     { return name; }
    public double getPrice()    { return price; }
    public int    getQuantity() { return quantity; }

    public void setId(long id)           { this.id = id; }
    public void setName(String name)     { this.name = name; }
    public void setPrice(double price)   { this.price = price; }
    public void setQuantity(int qty)     { this.quantity = qty; }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', price=%.2f, quantity=%d}",
                id, name, price, quantity);
    }
}
