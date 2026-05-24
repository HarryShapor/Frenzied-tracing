package org.shaporenko.dto.paths;

import java.util.List;

public record PathResult(
        Integer startVertex,
        Integer endVertex,
        List<Integer> path
) {
}
