package org.shaporenko.service;

import org.shaporenko.model.Board;
import org.shaporenko.util.LinkedList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.shaporenko.service.AllPathsFinder.*;
public class WayService {

    private Board board;
    private AllPathsFinder dfs;
    private Set<List<Integer>> routes = new HashSet<>();
//    private List<Integer> path = new ArrayList<>();
//    private Set<int[]> edges = new HashSet<>();

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Set<List<Integer>> getRoutes() {
        return routes;
    }

    public void setRoutes(Set<List<Integer>> routes) {
        this.routes = routes;
    }

    public WayService(Board board) {
        this.board = board;
//        this.path = path;
//        this.edges = edges;
        dfs = new AllPathsFinder(this.board);
    }

    public long allWays(int[] segment, List<LinkedList<Integer>> segLists){
        Set<List<Integer>> routes = null;
        long count = 0;
        for (int i=0; i < segment.length; i++){
            for (int j=i; j<segment.length; j++){
                if (i != j) {
//                    System.out.println("i - " + (segment[i]-1) + ", j - " + (segment[j]-1));
                    routes = dfs2(i, j, segLists);
                    count += routes.size();
                }
            }
        }
        System.out.println("Количество всех путей: " + count);
        return count;
    }



    public int gwtWay(int i, int j){
        Set<List<Integer>> routes = dfs2(i, j, this.board.getNeighboringContacts());

        return routes.size();
    }

    public void allWays(){
//        List<Integer> lst = this.dfs();
//        for (Integer i : lst) {
//                System.out.print(i + "  ");
//            }
//        this.dfs(0,8);
//        for (Integer i : path) {
//                System.out.print(i + "  ");
//            }
        int maxSize = 0;
        for (int i=0; i < this.board.getN(); i++){
            for (int j=i; j<this.board.getN(); j++){
                if (i != j) {
//                    System.out.println("i - " + i + ", j - " + j);
                    Set<List<Integer>> routes1 = dfs.findAllSimplePathsBetweenSourceAndTarget(i, j);
                    for (List<Integer> route : routes1) {
                        if (maxSize < route.size()){
                            maxSize = route.size();
                        }
                        this.routes.add(route);
                    }
                }
            }
        }
//        for (List<Integer> lst : this.routes) {
//            System.out.println(lst);
//        }
        System.out.println("Количество всех путей: " + this.routes.size());
        System.out.println("Максимальная длина пути: " + maxSize);
    }



}
