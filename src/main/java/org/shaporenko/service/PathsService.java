package org.shaporenko.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.shaporenko.dto.paths.PathWithTurnInfo;
import org.shaporenko.dto.paths.PathsResponse;
import org.shaporenko.entity.Board;
import org.shaporenko.entity.PathString;
import org.shaporenko.repository.BoardRepository;
import org.shaporenko.util.LinkedList;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.shaporenko.service.SimplePathsFinderService.*;

@Service
@RequiredArgsConstructor
public class PathsService {

    private final GraphService graphService;
    private final SimplePathsFinderService simplePathsFinderService;


    private PathString createEntityString(List<Integer> path) {
        PathString entity = new PathString();

        String pathString = path.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        entity.setPath(pathString);

        entity.setStartVertex(path.get(0));
        entity.setEndVertex(path.get(path.size() - 1));
        entity.setLength(path.size());

        return entity;
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

    public int gwtWay(int start, int end, Long id){
        Set<List<Integer>> routes = dfs2(start, end, graphService.getNeighborhoodGraph(id));

        return routes.size();
    }

    public Set<List<Integer>> allWays(Board board){
        Set<List<Integer>> routes = new HashSet<>();
        int maxSize = 0;
        for (int i=0; i < board.getN(); i++){
            for (int j=i; j<board.getN(); j++){
                if (i != j) {
                    Set<List<Integer>> routes1 = simplePathsFinderService
                            .findAllSimplePathsBetweenSourceAndTarget(i, j, board.getId());
                    for (List<Integer> route : routes1) {
//                        if (maxSize < route.size()){
//                            maxSize = route.size();
//                        }
                        routes.add(route);
                    }
                }
            }
        }
        return routes;
    }

    public List<PathWithTurnInfo> allWaysWithTurnInfo(Board board) {
        List<PathWithTurnInfo> result = new ArrayList<>();

        for (int i = 0; i < board.getN(); i++) {
            for (int j = i; j < board.getN(); j++) {
                if (i != j) {
                    Set<List<Integer>> routes = simplePathsFinderService
                            .findAllSimplePathsBetweenSourceAndTarget(i, j, board.getId());

                    for (List<Integer> route : routes) {
                        int turns = calculateTurns(route, board);
                        result.add(new PathWithTurnInfo(route, turns));
                    }
                }
            }
        }

        return result;
    }

    private int calculateTurns(List<Integer> path, Board board) {
        if (path.size() < 3) return 0;

        int turns = 0;
        int cols = board.getCountHorizontalPoints();

        // Получаем координаты для каждой вершины
        List<int[]> coordinates = path.stream()
                .map(vertex -> new int[]{vertex / cols, vertex % cols})
                .collect(Collectors.toList());

        // Определяем направление первого сегмента
        int deltaRow1 = coordinates.get(1)[0] - coordinates.get(0)[0];
        int deltaCol1 = coordinates.get(1)[1] - coordinates.get(0)[1];

        for (int i = 2; i < coordinates.size(); i++) {
            int deltaRow2 = coordinates.get(i)[0] - coordinates.get(i - 1)[0];
            int deltaCol2 = coordinates.get(i)[1] - coordinates.get(i - 1)[1];

            if (deltaRow1 != deltaRow2 || deltaCol1 != deltaCol2) {
                turns++;
                deltaRow1 = deltaRow2;
                deltaCol1 = deltaCol2;
            }
        }

        return turns;
    }

}
