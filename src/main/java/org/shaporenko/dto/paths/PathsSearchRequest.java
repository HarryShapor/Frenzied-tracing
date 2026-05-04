package org.shaporenko.dto.paths;

public record PathsSearchRequest(
        Integer start,
        Integer end,
        Integer length
) {
}
