package org.shaporenko.service;

import org.shaporenko.model.Board;
import org.shaporenko.util.LinkedList;

import java.util.*;

public class AllPathsFinder {

    private Board board;

    public void setBoard(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public AllPathsFinder(Board board) {
        this.board = board;
    }

    private void dfsRecurs(int src, int dst){

        Set<List<Integer>> paths = new HashSet<>();
        List<Integer> currentPath = new ArrayList<>();
        Set<int[]> edges = new HashSet<>();

        int p = src;
        Set<Integer> edge = new HashSet<>();
        while (true) {
            currentPath.add(p);
            if (p == dst) {
                if (paths.contains(new ArrayList<>(currentPath))){
                    break;
                }
                paths.add(new ArrayList<>(currentPath));
                currentPath.remove(currentPath.size() - 1);
                currentPath.clear();
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
            edges.remove(edge);
            currentPath.remove(currentPath.size() - 1);
        }
        System.out.println(paths.size());
    }

    private int chooseTheDirectionOfThePath(int current, List<Integer> currentPath, Set<int[]> edges){
        boolean flag;

        for (Integer currnetNeighboring : this.board.getNeighboringContacts().get(current)) {
            flag = false;
            //цикл по парам рёбер
            for (int[] edge : edges) {
                //если ребро с текущим контактом и его соседом есть, то идём дальше
                if (Arrays.equals(edge, new int[]{current, currnetNeighboring})
                        || Arrays.equals(edge, new int[]{currnetNeighboring, current})) {
                    flag = true;
                }
            }
            if (flag) {
                continue;
            }
            //если flag == false и в текущем питу нет текущего контакта
            if (!flag && !currentPath.contains(currnetNeighboring)) {
                current = currnetNeighboring;
                break;
            }
        }
        return current;
    }

    public Set<List<Integer>> findAllSimplePathsBetweenSourceAndTarget(int source, int target){

        //инициализация структур данных
        Set<List<Integer>> paths = new HashSet<>();
        List<Integer> currentPath = new ArrayList<>();
        Set<int[]> edges = new HashSet<>();


        int current = source;
        int size;

        //Добавление в текущий путь начальную точку
        currentPath.add(current);
        while (true) {
            size = currentPath.size();

            int currentNew = chooseTheDirectionOfThePath(current, currentPath, edges);
            if (current != currentNew) {
                currentPath.add(currentNew); //добавляем текущий контакт
                edges.add(new int[]{current, currentNew}); //добавляем ребро с ним
                current = currentNew;
            }

            if (current != target && size == currentPath.size()) {
//              Возврат к вершине ветвления и удаление рёбер после неё
                if (size == 1) {
                    break;
                }
                int d = currentPath.get(currentPath.size() - 1);
                currentPath.remove(currentPath.size() - 1);
                try {
                    edges.removeIf(i -> i[0] == d);
                } catch (ConcurrentModificationException e) {
                }
                current = currentPath.get(currentPath.size() - 1);
            }

            if (current == target) {
                int d = currentPath.get(currentPath.size() - 1);
                List<Integer> pathLocale = new ArrayList<>(currentPath);
                paths.add(pathLocale);
                edges.removeIf(i -> i[0] == d);
                currentPath.remove(currentPath.size() - 1);
                current = currentPath.get(currentPath.size() - 1);
            }
        }
        return paths;
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
//                System.out.println(paths.size());
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
        this.currentPath.add(src);

        while (!stack.isEmpty()) {
//            System.out.println(paths);
            Pair<Integer, Integer> currentState = stack.peek();
            int currentNode = currentState.getKey();
            int neighborIndex = currentState.getValue();

            // Если достигли целевой вершины
            if (currentNode == dst) {
                this.paths.add(new ArrayList<>(this.currentPath));
                // Откат назад
                stack.pop();
                neighborIndices.remove(currentNode);
                this.currentPath.remove(this.currentPath.size() - 1);
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
                    this.currentPath.add(nextNeighbor);
                    stack.push(new Pair<>(nextNeighbor, 0));
                    neighborIndices.put(nextNeighbor, 0);
                }
            } else {
                // Все соседи обработаны - откат назад
                stack.pop();
                neighborIndices.remove(currentNode);

                // Удаляем последнее ребро из currentPath
                if (!this.currentPath.isEmpty()) {
                    this.currentPath.remove(this.currentPath.size() - 1);
                }

                // Удаляем ребра, связанные с текущим узлом
                if (!this.currentPath.isEmpty()) {
                    int prevNode = this.currentPath.get(this.currentPath.size() - 1);
                    Set<Integer> edge = new HashSet<>();
                    edge.add(prevNode);
                    edge.add(currentNode);
                    this.edges.remove(edge);
                }
            }
        }
    }*/

}
