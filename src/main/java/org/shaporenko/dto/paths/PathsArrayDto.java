package org.shaporenko.dto.paths;

import java.util.List;

public record PathsArrayDto(

    List<Integer> path,
    Integer startVertex,
    Integer endVertex,
    Integer pathLength,
    Integer turn,
    Integer numberSegment) {
}
