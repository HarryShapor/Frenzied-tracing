package org.shaporenko.repository;

import lombok.RequiredArgsConstructor;
import org.shaporenko.dto.paths.PathResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PathsProcedureRepository {

    private final JdbcTemplate jdbcTemplate;

    public void findAndSavePath(int startVertex, int endVertex) {
        jdbcTemplate.execute((Connection connection) -> {
            try (CallableStatement statement = connection.prepareCall("CALL find_and_save_path(?, ?)")) {
                statement.setInt(1, startVertex);
                statement.setInt(2, endVertex);
                statement.execute();
            }
            return null;
        });
    }

    public void clearResults() {
        jdbcTemplate.update("DELETE FROM results");
    }

    public List<PathResult> findAllFromResults() {
        return jdbcTemplate.query(
                """
                SELECT start_vertex, end_vertex, path_array
                FROM results
                WHERE path_array IS NOT NULL
                """,
                this::mapRow
        );
    }

    public List<PathResult> findResultsByStartVertexAndEndVertex(int startVertex, int endVertex) {
        return jdbcTemplate.query(
                """
                SELECT start_vertex, end_vertex, path_array
                FROM results
                WHERE start_vertex = ?
                  AND end_vertex = ?
                  AND path_array IS NOT NULL
                """,
                this::mapRow,
                startVertex,
                endVertex
        );
    }

    private PathResult mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PathResult(
                rs.getInt("start_vertex"),
                rs.getInt("end_vertex"),
                readIntegerArray(rs.getArray("path_array"))
        );
    }

    private List<Integer> readIntegerArray(Array sqlArray) throws SQLException {
        if (sqlArray == null) {
            return List.of();
        }

        Object array = sqlArray.getArray();
        if (array == null) {
            return List.of();
        }

        if (array instanceof Integer[] integers) {
            return Arrays.asList(integers);
        }
        if (array instanceof int[] integers) {
            return Arrays.stream(integers).boxed().toList();
        }
        if (array instanceof Number[] numbers) {
            List<Integer> result = new ArrayList<>(numbers.length);
            for (Number number : numbers) {
                result.add(number.intValue());
            }
            return result;
        }
        if (array instanceof Object[] elements) {
            List<Integer> result = new ArrayList<>(elements.length);
            for (Object element : elements) {
                if (element != null) {
                    result.add(((Number) element).intValue());
                }
            }
            return result;
        }

        throw new IllegalStateException("Unsupported SQL array type: " + array.getClass().getName());
    }
}
