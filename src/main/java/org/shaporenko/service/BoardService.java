package org.shaporenko.service;

import lombok.RequiredArgsConstructor;
import org.shaporenko.dto.board.BoardCreateDto;
import org.shaporenko.dto.board.BoardResponse;
import org.shaporenko.entity.Board;
import org.shaporenko.repository.BoardRepository;
import org.shaporenko.util.LinkedList;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardResponse saveBoard(BoardCreateDto boardDto){

        Integer countVerticalPoints = (int) (boardDto.height() / boardDto.gridPitch());
        Integer countHorizontalPoints = (int) (boardDto.width() / boardDto.gridPitch());
        Integer N = (int) (boardDto.height() * boardDto.width()
                        / Math.pow(boardDto.gridPitch(), 2) * boardDto.layers());
        Board board = new Board();
        board.setHeight(boardDto.height());
        board.setWidth(boardDto.width());
        board.setGridPitch(boardDto.gridPitch());
        board.setLayers(boardDto.layers());
        board.setDiagonals(boardDto.diagonals());
        board.setCountVerticalPoints(countVerticalPoints);
        board.setCountHorizontalPoints(countHorizontalPoints);
        board.setN(N);

        return convertToResponse(boardRepository.save(board));

    }

    public BoardResponse getBoard(Long id){
        return convertToResponse(boardRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    private Set<List<Integer>> splitIntoSegments(int sizeSegment, Board board){
        Set<List<Integer>> segments = new HashSet<>();

        int startingPointOfTheSegment = 0;
        int firstPointInLine = 0;
        while(true) {
            List<Integer> segment = new ArrayList<>();
            for (int rowInSegment = 0; rowInSegment < sizeSegment; rowInSegment++) {
                int pointSegment = startingPointOfTheSegment + (rowInSegment * board.getCountHorizontalPoints());
                if (pointSegment > board.getN()){
                    break;
                }
                for (int columnInSegment = 0; columnInSegment < sizeSegment; columnInSegment++) {
                    if (pointSegment+ columnInSegment >=
                            firstPointInLine + ((rowInSegment+1) * board.getCountHorizontalPoints())
                            || pointSegment + columnInSegment >= board.getN()){
                        break;
                    }
                    segment.add(pointSegment + columnInSegment);
                }
            }
            startingPointOfTheSegment += (sizeSegment - 1);
            if (startingPointOfTheSegment >= firstPointInLine + (board.getCountHorizontalPoints()-1)){
                firstPointInLine += ((sizeSegment - 1) * board.getCountHorizontalPoints());
                if (firstPointInLine >= board.getCountVerticalPoints() * (board.getCountHorizontalPoints()-1)){
                    segments.add(segment);
                    break;
                }
                startingPointOfTheSegment = firstPointInLine;
            }
            segments.add(segment);
        }

//        for (List<Integer> segment : segments){
//            System.out.println(segment);
//        }

        return segments;
    }

    private BoardResponse convertToResponse(Board board){
        return new BoardResponse(board.getHeight(), board.getWidth(), board.getGridPitch(),
                board.getLayers(), board.getDiagonals(),
                board.getCountVerticalPoints(), board.getCountHorizontalPoints(), board.getN());
    }

}
