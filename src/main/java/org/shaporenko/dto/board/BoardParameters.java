package org.shaporenko.dto.board;

public record BoardParameters(
        double width,
        double height,
        int layers,
        double gridPitch,
        boolean diagonals
) {}
