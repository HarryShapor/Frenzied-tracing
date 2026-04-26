package org.shaporenko.dto.board;

public record BoardParameters(
        double width,      // ширина платы
        double height,     // высота платы
        int layers,        // количество слоев
        double gridPitch,  // шаг сетки
        boolean diagonals  // разрешить диагонали
) {}
