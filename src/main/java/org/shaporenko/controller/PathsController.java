package org.shaporenko.controller;

import lombok.RequiredArgsConstructor;
import org.shaporenko.service.paths.PathsArrayService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paths")
@RequiredArgsConstructor
public class PathsController {

    private final PathsArrayService pathsArrayService;

    @PostMapping("/{id}")
    public void calculateAllPathsArray(@PathVariable(name = "id") Long id,
                                       @RequestParam(name = "size", required = false) Integer size){
        pathsArrayService.savePaths(id, size);
    }

}
