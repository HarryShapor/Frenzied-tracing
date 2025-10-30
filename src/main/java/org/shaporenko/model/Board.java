package org.shaporenko.model;

import org.shaporenko.dao.LinkedList;

import java.util.*;

public class Board {

    /**
    * Список списков для представления печатной платы (её контактов) в виде матрицы смежности
    * */
    private List<List<Integer>> board = null;
    private List<LinkedList<Integer>> boardList;

    public Board(int height, int weight,
                 double gridPitch, int layers, boolean diagonals){
        this.adjacencyMatrix(height, weight, gridPitch, layers, diagonals);
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
        for (int i = 0; i < countVerticalPoints; i++){
            for (int j = 0; j < countHorizontalPoints; j++){
                System.out.print(boardMatrix[i][j] + ", ");
            }
            System.out.println();
        }


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




}
