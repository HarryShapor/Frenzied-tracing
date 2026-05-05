package org.shaporenko.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.shaporenko.dto.paths.PathsArrayDto;
import org.shaporenko.entity.Board;
import org.shaporenko.entity.Paths;
import org.shaporenko.repository.BoardRepository;
import org.shaporenko.repository.PathsArrayRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PathsArrayService {

    private final BoardRepository boardRepository;
    private final PathsArrayRepository pathsArrayRepository;
    private final SimplePathsFinderService simplePathsFinderService;
    private final BoardService boardService;


    public void savePaths(Long id, Integer sizeSegment){

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found board with id: " + id));

        List<List<Integer>> segments = boardService.splitIntoSegments(sizeSegment, board);

        for (int i = 1; i < segments.size(); i++) {
            List<PathsArrayDto> dtos = allWays(board, segments.get((i-1)), i, sizeSegment);

            calculateAllPaths(dtos, 5);
        }
    }

    @Transactional
    public void calculateAllPaths(
            List<PathsArrayDto> paths, int maxLength) {

        List<Paths> entities = paths.stream()
                .filter(path -> path.turn() < maxLength)
                .map(this::createEntity)
                .collect(Collectors.toList());

        pathsArrayRepository.saveAll(entities);
    }

    private Paths createEntity(PathsArrayDto dto) {
        Paths entity = new Paths();

        entity.setPath(dto.path());
        entity.setStartVertex(dto.startVertex());
        entity.setEndVertex(dto.endVertex());

        entity.setLength(dto.pathLength());

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
