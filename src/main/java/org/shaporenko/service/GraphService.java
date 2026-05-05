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

    public List<List<Integer>> getNeighborhoodGraph(Long id){

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

    public List<List<Integer>> buildNeighborhoodGraph(
            Board board){

        //создание печатной платы
        List<List<Integer>> printedCircuitBoard = createOfContactPlatformNumbers(board);

        //вывод номеров контактов
//        System.out.println(this.showContactPlatform(printedCircuitBoard));

        //создание координат контактных площадок
        Map<Integer, int[]> contacts = this.setTheCoordinatesOfContactPads(printedCircuitBoard, board);

        //создание списков смежности контактов
        List<List<Integer>> neighboringContacts = initializationOfNeighboringContacts(board);

        //определение соседей контактных площадок
        neighboringContacts = determiningTheNeighborsOfContactSites(contacts, printedCircuitBoard,
                board, neighboringContacts);

        return neighboringContacts;
    }

    public Map<Integer, List<Integer>> buildNeighborhoodGraph(
            Board board, List<Integer> segment){

        //создание печатной платы
        List<List<Integer>> printedCircuitBoard = createOfContactPlatformNumbers(board, segment);

        //создание координат контактных площадок
        Map<Integer, int[]> contacts = this.setTheCoordinatesOfContactPads(printedCircuitBoard, board);

        //создание списков смежности контактов
        Map<Integer, List<Integer>> neighboringContacts = initializationOfNeighboringContacts(board, segment);

        //определение соседей контактных площадок
        neighboringContacts = determiningTheNeighborsOfContactSites(contacts, printedCircuitBoard,
                board, neighboringContacts, segment);

        return neighboringContacts;
    }

    private Map<Integer, List<Integer>> initializationOfNeighboringContacts(
            Board board, List<Integer> segment){
        Map<Integer, List<Integer>> neighboringContacts = new HashMap<>();

        for (int i = 0; i < board.getLayers() * board.getN(); i++){
            neighboringContacts.put(segment.get(i), new ArrayList<>());
        }

        return neighboringContacts;
    }

    private List<List<Integer>> initializationOfNeighboringContacts(
            Board board){
        List<List<Integer>> neighboringContacts = new ArrayList<>();

        for (int i = 0; i < board.getLayers() * board.getN(); i++){
            neighboringContacts.add(i, new ArrayList<>());
        }

        return neighboringContacts;
    }

    private List<List<Integer>> determiningTheNeighborsOfContactSites(
            Map<Integer, int[]> points, List<List<Integer>> printedCircuitBoard, Board board,
            List<List<Integer>> neighboringContacts){

        int contactFieldNumber = board.getN() / board.getLayers();
        for (int l = 0; l < board.getLayers(); l++) {
            for (int n = 0; n < contactFieldNumber; n++) {
                int el = n + contactFieldNumber *l;
                List<Integer> boardN = neighboringContacts.get(el);
                for (int i = 0; i < board.getCountVerticalPoints(); i++) {
                    for (int j = 0; j < board.getCountHorizontalPoints(); j++) {
                        if (board.getDiagonals()) {
                            if (NeighborhoodUtils.areDiagonalNeighbors(i, j, points.get(n)[0],
                                    points.get(n)[1])) {
                                boardN.add(printedCircuitBoard.get(i).get(j) + contactFieldNumber * l);
                            }
                        }
                        else {
                            if (NeighborhoodUtils.areOrthogonalNeighbors(i, j, points.get(n)[0],
                                    points.get(n)[1])) {
                                boardN.add(printedCircuitBoard.get(i).get(j)+contactFieldNumber*l);
                            }
                        }
                    }
                }
                int down = el + board.getCountHorizontalPoints() * board.getCountVerticalPoints();
                int up = el - board.getCountVerticalPoints() * board.getCountHorizontalPoints();
                if (up >= 0 && up < contactFieldNumber * board.getLayers()) {
                    boardN.add(up);
                }
                if (down >= 0 && down < contactFieldNumber*board.getLayers()) {
                    boardN.add(down);
                }
            }
        }
        return neighboringContacts;
    }

    //определение соседства
    //возможно segment не нужен и можно упростить
    private Map<Integer, List<Integer>> determiningTheNeighborsOfContactSites(
            Map<Integer, int[]> points, List<List<Integer>> printedCircuitBoard, Board board,
            Map<Integer, List<Integer>> neighboringContacts, List<Integer> segment){

        int contactFieldNumber = board.getN() / board.getLayers();
        for (int l = 0; l < board.getLayers(); l++) {
            for (int n = 0; n < contactFieldNumber; n++) {
                int el = n + contactFieldNumber *l;
                List<Integer> boardN = neighboringContacts.get(segment.get(el));
                for (int i = 0; i < board.getCountVerticalPoints(); i++) {
                    for (int j = 0; j < board.getCountHorizontalPoints(); j++) {
                        if (board.getDiagonals()) {
                            if (NeighborhoodUtils.areDiagonalNeighbors(i, j, points.get(segment.get(n))[0],
                                    points.get(segment.get(n))[1])) {
                                boardN.add(printedCircuitBoard.get(i).get(j) + contactFieldNumber * l);
                            }
                        }
                        else {
                            if (NeighborhoodUtils.areOrthogonalNeighbors(i, j, points.get(segment.get(n))[0],
                                    points.get(segment.get(n))[1])) {
                                boardN.add(printedCircuitBoard.get(i).get(j)+contactFieldNumber*l);
                            }
                        }
                    }
                }
                int down = el + board.getCountHorizontalPoints() * board.getCountVerticalPoints();
                int up = el - board.getCountVerticalPoints() * board.getCountHorizontalPoints();
                if (up >= 0 && up < contactFieldNumber * board.getLayers()) {
                    boardN.add(up);
                }
                if (down >= 0 && down < contactFieldNumber*board.getLayers()) {
                    boardN.add(down);
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
