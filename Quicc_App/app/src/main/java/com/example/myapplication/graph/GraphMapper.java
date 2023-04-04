package com.example.myapplication.graph;

import java.util.ArrayList;
import java.util.HashMap;

public class GraphMapper {

    private ArrayList<HashMap<Integer, Integer>> graph;
    private int endNode;
    private int nodeCount;
    private ArrayList<Integer> products;
    private ArrayList<ArrayList<ArrayList<Integer>>> dijkstraList = new ArrayList<>();

    public GraphMapper(Graph graph, ArrayList<Integer> products) {
        this.graph = graph.getGraph();
        this.nodeCount = graph.getNodeCount();
        this.products = products;
        this.endNode = graph.getEndNode();
    }

    public ArrayList<Integer> calc(){
        for(int i = 0; i <= nodeCount;i++){
            dijkstraList.add(new ArrayList<>());
            for(int j = 0;j <= nodeCount;j++){
                dijkstraList.get(i).add(new ArrayList<>());
                if(i == j){
                    dijkstraList.get(i).get(j).add(0);
                }
                else {
                    dijkstraList.get(i).get(j).add(Integer.MAX_VALUE);
                }
            }
        }
        for(int i = 0; i <= nodeCount; i++){
            dijkstra(i);
        }
        ArrayList<Integer> path = new ArrayList<>();
        path.add(0);
        path.add(0);
        ArrayList<ArrayList<Integer>> paths = _calc(products,path,0,new ArrayList<>());
        ArrayList<Integer> ret = new ArrayList<>();
        ret.add(Integer.MAX_VALUE);
        for(ArrayList<Integer> i:paths){
            if(ret.get(0) > i.get(0)){
                ret = i;
            }
        }
        return ret;
    }

    private ArrayList<ArrayList<Integer>> _calc(ArrayList<Integer> visit, ArrayList<Integer> path, int current, ArrayList<ArrayList<Integer>> acc){

        if (visit.isEmpty()){
            mergePaths(path,dijkstraList.get(current).get(endNode));
            acc.add(path);
            return acc;
        }

        for(int i = 0;i < visit.size();i++){
            ArrayList<Integer> pathClone = (ArrayList<Integer>) path.clone();
            mergePaths(pathClone,dijkstraList.get(current).get(visit.get(i)));
            ArrayList<Integer> visitClone = (ArrayList<Integer>) visit.clone();
            visitClone.remove(i);
            _calc(visitClone, pathClone, visit.get(i), acc);
        }
        return acc;
    }

    private void dijkstra(int start){
        ArrayList<Integer> unvisited = new ArrayList<>();
        for(int i = 0; i <= nodeCount;i++){
            unvisited.add(i);
        }
        ArrayList<Integer> path;

        int current = start;
        while (!unvisited.isEmpty()){
            path = dijkstraList.get(start).get(current);
            int currentDistance = path.get(0);
            HashMap<Integer, Integer> currentEdges = graph.get(current);
            for(int visit:unvisited){
                if(dijkstraList.get(start).get(visit).get(0) > currentDistance + currentEdges.get(visit) && currentEdges.get(visit) != Integer.MAX_VALUE){
                    dijkstraList.get(start).set(visit,(ArrayList<Integer>) dijkstraList.get(start).get(current).clone());
                    dijkstraList.get(start).get(visit).set(0, currentDistance + currentEdges.get(visit));
                    dijkstraList.get(start).get(visit).add(visit);
                }
            }
            unvisited.remove((Integer) current);
            ArrayList<Integer> distance = new ArrayList<>();
            for(ArrayList<Integer> i:dijkstraList.get(start)){
                distance.add(i.get(0));
            }
            current = calcCurrent(unvisited,distance);
        }
    }

    private int calcCurrent(ArrayList<Integer> unvisited, ArrayList<Integer> distance){
        int current = 0;
        int currentDistance = Integer.MAX_VALUE;
        for(int i:unvisited){
            if (distance.get(i) < currentDistance){
                currentDistance = distance.get(i);
                current = i;
            }
        }
        return current;
    }

    private ArrayList<Integer> mergePaths(ArrayList<Integer> a, ArrayList<Integer> b){
        a.set(0, a.get(0) + b.get(0));
        for(int i = 1;i < b.size();i++){
            a.add(b.get(i));
        }
        return a;
    }

}
