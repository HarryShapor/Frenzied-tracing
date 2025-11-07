package org.shaporenko.model;

import org.shaporenko.dao.LinkedList;

import java.util.*;

public class Board {

    /**
    * Список списков для представления печатной платы (её контактов) в виде матрицы смежности
    * */
    private List<List<Integer>> board = null;
    private List<LinkedList<Integer>> boardList;
    private int n;

    private Set<List<Integer>> routes = new HashSet<>();
    private List<Integer> path = new ArrayList<>();
    private Set<int[]> edges = new HashSet<>();


    public Board(int height, int weight,
                 double gridPitch, int layers, boolean diagonals){
        this.adjacencyMatrix(height, weight, gridPitch, layers, diagonals);
        this.n = (int) (height * weight * gridPitch * layers);
    }

    /**
    * Метод прнимающий параметры высоты, ширины, шага сетки платы, количество слоёв и
     * параметр diagonals, который при значении true учитывает диагональный контакты,
     * как соседние
    * */
    private void adjacencyMatrix(int height, int weight,
                                                double gridPitch, int layers, boolean diagonals){
        int countVerticalPoints = (int) (height / gridPitch);
        int countHorizontalPoints = (int) (weight / gridPitch);

        int[][] boardMatrix = new int[countVerticalPoints][countHorizontalPoints];
        int number = 0;
        for (int i = 0; i < countVerticalPoints; i++){
            for (int j = 0; j < countHorizontalPoints; j++){
                boardMatrix[i][j] = number++;
            }
        }

        Map<Integer, int[]> points = new HashMap<>();
        for (int i = 0; i < countVerticalPoints; i++){
            for (int j = 0; j < countHorizontalPoints; j++){
                points.put(boardMatrix[i][j], new int[]{i, j});
            }
        }
//        System.out.println(points.size());
//        for (int i=0; i < points.size(); i++){
//            System.out.println(Arrays.toString(points.get(i)));
//        }
        //вывод номеров контактов
//        for (int i = 0; i < countVerticalPoints; i++){
//            for (int j = 0; j < countHorizontalPoints; j++){
//                System.out.print(boardMatrix[i][j] + ", ");
//            }
//            System.out.println();
//        }


        //Формирования матрицы смежности
        this.board = new ArrayList<>();
        for (int i = 0; i < number*layers; i++){
            this.board.add(i, new ArrayList<>());
            for (int j=0; j < number*layers; j++){
                this.board.get(i).add(0);
            }
//            System.out.println(this.board.get(i));
        }

        //создание матрицы смежности
/*        for (int n = 0; n < number; n++) {
//            System.out.println("number - " + n);
            List<Integer> boardN = this.board.get(n);
            for (int i = 0; i < countVerticalPoints; i++) {
                for (int j = 0; j < countHorizontalPoints; j++) {
//                    System.out.println(i + " - i " + j + " - j " +
//                            (points.get(i)[0]) + " - in " + (points.get(i)[1]) + " - jn" +
//                            " i-in = " + (i - points.get(n)[0])
//                            + " j-jn = " + (j - points.get(n)[1]) + " number - " + boardMatrix[i][j]);
//                    System.out.println("Сумма - " + Math.abs((i - points.get(n)[0]) + (j - points.get(n)[1])));
                    if (neighbors(i,j, points.get(n)[0], points.get(n)[1])){
                        boardN.add(boardMatrix[i][j], 1);
                    }
                    else {
                        boardN.add(boardMatrix[i][j], 0);
                    }
                }
//                System.out.println();
            }
        }*/

        if (!diagonals){
            int count = 0;
            for (int l = 0; l < layers; l++) {
                for (int n = 0; n < number; n++) {
                    int el = n + number *l;
                    List<Integer> boardN = this.board.get(el);
//                    System.out.println(el);
                    for (int i = 0; i < countVerticalPoints; i++) {
                        for (int j = 0; j < countHorizontalPoints; j++) {
                            if (neighbors(i, j, points.get(n)[0], points.get(n)[1])) {
                                boardN.remove(boardMatrix[i][j]+number*l);
                                boardN.add(boardMatrix[i][j]+number*l, 1);
                                count++;
                            } /*else {
//                                System.out.println("i, j - " + i + ", " + j);
                                boardN.add(boardMatrix[i][j]*(l+1), 0);
                            }*/
                        }
                    }
                    try {
                        boardN.remove(el + countHorizontalPoints * countVerticalPoints);
                        boardN.add(el + countHorizontalPoints * countVerticalPoints, 1);
                    }
                    catch (IndexOutOfBoundsException e){

                    }
                    try {
                        boardN.remove(el - countHorizontalPoints * countVerticalPoints);
                        boardN.add(el - countHorizontalPoints * countVerticalPoints, 1);
                    }
                    catch (IndexOutOfBoundsException e){

                    }
                }
//            System.out.println("Количество единиц - " + count);
            }
        }
        else {
            for (int l = 0; l < layers; l++) {
                for (int n = 0; n < number; n++) {
//                System.out.println("number - " + n);
                    int el = n + number *l;
                    List<Integer> boardN = this.board.get(el);
                    for (int i = 0; i < countVerticalPoints; i++) {
                        for (int j = 0; j < countHorizontalPoints; j++) {
                            if (neighborsDiagonals(i, j, points.get(n)[0], points.get(n)[1])) {
                                boardN.remove(boardMatrix[i][j]+number*l);
                                boardN.add(boardMatrix[i][j]+number*l, 1);
                            }/* else {
                                boardN.add(boardMatrix[i][j], 0);
                            }*/
                        }
                    }

                    try {
                        boardN.remove(el + countHorizontalPoints * countVerticalPoints);
                        boardN.add(el + countHorizontalPoints * countVerticalPoints, 1);
                    }
                    catch (IndexOutOfBoundsException e){

                    }
                    try {
                        boardN.remove(el - countHorizontalPoints * countVerticalPoints);
                        boardN.add(el - countHorizontalPoints * countVerticalPoints, 1);
                    }
                    catch (IndexOutOfBoundsException e){

                    }
                }
            }
        }
    }

    /**
    * Метод возвращающий являются ли контакты по координатам x1,y1 и x2,y2 соседними
    * */
    private boolean neighbors(int x1, int y1, int x2, int y2){
        if ( ((x1 - x2 == 1 || x1 - x2 == -1) && (y1 - y2 == 0))
                || ((y1 - y2 == 1 || y1 - y2 == -1) && (x1 - x2 == 0))
        ){
            return true;
        }
        return false;
    }

    /**
     * Метод возвращающий являются ли контакты по координатам x1,y1 и x2,y2 соседними,
     * учитывая диагональные
     * */
    private boolean neighborsDiagonals(int x1, int y1, int x2, int y2){
        if ( ((x1 - x2 == 1 || x1 - x2 == -1) && (y1-y2 <= 1 && y1-y2 >= -1))
                || ((y1 - y2 == 1 || y1 - y2 == -1) && (x1-x2 <= 1 && x1-x2 >= -1))){
            return true;
        }
        return false;
    }



    public void matrixToList(){

        if (this.boardList != null){
            return;
        }
        int n = this.board.size();
        this.boardList = new ArrayList<>();
        for (int i =0; i <n; i++){
            this.boardList.add(i, new LinkedList<Integer>());
        }
        int count = 0;
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j ++){
                double arcWeight = this.board.get(i).get(j);
                if (arcWeight == 1){
                    this.boardList.get(i).ins(j);
                    count++;
                }
            }
        }

//        for (LinkedList<Integer> i : this.boardList){
//            System.out.println(i);
//        }
        System.out.println("Количество единиц - " + count);
    }


    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i =0; i < this.board.size(); i++){
            stringBuilder.append("e"+i + "  ");
        }
        stringBuilder.append("\n");
        for (List<Integer> element : this.board){
            for (Integer i : element) {
                stringBuilder.append(i + ",  ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();

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

        for (int i=0; i < n; i++){
            for (int j=i; j<n; j++){
                if (i != j) {
//                    System.out.println("i - " + i + ", j - " + j);
                    this.dfs(i, j);
                }
            }
        }
//        for (List<Integer> lst : this.routes) {
//            System.out.println(lst);
//        }
        System.out.println("Количество всех путей: " + this.routes.size());
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

            if (p < this.boardList.size()) {
                System.out.println(this.boardList.get(p));
                System.out.println();
                for (int val : this.boardList.get(p)) {
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


    public void dfs(int src, int dst){

        int p = src;
        this.path.clear();
        this.edges.clear();
//        System.out.println("path - "+ path);
        this.path.add(p);
        while (true) {
            int size = this.path.size();
            for (Integer i : this.boardList.get(p)) {
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

class Pair<K, V> {
    private final K key;
    private final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() { return key; }
    public V getValue() { return value; }
}