package org.shaporenko.repository;

import org.shaporenko.entity.PathString;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PathStringRepository extends JpaRepository<PathString, Long> {

//    @Query("SELECT * FROM paths_string WHERE start_vertex = start AND end_vertex = end" +
//            "ORDER BY path_length ASC LIMIT 1")
    List<PathString> findByStartVertexAndEndVertex(Integer start, Integer end);

    @Query(value = """
        INSERT INTO paths_string_2 (path_string, start_vertex, 
            end_vertex, path_length, turns)
        SELECT t2.path_string, t2.start_vertex, 
            t2.end_vertex, t2.path_length, t2.turns 
        FROM paths_string t2
        WHERE NOT EXISTS (
            SELECT 1
            FROM paths_string_2 t1
            WHERE (trim_path_endpoints(t2.path_string, t2.start_vertex::TEXT, t2.end_vertex::TEXT)
                 && string_to_array(t1.path_string, ','))
        )
        AND (t2.start_vertex = :startVertex AND t2.end_vertex = :endVertex)
        ORDER BY t2.path_length, t2.turns ASC
        LIMIT 1
        RETURNING path_string
        """, nativeQuery = true)
    Optional<List<String>> findBestPathForPair(
            @Param("startVertex") Integer startVertex,
            @Param("endVertex") Integer endVertex
    );

    @Query(value = "SELECT path_string FROM paths_string_2" , nativeQuery = true)
    List<String> findByPathsString();

}
