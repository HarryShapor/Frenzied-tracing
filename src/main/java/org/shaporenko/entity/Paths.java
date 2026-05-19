package org.shaporenko.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "paths_array")
public class Paths {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "path_array", nullable = false)
    private List<Integer> path;

    @Column(name = "start_vertex", nullable = false)
    private Integer startVertex;

    @Column(name = "end_vertex", nullable = false)
    private Integer endVertex;

    @Column(name = "path_length", nullable = false)
    private Integer length;

    @Column(name = "turns")
    private Integer turns;

    @Column(name = "number_segment")
    private Integer numberSegment;

    @Column(name = "level_")
    private Integer level;
}
