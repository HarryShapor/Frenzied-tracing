package org.shaporenko.controller;


import lombok.RequiredArgsConstructor;
import org.shaporenko.dto.board.BoardParameters;
import org.shaporenko.dto.board.GenerateBoardRequest;
import org.shaporenko.dto.paths.MultiPathRequest;
import org.shaporenko.service.board.BoardFileGeneratorService;
import org.shaporenko.service.paths.PathsArrayService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/board-file")
@RequiredArgsConstructor
public class BoardFileController {

    private final PathsArrayService pathsArrayService;
    private final BoardFileGeneratorService fileGeneratorService;

    @PostMapping("/generate")
    public ResponseEntity<ByteArrayResource> generateBoardFile(
            @RequestBody GenerateBoardRequest request) {

//        log.info("Generating board file for {} paths", request.paths().size());

        // Получаем пути (если нужно - вызываем сервис для поиска)
        List<String> paths =  pathsArrayService.findBestPathsForPairs(request.queries());

        // Генерируем содержимое файла
        String fileContent = fileGeneratorService.generateBoardFile(
                paths,
                request.parameters()
        );

        // Создаем ресурс для скачивания
        byte[] fileBytes = fileContent.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(fileBytes);

        // Формируем имя файла
        String fileName = "board_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                ".brd";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(fileBytes.length)
                .body(resource);
    }

    @PostMapping("/export")
    public ResponseEntity<ByteArrayResource> exportPaths(@RequestBody MultiPathRequest request) {
        // Получаем лучшие пути для каждой пары
        List<String> paths = pathsArrayService.findBestPathsForPairs(request.queries());

        // Параметры платы по умолчанию
        BoardParameters defaultParams = new BoardParameters(
                5.0,      // ширина
                5.0,       // высота
                1,          // слои
                1.0,       // шаг сетки мм
                false       // без диагоналей
        );



        String fileContent = fileGeneratorService.generateBoardFile(paths, defaultParams);
        byte[] fileBytes = fileContent.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(fileBytes);

        String fileName = "export_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                ".brd";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }
}

