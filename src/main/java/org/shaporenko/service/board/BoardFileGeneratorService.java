package org.shaporenko.service.board;

import lombok.RequiredArgsConstructor;
import org.shaporenko.dto.board.BoardParameters;
import org.shaporenko.service.GraphService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BoardFileGeneratorService {

    private static final double INCH_TO_MM = 25.4;
    private static final double INTERNAL_UNIT = 0.000100; // INCH
    private static final double TRACK_WIDTH = 80; // в internal units
    private static final double CLEARANCE = 100;

    private final GraphService graphService;

    public String generateBoardFile(List<String> paths, BoardParameters parameters) {
        StringBuilder sb = new StringBuilder();

        // Заголовок файла
        addHeader(sb);

        // Общие параметры
        addGeneralSection(sb, parameters);

        // Настройки
        addSetupSection(sb);

        // Электрические соединения
        addEquipotSection(sb);

        // Классы цепей
        addNClassSection(sb);

        // Дорожки (трассы)
        addTracksSection(sb, paths, parameters);

        // Завершение файла
        addFooter(sb);

        return sb.toString();
    }

    private void addHeader(StringBuilder sb) {
        String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        );
        sb.append("PCBNEW-BOARD Version 1 date ").append(timestamp).append("\n");
        sb.append("\n");
        sb.append("# Created by FrenziedTracing System\n");
        sb.append("\n");
    }

    private void addGeneralSection(StringBuilder sb, BoardParameters params) {
        sb.append("$GENERAL\n");
        sb.append("encoding utf-8\n");
        sb.append("LayerCount ").append(params.layers()).append("\n");
        sb.append("Ly 1FFF8001\n");
        sb.append("EnabledLayers 1FFF8001\n");
        sb.append("Links 0\n");
        sb.append("NoConn 0\n");

        // Размеры платы (в internal units)
        double widthInUnits = params.width() / INCH_TO_MM / INTERNAL_UNIT;
        double heightInUnits = params.height() / INCH_TO_MM / INTERNAL_UNIT;
        sb.append(String.format("Di 353 353 %.0f %.0f\n", widthInUnits, heightInUnits));

        sb.append("Ndraw 0\n");
        sb.append("Ntrack 1\n");
        sb.append("Nzone 0\n");
        sb.append("BoardThickness 630\n");
        sb.append("Nmodule 0\n");
        sb.append("Nnets 1\n");
        sb.append("$EndGENERAL\n");
        sb.append("\n");
    }

    private void addSetupSection(StringBuilder sb) {
        sb.append("$SETUP\n");
        sb.append("InternalUnit 0.000100 INCH\n");
        sb.append("Layers 2\n");
        sb.append("Layer[0] Back signal\n");
        sb.append("Layer[15] Front signal\n");
        sb.append("TrackWidth ").append((int)TRACK_WIDTH).append("\n");
        sb.append("TrackClearence ").append((int)CLEARANCE).append("\n");
        sb.append("ZoneClearence 200\n");
        sb.append("TrackMinWidth 80\n");
        sb.append("DrawSegmWidth 150\n");
        sb.append("EdgeSegmWidth 150\n");
        sb.append("ViaSize 350\n");
        sb.append("ViaDrill 250\n");
        sb.append("ViaMinSize 350\n");
        sb.append("ViaMinDrill 200\n");
        sb.append("MicroViaSize 200\n");
        sb.append("MicroViaDrill 50\n");
        sb.append("MicroViasAllowed 0\n");
        sb.append("MicroViaMinSize 200\n");
        sb.append("MicroViaMinDrill 50\n");
        sb.append("TextPcbWidth 120\n");
        sb.append("TextPcbSize 600 800\n");
        sb.append("EdgeModWidth 150\n");
        sb.append("TextModSize 600 600\n");
        sb.append("TextModWidth 120\n");
        sb.append("PadSize 600 600\n");
        sb.append("PadDrill 320\n");
        sb.append("Pad2MaskClearance 100\n");
        sb.append("AuxiliaryAxisOrg 0 0\n");
        sb.append("$EndSETUP\n");
        sb.append("\n");
    }

    private void addEquipotSection(StringBuilder sb) {
        sb.append("$EQUIPOT\n");
        sb.append("Na 0 \"\"\n");
        sb.append("St ~\n");
        sb.append("$EndEQUIPOT\n");
        sb.append("\n");
    }

    private void addNClassSection(StringBuilder sb) {
        sb.append("$NCLASS\n");
        sb.append("Name \"Default\"\n");
        sb.append("Desc \"Это класс цепей по умолчанию.\"\n");
        sb.append("Clearance ").append((int)CLEARANCE).append("\n");
        sb.append("TrackWidth ").append((int)TRACK_WIDTH).append("\n");
        sb.append("ViaDia 350\n");
        sb.append("ViaDrill 250\n");
        sb.append("uViaDia 200\n");
        sb.append("uViaDrill 50\n");
        sb.append("AddNet \"\"\n");
        sb.append("$EndNCLASS\n");
        sb.append("\n");
    }

    private void addTracksSection(StringBuilder sb, List<String> paths, BoardParameters params) {
        sb.append("$TRACK\n");

        int trackIndex = 0;
        Map<Integer, int[]> coord = graphService.getCoordinate(params);

        for (String pathStr : paths) {
            if (pathStr == null || pathStr.isEmpty()) continue;

            List<Integer> vertices = parsePath(pathStr);
            if (vertices.size() < 2) continue;

            // Рисуем сегменты между вершинами
            for (int i = 0; i < vertices.size() - 1; i++) {
                int from = vertices.get(i);
                int to = vertices.get(i + 1);

                // Получаем координаты для вершин
//                double[] fromCoord = getCoordinates(from, params);
//                double[] toCoord = getCoordinates(to, params);


                int[] fromCoord = coord.get(from);
                int[] toCoord = coord.get(to);

                // Добавляем трек (дорожку)
                sb.append("Po ");
                sb.append(trackIndex++).append(" ");
                sb.append(formatCoordinate(fromCoord[0]*1000)).append(" ");
                sb.append(formatCoordinate(fromCoord[1]*1000)).append(" ");
                sb.append(formatCoordinate(toCoord[0]*1000)).append(" ");
                sb.append(formatCoordinate(toCoord[1]*1000)).append(" ");
                sb.append((int)TRACK_WIDTH).append(" ");
                sb.append("-1\n");

                sb.append("De 0 0 0 0 0\n");
            }
        }

        sb.append("$EndTRACK\n");
        sb.append("\n");
        sb.append("$ZONE\n");
        sb.append("$EndZONE\n");
    }

    private void addFooter(StringBuilder sb) {
        sb.append("$EndBOARD\n");
    }

    private List<Integer> parsePath(String pathStr) {
        return List.of(pathStr.split(","))
                .stream()
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(java.util.ArrayList::new, java.util.ArrayList::add, java.util.ArrayList::addAll);
    }

    private double[] getCoordinates(int vertex, BoardParameters params) {
        // Предполагаем, что вершины расположены на сетке
        // Вычисляем координаты на основе номера вершины и шага сетки
        int cols = (int)(params.width() / params.gridPitch());
        int row = vertex / cols;
        int col = vertex % cols;

        // Координаты в мм
        double x = col * params.gridPitch();
        double y = row * params.gridPitch();

        // Конвертируем в internal units
        double xUnits = x / INCH_TO_MM / INTERNAL_UNIT ; // смещение как в исходном файле
        double yUnits = y / INCH_TO_MM / INTERNAL_UNIT; // смещение как в исходном файле

        return new double[]{xUnits, yUnits};
    }

    private String formatCoordinate(double coord) {
        return String.format("%.0f", coord);
    }
}