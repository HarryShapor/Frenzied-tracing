package org.shaporenko.dto.paths;

import java.util.List;

public record MultiPathRequest(
        List<PathsSearchRequest> queries,
        Integer maxLength
) {
}
