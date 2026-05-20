package org.shaporenko.service.board;

import org.shaporenko.dto.board.BoardParameters;
import org.shaporenko.entity.Board;
import org.shaporenko.util.BrdCoordinateConverter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BoardFileGeneratorService {

    private static final int TRACK_WIDTH = 197;
    private static final int CLEARANCE = 118;
    private static final int EDGE_WIDTH = 150;

    /** Формат De для сегмента дорожки (как в Primer9.brd). */
    private static final String TRACK_DE = "De 15 0 2 0 0";
    private static final String EDGE_DE = "De 28 0 900 0 0";

    public String generateBoardFile(List<String> paths, Board board) {
        BoardParameters parameters = toBoardParameters(board);
        Map<Integer, long[]> vertexCoordinates = BrdCoordinateConverter.vertexToBrdCoordinates(board);

        List<TrackSegment> trackSegments = buildTrackSegments(paths, vertexCoordinates);
        if (trackSegments.isEmpty()) {
            throw new IllegalStateException(
                    "Нет трасс для экспорта: results пуст или пути не содержат сегментов"
            );
        }

        long originX = BrdCoordinateConverter.BOARD_ORIGIN_X;
        long originY = BrdCoordinateConverter.BOARD_ORIGIN_Y;
        long boardWidth = BrdCoordinateConverter.mmToInternalUnits(board.getWidth());
        long boardHeight = BrdCoordinateConverter.mmToInternalUnits(board.getHeight());
        long boardRight = originX + boardWidth;
        long boardBottom = originY + boardHeight;

        int trackCount = trackSegments.size();
        int drawCount = 4;

        StringBuilder sb = new StringBuilder();
        addHeader(sb);
        addGeneralSection(sb, parameters, drawCount, trackCount);
        addSheetDescrSection(sb);
        addSetupSection(sb, parameters);
        addEquipotSection(sb);
        addNClassSection(sb);
        addBoardOutline(sb, originX, originY, boardRight, boardBottom);
        addTracksSection(sb, trackSegments);
        addFooter(sb);
        return sb.toString();
    }

    public String generateBoardFile(List<String> paths, BoardParameters parameters) {
        return generateBoardFile(paths, boardFromParameters(parameters));
    }

    private List<TrackSegment> buildTrackSegments(List<String> paths, Map<Integer, long[]> vertexCoordinates) {
        List<TrackSegment> segments = new ArrayList<>();

        for (String pathStr : paths) {
            if (pathStr == null || pathStr.isEmpty()) {
                continue;
            }

            List<Integer> vertices = parsePath(pathStr);
            if (vertices.size() < 2) {
                continue;
            }

            for (int i = 0; i < vertices.size() - 1; i++) {
                long[] fromCoord = requireCoordinates(vertexCoordinates, vertices.get(i));
                long[] toCoord = requireCoordinates(vertexCoordinates, vertices.get(i + 1));
                segments.add(new TrackSegment(
                        fromCoord[0], fromCoord[1], toCoord[0], toCoord[1]
                ));
            }
        }
        return segments;
    }

    private Board boardFromParameters(BoardParameters parameters) {
        int countVerticalPoints = (int) (parameters.height() / parameters.gridPitch());
        int countHorizontalPoints = (int) (parameters.width() / parameters.gridPitch());

        Board board = new Board();
        board.setWidth(parameters.width());
        board.setHeight(parameters.height());
        board.setGridPitch(parameters.gridPitch());
        board.setLayers(parameters.layers());
        board.setDiagonals(parameters.diagonals());
        board.setCountVerticalPoints(countVerticalPoints);
        board.setCountHorizontalPoints(countHorizontalPoints);
        board.setN(countVerticalPoints * countHorizontalPoints * Math.max(parameters.layers(), 1));
        return board;
    }

    private BoardParameters toBoardParameters(Board board) {
        return new BoardParameters(
                board.getWidth(),
                board.getHeight(),
                board.getLayers(),
                board.getGridPitch(),
                board.getDiagonals()
        );
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

    private void addGeneralSection(StringBuilder sb, BoardParameters params, int drawCount, int trackCount) {
        int layerCount = Math.max(params.layers(), 2);

        sb.append("$GENERAL\n");
        sb.append("encoding utf-8\n");
        sb.append("LayerCount ").append(layerCount).append("\n");
        sb.append("Ly 1FFF8007\n");
        sb.append("EnabledLayers 1FFF8007\n");
        sb.append("Links 0\n");
        sb.append("NoConn 0\n");

        double widthInUnits = params.width() / BrdCoordinateConverter.INCH_TO_MM / BrdCoordinateConverter.INTERNAL_UNIT_INCH;
        double heightInUnits = params.height() / BrdCoordinateConverter.INCH_TO_MM / BrdCoordinateConverter.INTERNAL_UNIT_INCH;
        sb.append(String.format(
                "Di %d %d %.0f %.0f\n",
                BrdCoordinateConverter.BOARD_ORIGIN_X,
                BrdCoordinateConverter.BOARD_ORIGIN_Y,
                BrdCoordinateConverter.BOARD_ORIGIN_X + widthInUnits,
                BrdCoordinateConverter.BOARD_ORIGIN_Y + heightInUnits
        ));

        sb.append("Ndraw ").append(drawCount).append("\n");
        sb.append("Ntrack ").append(trackCount).append("\n");
        sb.append("Nzone 0\n");
        sb.append("BoardThickness 630\n");
        sb.append("Nmodule 0\n");
        sb.append("Nnets 1\n");
        sb.append("$EndGENERAL\n");
        sb.append("\n");
    }

    private void addSheetDescrSection(StringBuilder sb) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("d MMM yyyy"));
        sb.append("$SHEETDESCR\n");
        sb.append("Sheet A4 11700 8267\n");
        sb.append("Title \"\"\n");
        sb.append("Date \"").append(date).append("\"\n");
        sb.append("Rev \"\"\n");
        sb.append("Comp \"\"\n");
        sb.append("Comment1 \"\"\n");
        sb.append("Comment2 \"\"\n");
        sb.append("Comment3 \"\"\n");
        sb.append("Comment4 \"\"\n");
        sb.append("$EndSHEETDESCR\n");
        sb.append("\n");
    }

    private void addSetupSection(StringBuilder sb, BoardParameters params) {
        int layerCount = Math.max(params.layers(), 2);

        sb.append("$SETUP\n");
        sb.append("InternalUnit 0.000100 INCH\n");
        sb.append("Layers ").append(layerCount).append("\n");
        sb.append("Layer[0] Back signal\n");
        if (layerCount > 2) {
            sb.append("Layer[1] Inner2 signal\n");
            sb.append("Layer[2] Inner3 signal\n");
        }
        sb.append("Layer[15] Front signal\n");
        sb.append("TrackWidth ").append(TRACK_WIDTH).append("\n");
        sb.append("TrackClearence ").append(CLEARANCE).append("\n");
        sb.append("ZoneClearence 200\n");
        sb.append("TrackMinWidth 118\n");
        sb.append("DrawSegmWidth 150\n");
        sb.append("EdgeSegmWidth 150\n");
        sb.append("ViaSize 354\n");
        sb.append("ViaDrill 197\n");
        sb.append("ViaMinSize 354\n");
        sb.append("ViaMinDrill 197\n");
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
        sb.append("Clearance ").append(CLEARANCE).append("\n");
        sb.append("TrackWidth ").append(TRACK_WIDTH).append("\n");
        sb.append("ViaDia 354\n");
        sb.append("ViaDrill 197\n");
        sb.append("uViaDia 200\n");
        sb.append("uViaDrill 50\n");
        sb.append("AddNet \"\"\n");
        sb.append("$EndNCLASS\n");
        sb.append("\n");
    }

    private void addBoardOutline(StringBuilder sb, long x1, long y1, long x2, long y2) {
        addDrawSegment(sb, x1, y1, x2, y1);
        addDrawSegment(sb, x2, y1, x2, y2);
        addDrawSegment(sb, x2, y2, x1, y2);
        addDrawSegment(sb, x1, y2, x1, y1);
    }

    private void addDrawSegment(StringBuilder sb, long x1, long y1, long x2, long y2) {
        sb.append("$DRAWSEGMENT\n");
        sb.append(String.format("Po 0 %d %d %d %d %d\n", x1, y1, x2, y2, EDGE_WIDTH));
        sb.append(EDGE_DE).append("\n");
        sb.append("$EndDRAWSEGMENT\n");
    }

    private void addTracksSection(StringBuilder sb, List<TrackSegment> segments) {
        sb.append("$TRACK\n");

        for (TrackSegment segment : segments) {
            appendTrackSegment(sb, segment.x1(), segment.y1(), segment.x2(), segment.y2());
        }

        sb.append("$EndTRACK\n");
        sb.append("\n");
        sb.append("$ZONE\n");
        sb.append("$EndZONE\n");
    }

    private void appendTrackSegment(StringBuilder sb, long x1, long y1, long x2, long y2) {
        sb.append(String.format(
                "Po 0 %d %d %d %d %d -1\n",
                x1, y1, x2, y2, TRACK_WIDTH
        ));
        sb.append(TRACK_DE).append("\n");
    }

    private long[] requireCoordinates(Map<Integer, long[]> vertexCoordinates, int vertex) {
        long[] coordinates = vertexCoordinates.get(vertex);
        if (coordinates == null) {
            throw new IllegalArgumentException("Unknown vertex number: " + vertex);
        }
        return coordinates;
    }

    private void addFooter(StringBuilder sb) {
        sb.append("$EndBOARD\n");
    }

    private List<Integer> parsePath(String pathStr) {
        return List.of(pathStr.split(","))
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(java.util.ArrayList::new, java.util.ArrayList::add, java.util.ArrayList::addAll);
    }

    private record TrackSegment(long x1, long y1, long x2, long y2) {
    }
}
