package org.shaporenko.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.shaporenko.dto.paths.PathsResponse;
import org.shaporenko.entity.Board;
import org.shaporenko.entity.PathBitmask;
import org.shaporenko.repository.BoardRepository;
import org.shaporenko.repository.PathBitmaskRepository;
import org.shaporenko.util.LinkedList;
import org.springframework.stereotype.Service;

import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.shaporenko.service.SimplePathsFinderService.*;

@Service
@RequiredArgsConstructor
public class PathsService {

    private final PathBitmaskRepository pathBitmaskRepository;
    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final GraphService graphService;

    private final SimplePathsFinderService simplePathsFinderService;

    public PathsResponse getAllPaths(){

        List<PathBitmask> pathBitmasks = pathBitmaskRepository.findAll();
        //вызов преобразования битовых масок обратно в числа
        PathsResponse paths = null;

        return paths;
    }

    public void calculateAllPaths(Long id){
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Не нашлась"));

        Set<List<Integer>> paths = allWays(board);

        calculateAllPaths(paths);

    }

    @Transactional
    public void calculateAllPaths(
            Set<List<Integer>> paths) {

        List<PathBitmask> entities = paths.stream()
                .map(path -> createEntity(path))
                .collect(Collectors.toList());

        // Массовое сохранение
        pathBitmaskRepository.saveAll(entities);
    }

    private PathBitmask createEntity(List<Integer> path) {
        PathBitmask entity = new PathBitmask();

        byte[] byteMask = new byte[(25 + 7) / 8];
        for (Integer vertex : path) {
            if (vertex >= 0 && vertex < 25) {
                byteMask[vertex / 8] |= (1 << (vertex % 8));
            }
        }

        entity.setVertexMask(byteMask);
        entity.setStartVertex(path.getFirst());
        entity.setEndVertex(path.getLast());
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
//        List<Integer> lst = this.dfs();
//        for (Integer i : lst) {
//                System.out.print(i + "  ");
//            }
//        this.dfs(0,8);
//        for (Integer i : path) {
//                System.out.print(i + "  ");
//            }
        Set<List<Integer>> routes = new HashSet<>();
        int maxSize = 0;
        for (int i=0; i < board.getN(); i++){
            for (int j=i; j<board.getN(); j++){
                if (i != j) {
//                    System.out.println("i - " + i + ", j - " + j);
                    Set<List<Integer>> routes1 = simplePathsFinderService
                            .findAllSimplePathsBetweenSourceAndTarget(i, j, board.getId());
                    for (List<Integer> route : routes1) {
                        if (maxSize < route.size()){
                            maxSize = route.size();
                        }
                        routes.add(route);
                    }
                }
            }
        }
//        for (List<Integer> lst : this.routes) {
//            System.out.println(lst);
//        }
//        System.out.println("Количество всех путей: " + routes.size());
//        System.out.println("Максимальная длина пути: " + maxSize);
        return routes;
    }



}
