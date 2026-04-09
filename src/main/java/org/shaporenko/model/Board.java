package org.shaporenko.model;

import org.shaporenko.util.LinkedList;

import java.util.*;

public class Board {

    /**
    * Список списков для представления печатной платы (её контактов) в виде матрицы смежности
    * */
    //рассматривается удаление данного поля, так как оно бесполезно
    private List<List<Integer>> board = null; //матрица смежности
    private List<LinkedList<Integer>> neighboringContacts; //списки смежности
    private int n; //количество контактных площадок

    private int countVerticalPoints;
    private int countHorizontalPoints;
    private double height;
    private double weight;
    private double gridPitch;
    private int layers;
    private boolean diagonals;


    public Board(int height, int weight,
                 double gridPitch, int layers, boolean diagonals){
        this.height = height;
        this.weight = weight;
        this.gridPitch = gridPitch;
        this.layers = layers;
        this.diagonals = diagonals;

        this.setN();
        //установка параметров контактов
        this.setCountVerticalPoints();
        this.setCountHorizontalPoints();

        this.buildNeighborhoodGraph();

    }

    public List<List<Integer>> getBoard() {
        return board;
    }

    public int getN() {
        return n;
    }

    private void setN(){
        this.n = (int) (this.height * this.weight / Math.pow(this.gridPitch, 2) * this.layers);
    }

    public List<LinkedList<Integer>> getNeighboringContacts() {
        return neighboringContacts;
    }

    /**
    * Метод принимающий параметры высоты, ширины, шага сетки платы, количество слоёв и
     * параметр diagonals, который при значении true учитывает диагональный контакты,
     * как соседние
    * */
    //недоделана
    private void adjacencyMatrix(int height, int weight,
                                                double gridPitch, int layers, boolean diagonals){

        List<List<Integer>> printedCircuitBoard = initializationOfContactPlatformNumbers();

        Map<Integer, int[]> contacts = setTheCoordinatesOfContactPads(printedCircuitBoard);

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
        for (int i = 0; i < this.n*layers; i++){
            this.board.add(i, new ArrayList<>());
            for (int j=0; j < this.n*layers; j++){
                this.board.get(i).add(0);
            }
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

        for (int l = 0; l < layers; l++) {
            for (int n = 0; n < this.n; n++) {
                int el = n + this.n *l;
                List<Integer> boardN = this.board.get(el);
//                    System.out.println(el);
                for (int i = 0; i < countVerticalPoints; i++) {
                    for (int j = 0; j < countHorizontalPoints; j++) {
                        if (!diagonals){
                            if (neighbors(i, j, contacts.get(n)[0], contacts.get(n)[1])) {
                                boardN.remove(printedCircuitBoard.get(i).get(j)+this.n*l);
                                boardN.add(printedCircuitBoard.get(i).get(j)+this.n*l, 1);
                            } /*else {
//                                System.out.println("i, j - " + i + ", " + j);
                                boardN.add(boardMatrix[i][j]*(l+1), 0);
                            }*/
                        }
                        else {
                            if (neighborsDiagonals(i, j, contacts.get(n)[0], contacts.get(n)[1])) {
                                boardN.remove(printedCircuitBoard.get(i).get(j)+this.n*l);
                                boardN.add(printedCircuitBoard.get(i).get(j)+this.n*l, 1);
                            }/* else {
                                boardN.add(boardMatrix[i][j], 0);
                            }*/
                        }
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

    private void setCountVerticalPoints(){
        this.countVerticalPoints = (int) (this.height / this.gridPitch);
    }

    private void setCountHorizontalPoints(){
        this.countHorizontalPoints = (int) (this.weight / this.gridPitch);
    }

    private List<List<Integer>> initializationOfContactPlatformNumbers(){
        List<List<Integer>> contactsPlatform = new ArrayList<>(this.countVerticalPoints);
        int number = 0;
        for (int i=0; i < this.countVerticalPoints; i++){
            contactsPlatform.add(new ArrayList<>(this.countHorizontalPoints));
            for (int j=0; j < this.countVerticalPoints; j++){
                List<Integer> horizontal = contactsPlatform.get(i);
                horizontal.add(number++);
            }
        }
        return contactsPlatform;
    }

    public String showContactPlatform(List<List<Integer>>  printedCircuitBoard){
        StringBuilder builder = new StringBuilder();
        for (List<Integer> horizontal : printedCircuitBoard){
            for (Integer contact : horizontal){
                builder.append(contact + "\t");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    private void initializationOfNeighboringContacts(){
        this.neighboringContacts = new ArrayList<>();

        for (int i = 0; i < this.n * layers; i++){
            this.neighboringContacts.add(i, new LinkedList<>());
        }
    }

    /**
     * Метод принимающий параметры высоты, ширины, шага сетки платы, количество слоёв и
     * параметр diagonals, который при значении true учитывает диагональные контакты,
     * как соседние
     * */
    private List<LinkedList<Integer>> buildNeighborhoodGraph(){

        //создание печатной платы
        List<List<Integer>>  printedCircuitBoard = initializationOfContactPlatformNumbers();

        //вывод номеров контактов
        System.out.println(this.showContactPlatform(printedCircuitBoard));

        //создание координат контактных площадок
        Map<Integer, int[]> contacts = this.setTheCoordinatesOfContactPads(printedCircuitBoard);

        //создание списков смежности контактов
        this.initializationOfNeighboringContacts();

        //определение соседей контактных площадок
        this.determiningTheNeighborsOfContactSites(contacts, printedCircuitBoard);

        return this.neighboringContacts;
    }

    private Map<Integer, int[]> setTheCoordinatesOfContactPads(List<List<Integer>> printedCircuitBoard){
        Map<Integer, int[]> contacts = new HashMap<>();
        for (int i = 0; i < this.countVerticalPoints; i++){
            for (int j = 0; j < this.countHorizontalPoints; j++){
                contacts.put(printedCircuitBoard.get(i).get(j), new int[]{i, j});
            }
        }
        return contacts;
    }

    private void determiningTheNeighborsOfContactSites(Map<Integer, int[]> points,
                                                       List<List<Integer>> printedCircuitBoard){
        int contactFieldNumber = this.getN() / layers;
        for (int l = 0; l < layers; l++) {
            for (int n = 0; n < contactFieldNumber; n++) {
                int el = n + contactFieldNumber *l;
                LinkedList<Integer> boardN = this.neighboringContacts.get(el);
                for (int i = 0; i < countVerticalPoints; i++) {
                    for (int j = 0; j < countHorizontalPoints; j++) {
                        if (diagonals) {
                            if (neighborsDiagonals(i, j, points.get(n)[0], points.get(n)[1])) {
//                                boardN.remove(boardMatrix[i][j]+number*l);
                                boardN.ins(printedCircuitBoard.get(i).get(j) + contactFieldNumber * l);
//                                boardN.ins(1);
                            }
                        }
                        else {
                            if (neighbors(i, j, points.get(n)[0], points.get(n)[1])) {
//                                boardN.ins(boardMatrix[i][j]+number*l, 1);
                                boardN.ins(printedCircuitBoard.get(i).get(j)+contactFieldNumber*l);
//                                boardN.ins(1);
                            }
                        }
                    }
                }
                int down = el + countHorizontalPoints * countVerticalPoints;
                int up = el - countHorizontalPoints * countVerticalPoints;
//                    System.out.println("el - " + el + " down - " + down + " up - " + up);
                if (up >= 0 && up < contactFieldNumber * layers) {
//                        boardN.ins(el + countHorizontalPoints * countVerticalPoints, 1);
                    boardN.ins(up);
                }
//                        boardN.ins(1);
                if (down >= 0 && down < contactFieldNumber*layers) {
                    boardN.ins(down);
//                        boardN.ins(1);
                }
            }
        }
    }


    /**
    * Метод возвращающий являются ли контакты по координатам x1,y1 и x2,y2 соседними
     * Ортоганальное соседство
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
     * Эвклидово соседство
     * */
    private boolean neighborsDiagonals(int x1, int y1, int x2, int y2){
        if ( ((x1 - x2 == 1 || x1 - x2 == -1) && (y1-y2 <= 1 && y1-y2 >= -1))
                || ((y1 - y2 == 1 || y1 - y2 == -1) && (x1-x2 <= 1 && x1-x2 >= -1))){
            return true;
        }
        return false;
    }



    public void matrixToList(){

        if (this.neighboringContacts != null){
            return;
        }
        int n = this.board.size();
        this.neighboringContacts = new ArrayList<>();
        for (int i =0; i <n; i++){
            this.neighboringContacts.add(i, new LinkedList<Integer>());
        }
        int count = 0;
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j ++){
                double arcWeight = this.board.get(i).get(j);
                if (arcWeight == 1){
                    this.neighboringContacts.get(i).ins(j);
                    count++;
                }
            }
        }

//        for (LinkedList<Integer> i : this.neighboringContacts){
//            System.out.println(i);
//        }
        System.out.println("Количество единиц - " + count);
    }


    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        if (this.board != null){
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
        else {
//            for (int i =0; i < this.neighboringContacts.size(); i++){
//                stringBuilder.append("e"+i + "  ");
//            }
//            stringBuilder.append("\n");
            int i = 0;
            for (LinkedList<Integer> element : this.neighboringContacts){
                stringBuilder.append("e" + (i++) + ": ");
                for (Integer j : element) {
                    stringBuilder.append(j + ",  ");
                }
                stringBuilder.append("\n");
            }
            return stringBuilder.toString();
        }
    }

}