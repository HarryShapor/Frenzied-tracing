package org.shaporenko.controller;

import lombok.RequiredArgsConstructor;
import org.shaporenko.dto.paths.PathsResponse;
import org.shaporenko.dto.paths.PathsSearchRequest;
import org.shaporenko.service.paths.PathsStringService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<String>> getPathsStartToAnd(@RequestBody List<PathsSearchRequest> request){
        return ResponseEntity.status(HttpStatus.OK).body(pathsStringService.findBestPathsForPairs(request));
    }
}
