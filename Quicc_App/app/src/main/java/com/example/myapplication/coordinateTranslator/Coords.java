package com.example.myapplication.coordinateTranslator;

import java.util.ArrayList;

public class Coords{
    public double x;
    public double y;

    public double distanceTo(Coords c){
        return Math.sqrt((x-c.x)*(x-c.x)+(y-c.y)*(y-c.y));
    }

    void translateByDegree(double deg){
        deg = Math.toRadians(deg);
        double xx, yy;
        xx = x*Math.cos(deg) - y*Math.sin(deg);
        yy = y*Math.cos(deg) + x*Math.sin(deg);
        x = xx;
        y = yy;
    }

    public Coords(double y, double x){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return x + "," + y;
    }

    ArrayList<Double> toCoordArray(){
        ArrayList<Double> arr = new ArrayList<>();
        arr.add(x);
        arr.add(y);
        return arr;
    }
}
