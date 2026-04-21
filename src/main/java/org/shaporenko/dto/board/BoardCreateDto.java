package org.shaporenko.dto.board;

public record BoardCreateDto(
        Double height,
        Double width,
        Double gridPitch,
        Integer layers,
        Boolean diagonals
) { }
