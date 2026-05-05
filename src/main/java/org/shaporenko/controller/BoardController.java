package org.shaporenko.controller;


import lombok.RequiredArgsConstructor;
import org.shaporenko.dto.board.BoardCreateDto;
import org.shaporenko.dto.board.BoardResponse;
import org.shaporenko.dto.graph.AdjacencyListResponse;
import org.shaporenko.service.board.BoardService;
import org.shaporenko.service.GraphService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final GraphService graphService;


    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable(name = "id") Long id){
        BoardResponse response = boardService.getBoard(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping()
    public ResponseEntity<BoardResponse> saveBoard(@RequestBody BoardCreateDto dto){
        BoardResponse response = boardService.saveBoard(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/graph")
    public ResponseEntity<AdjacencyListResponse> getAdjacencyList(@PathVariable(name = "id") Long id){
        List<List<Integer>> graph = graphService.getNeighborhoodGraph(id);
        return ResponseEntity.ok(new AdjacencyListResponse(graph));
    }

}
