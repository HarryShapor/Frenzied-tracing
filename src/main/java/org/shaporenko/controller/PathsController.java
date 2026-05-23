package org.shaporenko.controller;

import lombok.RequiredArgsConstructor;
import org.shaporenko.dto.paths.PathsSearchRequest;
import org.shaporenko.service.paths.PathsArrayService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/paths")
@RequiredArgsConstructor
public class PathsController {

    private final PathsArrayService pathsArrayService;

    @PostMapping("/{id}")
    public void calculateAllPaths(@PathVariable(name = "id") Long id,
                                  @RequestParam(name = "size", required = false) Integer size) {
        pathsArrayService.savePaths(id, size);
    }

    @PostMapping("/{id}/brd")
    public ResponseEntity<ByteArrayResource> exportBrdForBoard(
            @PathVariable(name = "id") Long id,
            @RequestBody List<PathsSearchRequest> pairs
    ) {
        String fileContent = pathsArrayService.generateBrdForBoard(id, pairs);
        return buildBrdResponse(fileContent, id);
    }

    private ResponseEntity<ByteArrayResource> buildBrdResponse(String fileContent, Long boardId) {
        byte[] fileBytes = fileContent.getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(fileBytes);

        String fileName = "board_" + boardId + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                ".brd";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(fileBytes.length)
                .body(resource);
    }
}
