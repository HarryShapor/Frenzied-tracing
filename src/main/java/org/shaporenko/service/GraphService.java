package org.shaporenko.service;

import lombok.RequiredArgsConstructor;
import org.shaporenko.dto.board.BoardParameters;
import org.shaporenko.entity.Board;
import org.shaporenko.repository.BoardRepository;
import org.shaporenko.util.LinkedList;
import org.shaporenko.util.NeighborhoodUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GraphService {

    private final BoardRepository boardRepository;

    public List<LinkedList<Integer>> getNeighborhoodGraph(Long id){

        return buildNeighborhoodGraph(boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("")));
    }

    private static List<List<Integer>> convertToList(List<LinkedList<Integer>> customList) {
        return customList.stream()
                .map(linkedList -> {
                    List<Integer> list = new java.util.ArrayList<>();
                    for (int i = 0; i < linkedList.length; i++) {
                        list.add(linkedList.ret(i));
                    }
                    return list;
                })
                .collect(Collectors.toList());
    }

    public List<LinkedList<Integer>> buildNeighborhoodGraph(
            Board board){

        //создание печатной платы
        List<List<Integer>> printedCircuitBoard = createOfContactPlatformNumbers(board);

        //вывод номеров контактов
//        System.out.println(this.showContactPlatform(printedCircuitBoard));

        //создание координат контактных площадок
        Map<Integer, int[]> contacts = this.setTheCoordinatesOfContactPads(printedCircuitBoard, board);

        //создание списков смежности контактов
        List<LinkedList<Integer>> neighboringContacts = initializationOfNeighboringContacts(board);

        //определение соседей контактных площадок
        neighboringContacts = determiningTheNeighborsOfContactSites(contacts, printedCircuitBoard,
                board, neighboringContacts);

        return neighboringContacts;
    }

    public Map<Integer, LinkedList<Integer>> buildNeighborhoodGraph(
            Board board, List<Integer> segment){

        //создание печатной платы
        List<List<Integer>> printedCircuitBoard = createOfContactPlatformNumbers(board, segment);

        //создание координат контактных площадок
        Map<Integer, int[]> contacts = this.setTheCoordinatesOfContactPads(printedCircuitBoard, board);

        //создание списков смежности контактов
        Map<Integer, LinkedList<Integer>> neighboringContacts = initializationOfNeighboringContacts(board, segment);

        //определение соседей контактных площадок
        neighboringContacts = determiningTheNeighborsOfContactSites(contacts, printedCircuitBoard,
                board, neighboringContacts, segment);

        return neighboringContacts;
    }

    private Map<Integer, LinkedList<Integer>> initializationOfNeighboringContacts(
            Board board, List<Integer> segment){
        Map<Integer, LinkedList<Integer>> neighboringContacts = new HashMap<>();

        for (int i = 0; i < board.getLayers() * board.getN(); i++){
            neighboringContacts.put(segment.get(i), new LinkedList<>());
        }

        return neighboringContacts;
    }

    private List<LinkedList<Integer>> initializationOfNeighboringContacts(
            Board board){
        List<LinkedList<Integer>> neighboringContacts = new ArrayList<>();

        for (int i = 0; i < board.getLayers() * board.getN(); i++){
            neighboringContacts.add(i, new LinkedList<>());
        }

        return neighboringContacts;
    }

    private List<LinkedList<Integer>> determiningTheNeighborsOfContactSites(
            Map<Integer, int[]> points, List<List<Integer>> printedCircuitBoard, Board board,
            List<LinkedList<Integer>> neighboringContacts){
        int contactFieldNumber = board.getN() / board.getLayers();
        for (int l = 0; l < board.getLayers(); l++) {
            for (int n = 0; n < contactFieldNumber; n++) {
                int el = n + contactFieldNumber *l;
                LinkedList<Integer> boardN = neighboringContacts.get(el);
                for (int i = 0; i < board.getCountVerticalPoints(); i++) {
                    for (int j = 0; j < board.getCountHorizontalPoints(); j++) {
                        if (board.getDiagonals()) {
                            if (NeighborhoodUtils.areDiagonalNeighbors(i, j, points.get(n)[0],
                                    points.get(n)[1])) {
                                boardN.ins(printedCircuitBoard.get(i).get(j) + contactFieldNumber * l);
                            }
                        }
                        else {
                            if (NeighborhoodUtils.areOrthogonalNeighbors(i, j, points.get(n)[0],
                                    points.get(n)[1])) {
                                boardN.ins(printedCircuitBoard.get(i).get(j)+contactFieldNumber*l);
                            }
                        }
                    }
                }
                int down = el + board.getCountHorizontalPoints() * board.getCountVerticalPoints();
                int up = el - board.getCountVerticalPoints() * board.getCountHorizontalPoints();
                if (up >= 0 && up < contactFieldNumber * board.getLayers()) {
                    boardN.ins(up);
                }
                if (down >= 0 && down < contactFieldNumber*board.getLayers()) {
                    boardN.ins(down);
                }
            }
        }
        return neighboringContacts;
    }

    //определение соседства
    //возможно segment не нужен и можно упростить
    private Map<Integer, LinkedList<Integer>> determiningTheNeighborsOfContactSites(
            Map<Integer, int[]> points, List<List<Integer>> printedCircuitBoard, Board board,
            Map<Integer, LinkedList<Integer>> neighboringContacts, List<Integer> segment){
        int contactFieldNumber = board.getN() / board.getLayers();
        for (int l = 0; l < board.getLayers(); l++) {
            for (int n = 0; n < contactFieldNumber; n++) {
                int el = n + contactFieldNumber *l;
                LinkedList<Integer> boardN = neighboringContacts.get(segment.get(el));
                for (int i = 0; i < board.getCountVerticalPoints(); i++) {
                    for (int j = 0; j < board.getCountHorizontalPoints(); j++) {
                        if (board.getDiagonals()) {
                            if (NeighborhoodUtils.areDiagonalNeighbors(i, j, points.get(segment.get(n))[0],
                                    points.get(segment.get(n))[1])) {
                                boardN.ins(printedCircuitBoard.get(i).get(j) + contactFieldNumber * l);
                            }
                        }
                        else {
                            if (NeighborhoodUtils.areOrthogonalNeighbors(i, j, points.get(segment.get(n))[0],
                                    points.get(segment.get(n))[1])) {
                                boardN.ins(printedCircuitBoard.get(i).get(j)+contactFieldNumber*l);
                            }
                        }
                    }
                }
                int down = el + board.getCountHorizontalPoints() * board.getCountVerticalPoints();
                int up = el - board.getCountVerticalPoints() * board.getCountHorizontalPoints();
                if (up >= 0 && up < contactFieldNumber * board.getLayers()) {
                    boardN.ins(up);
                }
                if (down >= 0 && down < contactFieldNumber*board.getLayers()) {
                    boardN.ins(down);
                }
            }
        }
        return neighboringContacts;
    }


    //создание координат каждой вершины
    private Map<Integer, int[]> setTheCoordinatesOfContactPads(List<List<Integer>> printedCircuitBoard,
                                                               Board board){
        Map<Integer, int[]> contacts = new HashMap<>();
        for (int i = 0; i < board.getCountVerticalPoints(); i++){
            for (int j = 0; j < board.getCountHorizontalPoints(); j++){
                contacts.put(printedCircuitBoard.get(i).get(j), new int[]{i, j});
            }
        }
        return contacts;
    }

    public Map<Integer, int[]> getCoordinate(BoardParameters board) {

        Integer countVerticalPoints = (int) (board.height() / board.gridPitch());
        Integer countHorizontalPoints = (int) (board.width() / board.gridPitch());

        List<List<Integer>> contactsPlatform = new ArrayList<>(countVerticalPoints);
        int number = 0;
        for (int i=0; i < countVerticalPoints; i++){
            contactsPlatform.add(new ArrayList<>(countHorizontalPoints));
            for (int j=0; j < countHorizontalPoints; j++){
                List<Integer> horizontal = contactsPlatform.get(i);
                horizontal.add(number++);
            }
        }


        Map<Integer, int[]> contacts = new HashMap<>();
        for (int i = 0; i < countVerticalPoints; i++) {
            for (int j = 0; j < countHorizontalPoints; j++) {
                contacts.put(contactsPlatform.get(i).get(j), new int[]{i, j});
            }
        }
        return contacts;
    }

    //создание номеров вершин графа
    private List<List<Integer>> createOfContactPlatformNumbers(Board board){
        List<List<Integer>> contactsPlatform = new ArrayList<>(board.getCountVerticalPoints());
        int number = 0;
        for (int i=0; i < board.getCountVerticalPoints(); i++){
            contactsPlatform.add(new ArrayList<>(board.getCountHorizontalPoints()));
            for (int j=0; j < board.getCountHorizontalPoints(); j++){
                List<Integer> horizontal = contactsPlatform.get(i);
                horizontal.add(number++);
            }
        }
        return contactsPlatform;
    }

    private List<List<Integer>> createOfContactPlatformNumbers(Board board,
                                                               List<Integer> segment){
        List<List<Integer>> contactsPlatform = new ArrayList<>(board.getCountVerticalPoints());
        int number = 0;
        for (int i=0; i < board.getCountVerticalPoints(); i++){
            contactsPlatform.add(new ArrayList<>(board.getCountHorizontalPoints()));
            for (int j=0; j < board.getCountHorizontalPoints(); j++){
                List<Integer> horizontal = contactsPlatform.get(i);
                horizontal.add(segment.get(number++));
            }
        }
        return contactsPlatform;
    }

    /**
     * Метод принимающий параметры высоты, ширины, шага сетки платы, количество слоёв и
     * параметр diagonals, который при значении true учитывает диагональный контакты,
     * как соседние
     * */
    //недоделана
    private void adjacencyMatrix(List<List<Integer>> matrix, Board board){

        List<List<Integer>> printedCircuitBoard = createOfContactPlatformNumbers(board);

        Map<Integer, int[]> contacts = setTheCoordinatesOfContactPads(printedCircuitBoard, board);

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
        matrix = new ArrayList<>();
        for (int i = 0; i < board.getN() * board.getLayers(); i++){
            matrix.add(i, new ArrayList<>());
            for (int j=0; j < board.getN()*board.getLayers(); j++){
                matrix.get(i).add(0);
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

        for (int l = 0; l < board.getLayers(); l++) {
            for (int n = 0; n < board.getN(); n++) {
                int el = n + board.getN() * l;
                List<Integer> boardN = matrix.get(el);
//                    System.out.println(el);
                for (int i = 0; i < board.getCountVerticalPoints(); i++) {
                    for (int j = 0; j < board.getCountHorizontalPoints(); j++) {
                        if (!board.getDiagonals()){
                            if (NeighborhoodUtils.areOrthogonalNeighbors(i, j, contacts.get(n)[0], contacts.get(n)[1])) {
                                boardN.remove(printedCircuitBoard.get(i).get(j)+board.getN() * l);
                                boardN.add(printedCircuitBoard.get(i).get(j)+board.getN() * l, 1);
                            } /*else {
//                                System.out.println("i, j - " + i + ", " + j);
                                boardN.add(boardMatrix[i][j]*(l+1), 0);
                            }*/
                        }
                        else {
                            if (NeighborhoodUtils.areDiagonalNeighbors(i, j, contacts.get(n)[0], contacts.get(n)[1])) {
                                boardN.remove(printedCircuitBoard.get(i).get(j)+board.getN()*l);
                                boardN.add(printedCircuitBoard.get(i).get(j)+board.getN()*l, 1);
                            }/* else {
                                boardN.add(boardMatrix[i][j], 0);
                            }*/
                        }
                    }
                }
                try {
                    boardN.remove(el + board.getCountHorizontalPoints() *
                            board.getCountVerticalPoints());
                    boardN.add(el + board.getCountHorizontalPoints() *
                            board.getCountVerticalPoints(), 1);
                }
                catch (IndexOutOfBoundsException e){

                }
                try {
                    boardN.remove(el - board.getCountHorizontalPoints() *
                            board.getCountVerticalPoints());
                    boardN.add(el - board.getCountHorizontalPoints() *
                            board.getCountVerticalPoints(), 1);
                }
                catch (IndexOutOfBoundsException e){

                }
            }
//            System.out.println("Количество единиц - " + count);
        }

    }

    public List<LinkedList<Integer>> matrixToList(
            List<LinkedList<Integer>> neighboringContacts, List<List<Integer>> board){

        if (neighboringContacts != null){
            throw new NullPointerException("NeighboringContacts in null");
        }
        int n = board.size();
        neighboringContacts = new ArrayList<>();
        for (int i =0; i <n; i++){
            neighboringContacts.add(i, new LinkedList<Integer>());
        }
        int count = 0;
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j ++){
                double arcWeight = board.get(i).get(j);
                if (arcWeight == 1){
                    neighboringContacts.get(i).ins(j);
                    count++;
                }
            }
        }

//        System.out.println("Количество единиц - " + count);
        return neighboringContacts;
    }

    private String showContactPlatform(List<List<Integer>>  printedCircuitBoard){
        StringBuilder builder = new StringBuilder();
        for (List<Integer> horizontal : printedCircuitBoard){
            for (Integer contact : horizontal){
                builder.append(contact + "\t");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

}
