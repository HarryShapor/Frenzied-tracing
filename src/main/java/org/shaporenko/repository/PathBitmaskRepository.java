package org.shaporenko.repository;

import org.shaporenko.entity.PathBitmask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PathBitmaskRepository extends JpaRepository<PathBitmask, Long> {

    // Поиск путей между конкретными вершинами
    List<PathBitmask> findByStartVertexAndEndVertexOrderByPathLengthAsc(short start, short end);

    // Поиск путей заданной длины
    List<PathBitmask> findByPathLength(short length);

    // Поиск путей, содержащих вершину (через JPQL)
//    @Query("SELECT p FROM PathBitmask p WHERE LENGTH(p.vertexMask) > 0")
//    List<PathBitmask> findAllPaths();

}
