package org.shaporenko.repository;

import org.shaporenko.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {


//    @Query("SELECT id, height, width, grid_pitch, layers," +
//            "diagonals, count_vertical_points, count_horizontal_points, n" +
//            "  FROM boards WHERE id= :id")
    Optional<Board> findById(@Param("id") Long id);

}
