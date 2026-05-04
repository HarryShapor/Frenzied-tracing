package org.shaporenko.controller;


import lombok.RequiredArgsConstructor;
import org.shaporenko.service.PathsArrayService;
import org.shaporenko.service.PathsStringService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paths-array")
@RequiredArgsConstructor
public class PathsArrayController {

    private final PathsArrayService pathsArrayService;

    @PostMapping("/{id}")
    public void calculateAllPathsArray(@PathVariable(name = "id") Long id,
                                       @RequestParam(name = "size", required = false) Integer size){
        pathsArrayService.savePaths(id, size);
    }

}
