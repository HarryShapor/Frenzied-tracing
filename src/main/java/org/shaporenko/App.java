package org.shaporenko;

import org.shaporenko.service.WayService;
import org.shaporenko.util.LinkedList;
import org.shaporenko.model.Board;

import java.util.List;

public class App
{

    private final static int height = 500;
    private final static int weight = 500;
    private final static double gridPitch = 1;
    private final static int layers = 1;
    private final static boolean diagonals = false;

    public static void main( String[] args )
    {

        Board board = new Board(height, weight, gridPitch, layers, diagonals);
        System.out.println(board);
        System.out.println();
//        board.matrixToList();
//        WayService wayService = new WayService(board);
//        wayService.allWays();

        List<LinkedList<Integer>> boardList = board.getNeighboringContacts();
        int n = (height * weight);
        int n2 = n * (n-1);
        System.out.println("n - " + n);

        board.splitIntoSegments(5);


    }
    //мусор
    {
        //        int b1 = 0;
//        int b2 = 9;
//        int b3 = 18;
//        Thread t1 = new Thread(new ThreadDFS(0, 2, boardList, n * layers, 1));
//        Thread t2 = new Thread(new ThreadDFS(3, 5, boardList, n * layers, 2));
//        Thread t3 = new Thread(new ThreadDFS(6, 8, boardList, n * layers, 3));
//        Thread t4 = new Thread(new ThreadDFS(9, 16, boardList, n * layers, 4));
//        Thread t5 = new Thread(new ThreadDFS(17, 24, boardList, n * layers, 5));
//        t1.start();
//        t2.start();
//        t3.start();
//        t4.start();
//        t5.start();
        long startTime = System.currentTimeMillis();
//        System.out.println(board.gwtWay(0,35));
//        System.out.println(board.gwtWay(0,1));
//        System.out.println(board.gwtWay(0,15));
//        System.out.println(board.gwtWay(0,20));
//        board.allWays();
/*      //3 сегмента 6x3, 6x3 и 6x2
        List<LinkedList<Integer>> boardList2 = board.adjacencyListsReturn(6,3,1,1,false);
        List<LinkedList<Integer>> boardList3 = board.adjacencyListsReturn(6,2,1,1,false);
        board.allWays(new int[]{1,2,3,7,8,9,13,14,15,19,20,21,25,26,27,31,32,33}, boardList2);
        board.allWays(new int[]{4,5,6,10,11,12,16,17,18,22,23,24,28,29,30,34,35,36}, boardList2);
        board.allWays(new int[]{3,4,9,10,15,16,21,22,27,28,33,34}, boardList3);*/


/*        //25 сегмента 2x2
        List<LinkedList<Integer>> boardList2 = board.adjacencyListsReturn(2,2,1,1,false);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        board.allWays(new int[]{1,2,7,8}, boardList2);
        */

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println(duration / 1000);
        int p = 9;
        for (int i = 0; i < p; i++){
//            System.out.println("Начало - " + (n * i) + " Конец - " + (n * i + (p-1)) + "");
//            System.out.println("До: начало - " + (p * (i+1)) + " Конец - " + (p * (i+1) + (p-1)) + "");
        }
//        Thread thread = new Thread(new ThreadDFS(b, n, boardList, n * layers, 1));
//        thread.start();

        // 1 слой - 1 - 8
        // 2 слой - 9 - 16
        // 3 слой - 17 - 24
        /*
         * запуск от 1 по 4 до 24
         * запуск от 5 по 8 до 24
         * запуск от 9 по 16 до 24
         * запуск от 17 по 24 до 24
         *
         * */

    }
}
