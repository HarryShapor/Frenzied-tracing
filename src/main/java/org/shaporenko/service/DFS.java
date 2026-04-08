package org.shaporenko.service;

import org.shaporenko.model.Board;
import org.shaporenko.util.LinkedList;

import java.util.*;

public class DFS {

    private Board board;
    private Set<List<Integer>> routes;
    private List<Integer> path = new ArrayList<>();
    private Set<int[]> edges = new HashSet<>();

    public void setBoard(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public DFS(Board board, Set<List<Integer>> routes) {
        this.board = board;
        this.routes = routes;
    }

    private void dfsRecurs(int src, int dst){
        int p = src;
        Set<Integer> edge = new HashSet<>();
        while (true) {
            this.path.add(p);
            if (p == dst) {
                if (routes.contains(new ArrayList<>(path))){
                    break;
                }
                this.routes.add(new ArrayList<>(this.path));
                this.path.remove(this.path.size() - 1);
                path.clear();
                p = src;
            }

            if (p < this.board.getNeighboringContacts().size()) {
                System.out.println(this.board.getNeighboringContacts().get(p));
                System.out.println();
                for (int val : this.board.getNeighboringContacts().get(p)) {
//                    System.out.println("val - " + val);
                    edge = new HashSet<>();
                    edge.add(src);
                    edge.add(val);
                    if (!edges.contains(edge)) {
//                        this.edges.add(edge);
//                        dfsRecurs(val, dst);
                        p = val;
                    }
                }
            }
            this.edges.remove(edge);
            this.path.remove(this.path.size() - 1);
        }
        System.out.println(routes.size());
    }

    public Set<List<Integer>> dfs(int src, int dst){

        int p = src;
        this.path.clear();
        this.edges.clear();
//        System.out.println("path - "+ path);
        this.path.add(p);
        while (true) {
            int size = this.path.size();
            for (Integer i : this.board.getNeighboringContacts().get(p)) {
//                System.out.println(this.boardList.get(p));
                boolean flag = false;
                for (int[] g : this.edges) {
                    if (Arrays.equals(g, new int[]{p, i}) || Arrays.equals(g, new int[]{i, p})) {
                        flag = true;
                    }
                }
                if (flag) {
                    continue;
                }
                if (!flag && !this.path.contains(i)) {
//                    int d = path.size()-1;
                    this.path.add(i);
                    this.edges.add(new int[]{p, i});
                    p = i;
                    break;
                }
            }
            if (p != dst && size == path.size()) {
//              Возврат к вершине ветвления и удаление рёбер после неё
//                System.out.println(path);
                if (size == 1) {
//                    System.out.println(this.routes);
                    break;
                }
                int d = this.path.get(this.path.size() - 1);
                this.path.remove(this.path.size() - 1);
                try {
                    this.edges.removeIf(i -> i[0] == d);
                    /*for (int[] edge : edges) {
                        if (edge[0] == d) {
                            edges.remove(edge);
                        }
                    }*/
/*                    System.out.print("edges - ");
                    for (int[] edge : edges) {
                        System.out.print(Arrays.toString(edge) + " ");
                    }
                    System.out.println();*/
                } catch (ConcurrentModificationException e) {
//                   break;
                }
                p = this.path.get(this.path.size() - 1);

//                break;
            }
            if (p == dst) {
//                if (!routes.contains(path)){
//                    routes.add(path);
//                    path.remove(path.size()-1);
//                    p = path.get(path.size()-1);
//                }
//                else {
//                    path.remove(path.size()-1);
//                    p = path.get(path.size()-1);
//                }
                int d = this.path.get(this.path.size() - 1);
                List<Integer> pathLocale = new ArrayList<>(this.path);
//                System.out.println(pathLocale);
//                System.out.println(pathLocale.hashCode());
                this.routes.add(pathLocale);
//                System.out.println(this.routes);
//                System.out.println(path);
                this.edges.removeIf(i -> i[0] == d);

//                for (int[] edge : edges){
//                    if (edge[0] == d){
//                        edges.remove(edge);
//                    }
//                }
                /*System.out.print("edges - ");
                for (int[] edge : edges) {
                    System.out.print(Arrays.toString(edge) + " ");
                }
                System.out.println();*/
                this.path.remove(this.path.size() - 1);
                p = this.path.get(this.path.size() - 1);
            }
        }
        return this.routes;
    }

    public static Set<List<Integer>> dfs2(int src, int dst, List<LinkedList<Integer>> boardList){
        Set<List<Integer>> routes = new HashSet<>();
        List<Integer> path = new ArrayList<>();
        Set<int[]> edges = new HashSet<>();
        int p = src;
        path.clear();
        edges.clear();
        path.add(p);
        while (true) {
            int size = path.size();
            for (Integer i : boardList.get(p)) {
                boolean flag = false;
                for (int[] g : edges) {
                    if (Arrays.equals(g, new int[]{p, i}) || Arrays.equals(g, new int[]{i, p})) {
                        flag = true;
                    }
                }
                if (flag) {
                    continue;
                }
                if (!flag && !path.contains(i)) {
                    path.add(i);
                    edges.add(new int[]{p, i});
                    p = i;
                    break;
                }
            }
            if (p != dst && size == path.size()) {
                if (size == 1) {
                    break;
                }
                int d1 = path.get(path.size() - 1);
                path.remove(path.size() - 1);
                try {
                    edges.removeIf(i -> i[0] == d1);
                } catch (ConcurrentModificationException e) {

                }
                p = path.get(path.size() - 1);

            }
            if (p == dst) {
                int d1 = path.get(path.size() - 1);
                List<Integer> pathLocale = new ArrayList<>(path);
                routes.add(pathLocale);
//                System.out.println(routes.size());
                edges.removeIf(i -> i[0] == d1);

                path.remove(path.size() - 1);
                p = path.get(path.size() - 1);
            }
        }
        return routes;
    }

    /*public void dfsIter(int src, int dst){
        Deque<Pair<Integer, Integer>> stack = new ArrayDeque<>();
        // Мапа для отслеживания индексов обработанных соседей для каждого узла
        Map<Integer, Integer> neighborIndices = new HashMap<>();

        // Инициализация начального состояния
        stack.push(new Pair<>(src, 0));
        neighborIndices.put(src, 0);
        this.path.add(src);

        while (!stack.isEmpty()) {
//            System.out.println(routes);
            Pair<Integer, Integer> currentState = stack.peek();
            int currentNode = currentState.getKey();
            int neighborIndex = currentState.getValue();

            // Если достигли целевой вершины
            if (currentNode == dst) {
                this.routes.add(new ArrayList<>(this.path));
                // Откат назад
                stack.pop();
                neighborIndices.remove(currentNode);
                this.path.remove(this.path.size() - 1);
                continue;
            }

            // Получаем список соседей текущего узла
            List<Integer> neighbors = (currentNode < this.board.size()) ? this.board.get(currentNode) : Collections.emptyList();

            // Если есть еще непроверенные соседи
            if (neighborIndex < neighbors.size()) {
                int nextNeighbor = neighbors.get(neighborIndex);

                // Обновляем индекс для текущего узла
                stack.pop();
                stack.push(new Pair<>(currentNode, neighborIndex + 1));

                // Проверяем, можно ли пройти по этому ребру
                Set<Integer> edge = new HashSet<>();
                edge.add(currentNode);
                edge.add(nextNeighbor);

                if (!this.edges.contains(edge)) {
                    // Добавляем ребро и переходим к соседу
                    this.edges.add(edge);
                    this.path.add(nextNeighbor);
                    stack.push(new Pair<>(nextNeighbor, 0));
                    neighborIndices.put(nextNeighbor, 0);
                }
            } else {
                // Все соседи обработаны - откат назад
                stack.pop();
                neighborIndices.remove(currentNode);

                // Удаляем последнее ребро из path
                if (!this.path.isEmpty()) {
                    this.path.remove(this.path.size() - 1);
                }

                // Удаляем ребра, связанные с текущим узлом
                if (!this.path.isEmpty()) {
                    int prevNode = this.path.get(this.path.size() - 1);
                    Set<Integer> edge = new HashSet<>();
                    edge.add(prevNode);
                    edge.add(currentNode);
                    this.edges.remove(edge);
                }
            }
        }
    }*/

}
