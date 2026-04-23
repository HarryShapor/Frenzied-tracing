package org.shaporenko.controller;

import lombok.RequiredArgsConstructor;
import org.shaporenko.dto.paths.MultiPathRequest;
import org.shaporenko.dto.paths.PathsResponse;
import org.shaporenko.dto.paths.PathsSearchRequest;
import org.shaporenko.service.PathsStringService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paths-string")
@RequiredArgsConstructor
public class PathsStringController {

    private final PathsStringService pathsStringService;

    @PostMapping("/{id}")
    public void calculateAllPathsString(@PathVariable(name = "id") Long id){
        pathsStringService.calculateAllPaths(id);
    }

    @GetMapping()
    public ResponseEntity<PathsResponse> getAllPaths(){
        return ResponseEntity.status(HttpStatus.OK).body(pathsStringService.getAllPaths());
    }

    @GetMapping("/control-points")
    public ResponseEntity<PathsResponse> getPathsByControlPoints(@RequestBody PathsSearchRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(pathsStringService.getPathsStartAndEnd(request));
    }

    @GetMapping("/multi-paths")
    public ResponseEntity<PathsResponse> getPathsStartToAnd(@RequestBody MultiPathRequest request){
//        return ResponseEntity.status(HttpStatus.OK).body(pathsStringService.getMultiPaths(request));
        return null;
    }
}
