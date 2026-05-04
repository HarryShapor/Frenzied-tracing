package org.shaporenko.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.shaporenko.dto.paths.PathWithTurnInfo;
import org.shaporenko.dto.paths.PathsArrayDto;
import org.shaporenko.entity.Board;
import org.shaporenko.entity.PathString;
import org.shaporenko.entity.PathsArray;
import org.shaporenko.repository.BoardRepository;
import org.shaporenko.repository.PathStringRepository;
import org.shaporenko.repository.PathsArrayRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.shaporenko.service.SimplePathsFinderService.dfs2;

@Service
@RequiredArgsConstructor
public class PathsArrayService {

    private final BoardRepository boardRepository;
    private final PathsArrayRepository pathsArrayRepository;
    private final SimplePathsFinderService simplePathsFinderService;
    private final BoardService boardService;


    public void savePaths(Long id, Integer sizeSegment){

        //получить плату
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found board with id: " + id));
        //разделить на сегменты и получить их
        List<List<Integer>> segments = boardService.splitIntoSegments(sizeSegment, board);


        //цикл по сегментами
        for (int i = 1; i < 3; i++) {
            //вызывать для каждого сегмента поиск путей
            List<PathsArrayDto> dtos = allWays(board, segments.get((i-1)), i, sizeSegment);

            //сохранять в базу
            calculateAllPaths(dtos);
        }
    }

    @Transactional
    public void calculateAllPaths(
            List<PathsArrayDto> paths) {

        int maxLength = 5;

        List<PathsArray> entities = paths.stream()
                .filter(path -> path.turn() < maxLength)
                .map(this::createEntity)
                .collect(Collectors.toList());

        pathsArrayRepository.saveAll(entities);
    }

    private PathsArray createEntity(PathsArrayDto dto) {
        PathsArray entity = new PathsArray();

        entity.setPath(dto.path());
        entity.setStartVertex(dto.startVertex());
        entity.setEndVertex(dto.endVertex());

        entity.setPathLength(dto.pathLength());

        entity.setTurns(dto.turn());
        entity.setNumberSegment(dto.numberSegment());

        return entity;
    }

    private List<PathsArrayDto> allWays(Board board, List<Integer> segment, Integer numberSegment,
                                        Integer sizeSegment) {
        List<PathsArrayDto> result = new ArrayList<>();

        for (int i = 0; i < segment.size(); i++) {
            for (int j = i; j < segment.size(); j++) {
                if (i != j) {
                    Set<List<Integer>> routes = simplePathsFinderService
                            .findAllSimplePathsBetweenSourceAndTarget(segment.get(i),
                                    segment.get(j), segment, sizeSegment);

                    for (List<Integer> route : routes) {
                        int turns = calculateTurns(route, board);
                        PathsArrayDto dto = new PathsArrayDto(route, route.getFirst(),
                                route.getLast(), route.size(), turns, numberSegment);
                        result.add(dto);

                    }
                }
            }
        }
//        int size = result.size();
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
