package com.example.myapplication.product;

public class Product {
    private String name;
    private int from;
    private int to;
    private float percent;

    public Product(String name, int from, int to, float percent) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.percent = percent;
    }

    public String getName() {
        return name;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public void setTo(int to) {
        this.to = to;
    }
}
