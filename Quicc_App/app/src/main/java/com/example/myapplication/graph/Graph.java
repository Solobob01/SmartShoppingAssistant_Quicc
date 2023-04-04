package com.example.myapplication.graph;

import android.content.res.AssetManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.myapplication.product.Product;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Graph {
    private ArrayList<HashMap<Integer, Integer>> graph;
    private int nodeCount;
    private int endNode;

    private ArrayList<Node> nodeList;

    public Graph(ArrayList<Node> map) {
        nodeList = map;
        graph = new ArrayList<>();
        for (Node node : map) {
            graph.add(new HashMap<>());
        }
        for (Node node : map) {
            for(Node otherNode:map){
                if(node.getIndex() != otherNode.getIndex()) {
                    graph.get(node.getIndex()).put(otherNode.getIndex(), Integer.MAX_VALUE);
                } else{
                    graph.get(node.getIndex()).put(node.getIndex(),0);
                }
            }
            for (Node neighbors : node.getNeighbours()) {
                graph.get(node.getIndex()).put(neighbors.getIndex(), (int) Math.round(distance(node.getX(), neighbors.getX(), node.getY(), neighbors.getY())));
                graph.get(neighbors.getIndex()).put(node.getIndex(), (int) Math.round(distance(node.getX(), neighbors.getX(), node.getY(), neighbors.getY())));
            }
            if (node.isEnd()) {
                endNode = node.getIndex();
            }
        }
        nodeCount = graph.size() - 1;
    }

    private Graph(ArrayList<HashMap<Integer, Integer>> graph, int endNode) {
        this.graph = graph;
        this.endNode = endNode;
        nodeCount = graph.size() - 1;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private ArrayList<Integer> buildGraph(ArrayList<Product> products) {
        ArrayList<Integer> productNodes = new ArrayList<>();
        while (!products.isEmpty()) {
            Product product = products.get(0);
            int edgeLength = graph.get(product.getFrom()).get(product.getTo());
            graph.add(new HashMap<>());
            nodeCount++;
            Node productNode = new Node(0,0,new ArrayList<>(),nodeCount,false,product);
            productNode.setX(((nodeList.get(product.getTo()).getX()-nodeList.get(product.getFrom()).getX())*product.getPercent())+nodeList.get(product.getFrom()).getX());
            productNode.setY(((nodeList.get(product.getTo()).getY()-nodeList.get(product.getFrom()).getY())*product.getPercent())+nodeList.get(product.getFrom()).getY());
            productNode.getNeighbours().add(nodeList.get(product.getFrom()));
            productNode.getNeighbours().add(nodeList.get(product.getTo()));
            ArrayList<Product> productsBetween = nodeList.get(product.getFrom()).getProductsBetween(product.getTo());
            if(productsBetween != null) {
                ArrayList<Product> productsBetweenFrom = toArrayList(productsBetween.stream().filter(p ->
                {
                    float percent = p.getPercent();
                    if (p.getTo() == product.getFrom()) {
                        percent = 1 - percent;
                    }
                    return (percent <= product.getPercent()) && (!p.getName().equals(product.getName()));
                }).toArray());
                ArrayList<Product> productsBetweenTo = toArrayList(productsBetween.stream().filter(p ->
                {
                    float percent = p.getPercent();
                    if (p.getTo() == product.getFrom()) {
                        percent = 1 - percent;
                    }
                    return percent > product.getPercent();
                }).toArray());
                if(!productsBetweenFrom.isEmpty()) {
                    productNode.setProductsBetween(product.getFrom(), productsBetweenFrom);
                    nodeList.get(product.getFrom()).setProductsBetween(productNode.getIndex(),productsBetweenFrom);
                }
                if(!productsBetweenTo.isEmpty()) {
                    productNode.setProductsBetween(product.getTo(), productsBetweenTo);
                    nodeList.get(product.getTo()).setProductsBetween(productNode.getIndex(),productsBetweenFrom);
                }
            }
            try {
                nodeList.get(product.getFrom()).removeProductsBetween(product.getTo());
            } catch (Exception ignored) {
            }

            nodeList.get(product.getFrom()).getNeighbours().remove(nodeList.get(product.getTo()));
            nodeList.get(product.getTo()).getNeighbours().remove(nodeList.get(product.getFrom()));
            nodeList.add(productNode);

            for(int i = 0; i<graph.size();i++){
                graph.get(i).put(nodeCount,Integer.MAX_VALUE);
                graph.get(nodeCount).put(i,Integer.MAX_VALUE);
                if(i == nodeCount){
                    graph.get(i).put(i,0);
                }
            }
            graph.get(product.getFrom()).put(product.getTo(), Integer.MAX_VALUE);
            graph.get(product.getFrom()).put(nodeCount, Math.round(((float) edgeLength) * product.getPercent()));
            graph.get(nodeCount).put(product.getFrom(), Math.round(((float) edgeLength) * product.getPercent()));
            graph.get(product.getTo()).put(nodeCount, Math.round(((float) edgeLength) * (1 - product.getPercent())));
            graph.get(nodeCount).put(product.getTo(), Math.round(((float) edgeLength) * (1 - product.getPercent())));
            productNodes.add(nodeCount);
            products.remove(0);
            for (Product otherProduct : products) {
                if (otherProduct.getTo() == product.getTo() && otherProduct.getFrom() == product.getFrom()) {
                    if (product.getPercent() < otherProduct.getPercent()) {
                        otherProduct.setPercent(otherProduct.getPercent() / product.getPercent());
                        otherProduct.setTo(nodeCount);
                    } else {
                        otherProduct.setPercent((otherProduct.getPercent() / product.getPercent()) - 1);
                        otherProduct.setFrom(nodeCount);
                    }
                } else if (otherProduct.getFrom() == product.getTo() && otherProduct.getTo() == product.getFrom()) {
                    float rotatePercent = 1 - otherProduct.getPercent();

                    if (rotatePercent < otherProduct.getPercent()) {
                        otherProduct.setPercent(1 - (rotatePercent / product.getPercent()));
                        otherProduct.setFrom(nodeCount);
                    } else {
                        otherProduct.setPercent(1 - ((rotatePercent / product.getPercent()) - 1));
                        otherProduct.setTo(nodeCount);
                    }
                }
            }

        }
        return productNodes;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Node> calc(ArrayList<Product> products) {
        ArrayList<Integer> productNodes = buildGraph(products);
        ArrayList<Integer> nodeNumList = (new GraphMapper(this, productNodes)).calc();
        System.out.println(nodeNumList.get(0));
        nodeNumList.remove(0);
        Object[] nodeArray = nodeNumList.stream().map(in -> nodeList.get(in)).toArray();
        Node[] ret = new Node[nodeArray.length];
        for(int i = 0; i < nodeArray.length;i++){
            ret[i] = (Node) nodeArray[i];
        }
        return new ArrayList<>(Arrays.asList(ret));

    }

    public static ArrayList<Product> mergeList(ArrayList<Product> l1, ArrayList<Product> l2){
        if(l2!=null) {
            for (Product product : l2) {
                if (!l1.contains(product)) {
                    l1.add(product);
                }
            }
        }
        return l1;
    }

    public ArrayList<Product> getProductsOnRoute(ArrayList<Node> route){
        ArrayList<Product> ret = new ArrayList<>();
        for(int i = 0; i < route.size()-1; i++){
            mergeList(ret,route.get(i).getProductsBetween(route.get(i+1).getIndex()));
        }
        return ret;
    }


    public ArrayList<HashMap<Integer, Integer>> getGraph() {
        return graph;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public int getEndNode() {
        return endNode;
    }

    public static double distance(double x1, double x2, double y1,
                                  double y2) {
        double dx = x2-x1;
        double dy = y2-y1;

        double distance = Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2));

        return distance;
    }

    public static double distance(double x1, double x2, double y1,
                                  double y2, double percent) {
        double dx = x2-x1;
        double dy = y2-y1;

        double distance = Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2)) * percent;

        return distance;
    }
    private static ArrayList<Product> toArrayList(Object[] arr){
        ArrayList<Product> ret = new ArrayList<>();
        for(Object o:arr){
            ret.add((Product) o);
        }
        return ret;
    }

    public void integrateProducts(ArrayList<Product> products){
        for(Product product:products){
            for(Node node:nodeList){
                if(node.getIndex() == product.getFrom()){
                    try {
                        node.addProductsBetween(product,product.getTo());
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    static public Graph createGraph(AssetManager assetManager, String filePath) throws IOException {
        ArrayList<Node> listOfNodes = new ArrayList<>();
        InputStream is = assetManager.open(filePath);;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        int numberNodes = Integer.parseInt(reader.readLine());
        listOfNodes = makeListOfNodes(numberNodes, reader);
        listOfNodes = makeListOfNeigh(numberNodes, reader, listOfNodes);

        return new Graph(listOfNodes);
    }

    public static ArrayList<Node> makeListOfNeigh(int numberNodes, BufferedReader reader, ArrayList<Node> listOfAllNodes) throws IOException {
        String line;
        for(int i = 0;i < numberNodes;i++){
            line = reader.readLine();
            String[] lineRead = line.split(" ");
            int contor = 0;
            int indexNode = -1;
            for(String node : lineRead){
                if(contor == 0){
                    indexNode = Integer.parseInt(node);
                    contor++;
                    continue;
                }
                int indexNeighNode = Integer.parseInt(node);
                Node startNode = listOfAllNodes.get(indexNode);
                Node endNode = listOfAllNodes.get(indexNeighNode);
                startNode.getNeighbours().add(endNode);
            }
        }
        return listOfAllNodes;
    }

    public static ArrayList<Node> makeListOfNodes(int numberNodes, BufferedReader reader) throws IOException {
        ArrayList<Node> listOfNodes = new ArrayList<>();
        String line;
        for(int i = 0;i < numberNodes;i++){
            line = reader.readLine();
            String[] lineRead = line.split(" ");
            int index = Integer.parseInt(lineRead[0]);
            Double x = Double.parseDouble(lineRead[1]);
            Double y = Double.parseDouble(lineRead[2]);
            Boolean end = Boolean.parseBoolean(lineRead[3]);
            listOfNodes.add(new Node(x, y, new ArrayList<>(), index, end, null));
        }
        return listOfNodes;
    }

    @Override
    public String toString() {
        return "Graph{" +
                "graph=" + graph +
                ", nodeCount=" + nodeCount +
                ", endNode=" + endNode +
                ", nodeList=" + nodeList +
                '}';
    }
}
