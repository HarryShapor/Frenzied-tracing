package org.shaporenko.dto.board;

public record BoardResponse(
    Double height,
    Double width,
    Double gridPitch,
    Integer layers,
    Boolean diagonals,
    Integer countVerticalPoints,
    Integer countHorizontalPoints,
    Integer n
) { }
