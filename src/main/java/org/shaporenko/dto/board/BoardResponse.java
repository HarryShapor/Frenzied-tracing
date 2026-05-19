package org.shaporenko.dto.board;

public record BoardResponse(
    Long id,
    Double height,
    Double width,
    Double gridPitch,
    Integer layers,
    Boolean diagonals,
    Integer countVerticalPoints,
    Integer countHorizontalPoints,
    Integer n
) { }
