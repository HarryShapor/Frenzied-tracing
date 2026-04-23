package org.shaporenko.controller;

import lombok.RequiredArgsConstructor;
import org.shaporenko.dto.paths.MultiPathRequest;
import org.shaporenko.dto.paths.PathsResponse;
import org.shaporenko.dto.paths.PathsSearchRequest;
import org.shaporenko.service.PathsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paths")
@RequiredArgsConstructor
public class PathsController {

    private final PathsService pathsService;

    //создание всех путей
    @PostMapping("/{id}")
    public void calculateAllPaths(@PathVariable(name = "id") Long id){
        pathsService.calculateAllPaths(id);
    }

    //получение всех путей
    @GetMapping()
    public ResponseEntity<PathsResponse> getAllPaths(){
        return ResponseEntity.status(HttpStatus.OK).body(pathsService.getAllPaths());
    }

//    //получение путей по start, end
//    @GetMapping()
//    public ResponseEntity<PathsResponse> getPathsStartToAnd(@RequestBody PathsSearchRequest request){
//        return ResponseEntity.status(HttpStatus.OK).body(pathsService.getPaths(request));
//    }
//
//    //получение путей по списку пар
//    @GetMapping()
//    public ResponseEntity<PathsResponse> getPathsStartToAnd(@RequestBody MultiPathRequest request){
//        return ResponseEntity.status(HttpStatus.OK).body(pathsService.getMultiPaths(request));
//    }

    //удаление

}
