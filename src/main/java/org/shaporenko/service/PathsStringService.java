package org.shaporenko.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.shaporenko.dto.paths.MultiPathRequest;
import org.shaporenko.dto.paths.PathWithTurnInfo;
import org.shaporenko.dto.paths.PathsResponse;
import org.shaporenko.dto.paths.PathsSearchRequest;
import org.shaporenko.entity.Board;
import org.shaporenko.entity.PathString;
import org.shaporenko.repository.BoardRepository;
import org.shaporenko.repository.PathStringRepository;
import org.shaporenko.util.LinkedList;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.shaporenko.service.SimplePathsFinderService.dfs2;

@Service
@RequiredArgsConstructor
public class PathsStringService {

    private final BoardRepository boardRepository;
    private final GraphService graphService;
    private final PathStringRepository pathStringRepository;


    private final SimplePathsFinderService simplePathsFinderService;

    public PathsResponse getAllPaths(){
        List<PathString> pathStrings = pathStringRepository.findAll();
        return createPathsStringResponse(pathStrings);
    }

    public PathsResponse getPathsStartAndEnd(PathsSearchRequest dto){
        List<PathString> pathStrings = pathStringRepository
                .findByStartVertexAndEndVertex(dto.start(), dto.end());
        return createPathsStringResponse(pathStrings);
    }

//    public PathsResponse getMultiPathsString(MultiPathRequest dto){
//        List<Object[]> pairs = dto.queries().stream()
//                .map(q -> new Object[]{q.start(), q.end()})
//                .collect(Collectors.toList());
//
//
//        return createPathsStringResponse(pathStrings);
//    }

    @Transactional
    public List<String> findBestPathsForPairs(List<PathsSearchRequest> request) {
        List<String> results = new ArrayList<>();

        for (PathsSearchRequest pair : request) {
            pathStringRepository.findBestPathForPair(
                    pair.start(),
                    pair.end()
            );
        }
        results = pathStringRepository.findByPathsString();

        return results;
    }

    public void calculateAllPaths(Long id){
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found board with id: " + id));

        List<PathWithTurnInfo> paths = allWaysWithTurnInfo(board);

        calculateAllPaths(paths);
    }

    private PathsResponse createPathsStringResponse(List<PathString> pathStrings){
        Set<List<Integer>> paths = pathStrings.stream()
                .map(entity -> {
                    // Получаем путь из строки "0,1,3,5"
                    String pathStr = entity.getPathString();
                    List<Integer> path = Arrays.stream(pathStr.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                    return path;
                })
                .collect(Collectors.toSet());

        return new PathsResponse(paths, paths.size());
    }

    @Transactional
    public void calculateAllPaths(
            List<PathWithTurnInfo> paths) {

        int maxLength = 7;

        List<PathString> entities = paths.stream()
                .filter(path -> path.turns() < maxLength)
                .map(this::createEntity)
                .collect(Collectors.toList());

        pathStringRepository.saveAll(entities);
    }

    private PathString createEntity(PathWithTurnInfo pathWithTurnInfo) {
        PathString entity = new PathString();

        List<Integer> path = pathWithTurnInfo.path();

        String pathString = path.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        entity.setPathString(pathString);

        entity.setStartVertex(path.get(0));
        entity.setEndVertex(path.get(path.size() - 1));

        entity.setPathLength(path.size());

        entity.setTurns(pathWithTurnInfo.turns());

        return entity;
    }


    private PathString createEntity(List<Integer> path) {
        PathString entity = new PathString();

        String pathString = path.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        entity.setPathString(pathString);

        entity.setStartVertex(path.get(0));
        entity.setEndVertex(path.get(path.size() - 1));
        entity.setPathLength(path.size());

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
        for (int i=0; i < board.getN(); i++){
            for (int j=i; j<board.getN(); j++){
                if (i != j) {
                    Set<List<Integer>> routes1 = simplePathsFinderService
                            .findAllSimplePathsBetweenSourceAndTarget(i, j, board.getId());
                    for (List<Integer> route : routes1) {
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
        int size = result.size();
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
