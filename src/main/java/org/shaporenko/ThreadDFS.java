package org.shaporenko;

import org.shaporenko.util.LinkedList;
import org.shaporenko.model.Board;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ThreadDFS implements Runnable {

    private int begin;
    private int end;
    private List<LinkedList<Integer>> boardList;
    private int n;
    private int number;

    public ThreadDFS(int b, int e, List<LinkedList<Integer>> boardList, int n, int number){
        this.begin = b;
        this.end = e;
        this.boardList = boardList;
        this.n = n;
        this.number = number;
    }

    @Override
    public void run() {
        Set<List<Integer>> routes = new HashSet<>();
        for (int i=this.begin; i < end; i++){
            for (int j = i; j < n; j++) {
                if (i != j){
                    System.out.println("Thread " + number + " - запуск " + i + ", " + j);
//                    routes = Board.dfs2(i,j, this.boardList);
//                    System.out.println(Board.getNeighboringContacts());
                }
            }
        }
        System.out.println(routes.size());

    }
}
