package org.shaporenko.repository;

import org.shaporenko.entity.PathString;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PathStringRepository extends JpaRepository<PathString, Long> {

    List<PathString> findByStartVertexAndEndVertex(Integer start, Integer end);


}
