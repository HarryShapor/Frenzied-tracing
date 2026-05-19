package org.shaporenko.dto.board;

public record BoardSegmentsDto(
        Integer countVerticalSegment,
        Integer countHorizontalSegment,
        Integer layers,
        Boolean diagonals,
        Integer n
) {
}
