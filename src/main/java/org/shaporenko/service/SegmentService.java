package org.shaporenko.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.shaporenko.dto.Segments;
import org.shaporenko.dto.board.BoardSegmentsDto;
import org.shaporenko.dto.paths.PathsArrayDto;
import org.shaporenko.entity.Board;
import org.shaporenko.entity.Paths;
import org.shaporenko.repository.PathsRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SegmentService {

    private final SimplePathsFinderService simplePathsFinderService;
    private final PathsRepository pathsArrayRepository;

//сегментирование с уровнями сегментов
    //необходимы данные самой платы
    /**
     Берём точки, смотрим количество, если больше 25, то рассчитываем сегменты, если их больше 25,
     то для них тоже надо сделать сетку (граф)
     и рассчитывать сегменты следующего уровня и так пока размер будет меньше или равен 25
     Также надо учитывать длину и высоту графов разных уровней
     */
    public void split(Board board, Integer sizeSegment){

        Integer layers = board.getLayers();
        Boolean diagonals = board.getDiagonals();

        BoardSegmentsDto dto = new BoardSegmentsDto(board.getCountVerticalPoints(),
                board.getCountHorizontalPoints(), layers, diagonals, board.getN());

        int level = 0;
        int n = board.getN();
        int columns = board.getCountHorizontalPoints();
        while (n > 25){
            Segments segmentsGraph = splitIntoSegments(sizeSegment, dto);
            segmentsGraph.setLevel(level);
            List<List<Integer>> segments = segmentsGraph.getSegments();
            List<PathsArrayDto> dtos;
            for (int i = 1; i <= segments.size(); i++) {
                dtos = null;
                dtos = allWays(columns, segments.get((i-1)), i, sizeSegment,
                        level);

                calculateAllPaths(dtos, 6);
                if (i % 25 == 0) {
                    System.gc();
                }
            }

            n = segmentsGraph.getHeight() * segmentsGraph.getWidth();
            dto = new BoardSegmentsDto(segmentsGraph.getHeight(), segmentsGraph.getWidth(),
                    layers, diagonals, n);
            level++;
            columns = segmentsGraph.getWidth();
        }

        //верхний уровень
        Segments segmentsGraph = splitIntoSegments(sizeSegment, dto);
        segmentsGraph.setLevel(level);
        List<List<Integer>> segments = segmentsGraph.getSegments();

        for (int i = 1; i <= segments.size(); i++) {
            List<PathsArrayDto> dtos = allWays(columns, segments.get((i-1)), i, sizeSegment,
                    level);

            calculateAllPaths(dtos, 6);
        }

    }

    //универсальный метод сегментирования
    // разбивает на сегменты собирает количество длины и высоты уменьшенного графа

    public Segments splitIntoSegments(int sizeSegment, BoardSegmentsDto board){
        List<List<Integer>> segments = new ArrayList<>();
        int countHorizontalSegments = 0;
        int countVerticalSegments = 0;

        int startingPointOfTheSegment = 0;
        int firstPointInLine = 0;

        while(true) {
            List<Integer> segment = new ArrayList<>();
            for (int rowInSegment = 0; rowInSegment < sizeSegment; rowInSegment++) {
                int pointSegment = startingPointOfTheSegment + (rowInSegment * board.countHorizontalSegment());
                if (pointSegment > board.n()){
                    break;
                }
                for (int columnInSegment = 0; columnInSegment < sizeSegment; columnInSegment++) {
                    if (pointSegment+ columnInSegment >=
                            firstPointInLine + ((rowInSegment+1) * board.countHorizontalSegment())
                            || pointSegment + columnInSegment >= board.n()){
                        break;
                    }
                    segment.add(pointSegment + columnInSegment);
                }
            }
            startingPointOfTheSegment += (sizeSegment - 1);
            if (countVerticalSegments == 0){
                countHorizontalSegments += 1;
            }
            if (startingPointOfTheSegment >= firstPointInLine + (board.countHorizontalSegment()-1)){
                firstPointInLine += ((sizeSegment - 1) * board.countHorizontalSegment());
                countVerticalSegments += 1;
                if (firstPointInLine >= board.countVerticalSegment() * (board.countHorizontalSegment()-1)){
                    segments.add(segment);
                    break;
                }
                startingPointOfTheSegment = firstPointInLine;
            }
            segments.add(segment);
        }

        Segments segmentsDto = new Segments();
        segmentsDto.setSegments(segments);
        segmentsDto.setHeight(countVerticalSegments);
        segmentsDto.setWidth(countHorizontalSegments);

        return segmentsDto;
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
        entity.setLevel(dto.level());

        return entity;
    }

    private List<PathsArrayDto> allWays(Integer columns, List<Integer> segment, Integer numberSegment,
                                        Integer sizeSegment, Integer level) {
        List<PathsArrayDto> result = new ArrayList<>();

        for (int i = 0; i < segment.size(); i++) {
            for (int j = i; j < segment.size(); j++) {
                if (i != j) {
                    Set<List<Integer>> routes = simplePathsFinderService
                            .findAllSimplePathsBetweenSourceAndTarget(segment.get(i),
                                    segment.get(j), segment, sizeSegment);

                    for (List<Integer> route : routes) {
                        int turns = calculateTurns(route, columns);
                        PathsArrayDto dto = new PathsArrayDto(route, route.getFirst(),
                                route.getLast(), route.size(), turns, numberSegment, level);
                        result.add(dto);

                    }
                }
            }
        }
        return result;
    }

    private int calculateTurns(List<Integer> path, Integer columns) {
        if (path.size() < 3) return 0;

        int turns = 0;;

        // Получаем координаты для каждой вершины
        List<int[]> coordinates = path.stream()
                .map(vertex -> new int[]{vertex / columns, vertex % columns})
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

