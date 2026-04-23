package org.shaporenko.dto.paths;

import java.util.List;

public record PathWithTurnInfo(
        List<Integer> path,
        int turns,
        int length
) {
    public PathWithTurnInfo(List<Integer> path, int turns) {
        this(path, turns, path.size());
    }
}