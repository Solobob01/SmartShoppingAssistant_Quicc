package com.example.myapplication.graph;

import com.example.myapplication.MainActivity;
import com.example.myapplication.constants.Constants;
import com.example.myapplication.coordinateTranslator.CoordinateTranslator;
import com.example.myapplication.product.Product;
import com.example.myapplication.ui.map.TestView;

import java.util.ArrayList;
import java.util.HashMap;

public class Node {
    private double x;
    private double y;
    private ArrayList<Node> neighbours;
    private int index;
    private boolean end;

    private boolean isProduct = false;
    private HashMap<Integer, ArrayList<Product>> productsBetween;

    private Product product;
    static CoordinateTranslator coordinateTranslator = new CoordinateTranslator(
            Constants.BLLAT,
            Constants.BLLNG,
            Constants.TLLAT,
            Constants.TLLNG,
            Constants.BRLAT,
            Constants.BRLNG,
            Constants.TRLAT,
            Constants.TRLNG);


    public Node(double x, double y, ArrayList<Node> neighbours, int index, boolean end, Product product) {
        if(product != null){
            isProduct = true;
            this.product = product;
            this.x = x;
            this.y = y;
        } else {
            // TECHNICALLY Y SHOULD BE LATITUDE BUT I PUT X Y SSO THAT WE CAN JUST PASTE GOOGLE MAPS COORDS
            ArrayList<Double> coords = coordinateTranslator.coordTranslateToPhone(x,y, TestView.HOR_SIZE, TestView.VERT_SIZE);
            this.x = coords.get(0);
            this.y = coords.get(1);
        }

        this.neighbours = neighbours;
        this.index = index;
        this.end = end;
        productsBetween = new HashMap<>();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public ArrayList<Node> getNeighbours() {
        return neighbours;
    }

    public int getIndex() {
        return index;
    }

    public boolean isEnd() {
        return end;
    }

    public boolean isProduct() {
        return isProduct;
    }

    public Product getProduct() {
        return product;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public ArrayList<Product> getProductsBetween(Node node){
        return getProductsBetween(node.getIndex());
    }

    public ArrayList<Product> getProductsBetween(int neighborIndex){
        try{
            return productsBetween.get(neighborIndex);
        } catch (Exception e){
            return null;
        }
    }

    public void setProductsBetween(int index,ArrayList<Product> newList) {
        productsBetween.put(index,newList);
    }

    public void addProductsBetween(Product newProduct, Node node) throws Exception {
        addProductsBetween(newProduct,node.getIndex());
    }

    public void addProductsBetween(Product newProduct,int neighborIndex) throws Exception {
        boolean isNeighbor = false;
        Node neighbor = null;
        for(Node node:neighbours){
            if (node.getIndex() == neighborIndex){
                isNeighbor = true;
                neighbor = node;
            }
        }
        if(!isNeighbor){
            throw new Exception();
        }
        if (getProductsBetween(neighborIndex) == null){
            ArrayList<Product> newList = new ArrayList<>();
            newList.add(newProduct);
            productsBetween.put(neighborIndex,newList);
            neighbor.setProductsBetween(getIndex(),newList);
        } else{
            getProductsBetween(neighborIndex).add(newProduct);
            neighbor.getProductsBetween(getIndex()).add(newProduct);
        }

    }

    public void removeProductsBetween(int index) throws Exception {
        productsBetween.remove(index);
        boolean deleted = false;
        for (Node node:neighbours){
            if(node.getIndex() == index){
                node._removeProductsBetween(getIndex());
                deleted = true;
            }
        }
        if (!deleted){
            throw new Exception();
        }
    }

    private void _removeProductsBetween(int index){
        productsBetween.remove(index);
    }
}
