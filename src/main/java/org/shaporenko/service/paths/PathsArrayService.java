package org.shaporenko.service.paths;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.shaporenko.dto.paths.PathResult;
import org.shaporenko.dto.paths.PathsArrayDto;
import org.shaporenko.dto.paths.PathsSearchRequest;
import org.shaporenko.entity.Board;
import org.shaporenko.entity.Paths;
import org.shaporenko.repository.BoardRepository;
import org.shaporenko.repository.PathsProcedureRepository;
import org.shaporenko.repository.PathsRepository;
import org.shaporenko.service.SegmentService;
import org.shaporenko.service.SimplePathsFinderService;
import org.shaporenko.service.board.BoardFileGeneratorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PathsArrayService {

    private final BoardRepository boardRepository;
    private final PathsRepository pathsArrayRepository;
    private final PathsProcedureRepository pathsProcedureRepository;
    private final SimplePathsFinderService simplePathsFinderService;
    private final SegmentService segmentService;
    private final BoardFileGeneratorService boardFileGeneratorService;


    @Transactional
    public List<PathResult> findPathsForContactPadPairs(List<PathsSearchRequest> pairs) {
        pathsProcedureRepository.clearResults();

        for (PathsSearchRequest pair : pairs) {
            pathsProcedureRepository.findAndSavePath(pair.start(), pair.end());
        }

        return pathsProcedureRepository.findAllFromResults();
    }

    @Transactional
    public List<String> findBestPathsForPairs(List<PathsSearchRequest> pairs) {
        return findPathsForContactPadPairs(pairs).stream()
                .filter(path -> !path.path().isEmpty())
                .map(this::pathToString)
                .collect(Collectors.toList());
    }

    @Transactional
    public String generateBrdForBoard(Long boardId, List<PathsSearchRequest> pairs) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Not found board with id: " + boardId));

        List<String> paths = findBestPathsForPairs(pairs);
        return boardFileGeneratorService.generateBoardFile(paths, board);
    }

    private String pathToString(PathResult pathResult) {
        return pathResult.path().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    public void savePaths(Long id, Integer sizeSegment){

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found board with id: " + id));

//        List<List<Integer>> segments = boardService.splitIntoSegments(sizeSegment, board);
//
//        for (int i = 1; i < segments.size(); i++) {
//            List<PathsArrayDto> dtos = allWays(board, segments.get((i-1)), i, sizeSegment);
//
//            calculateAllPaths(dtos, 5);
//        }
        if (board.getN() > 25){
            segmentService.split(board, sizeSegment);
        }
        else {
            //обычный расчёт
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
                                route.getLast(), route.size(), turns, numberSegment, 0);
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
