package org.shaporenko.dto.paths;

import java.util.List;
import java.util.Set;

public record PathsResponse(
        Set<List<Integer>> paths,
        Integer count
) { }
