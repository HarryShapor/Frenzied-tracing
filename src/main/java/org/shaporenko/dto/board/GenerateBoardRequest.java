package org.shaporenko.dto.board;


import org.shaporenko.dto.paths.PathsSearchRequest;
import org.shaporenko.entity.Board;

import java.util.List;

public record GenerateBoardRequest(
        List<PathsSearchRequest> queries,
        BoardParameters parameters
) {}
