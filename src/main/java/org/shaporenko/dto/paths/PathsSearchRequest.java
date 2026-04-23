package org.shaporenko.dto.paths;

import lombok.NonNull;

public record PathsSearchRequest(
        Integer start,
        Integer end,
        Integer length
) {
}
