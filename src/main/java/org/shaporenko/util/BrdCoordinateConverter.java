package org.shaporenko.util;

import org.shaporenko.entity.Board;

import java.util.HashMap;
import java.util.Map;

/**
 * Преобразует номера вершин графа (контактных площадок) в координаты KiCad .brd
 * (internal units: 1 unit = 0.0001 inch).
 */
public final class BrdCoordinateConverter {

    public static final double INCH_TO_MM = 25.4;
    public static final double INTERNAL_UNIT_INCH = 0.0001;

    /** Начало координат платы — совпадает с Di в секции $GENERAL. */
    public static final long BOARD_ORIGIN_X = 353;
    public static final long BOARD_ORIGIN_Y = 353;

    private BrdCoordinateConverter() {
    }

    public static long mmToInternalUnits(double mm) {
        return Math.round(mm / INCH_TO_MM / INTERNAL_UNIT_INCH);
    }

    /**
     * Номер вершины → [x, y] в internal units KiCad.
     * Нумерация вершин совпадает с {@link org.shaporenko.service.GraphService}:
     * row-major по сетке countVerticalPoints × countHorizontalPoints.
     */
    public static Map<Integer, long[]> vertexToBrdCoordinates(Board board) {
        int cols = board.getCountHorizontalPoints();
        int rows = board.getCountVerticalPoints();
        int verticesPerLayer = rows * cols;
        long step = mmToInternalUnits(board.getGridPitch());

        Map<Integer, long[]> coordinates = new HashMap<>(board.getN());
        for (int vertex = 0; vertex < board.getN(); vertex++) {
            int localVertex = verticesPerLayer > 0 ? vertex % verticesPerLayer : vertex;
            int row = localVertex / cols;
            int col = localVertex % cols;

            long x = BOARD_ORIGIN_X + (long) col * step;
            long y = BOARD_ORIGIN_Y + (long) row * step;
            coordinates.put(vertex, new long[]{x, y});
        }
        return coordinates;
    }
}
