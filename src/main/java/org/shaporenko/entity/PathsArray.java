package org.shaporenko.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "paths_array")
public class PathsArray {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "path_array", nullable = false)
    private String path;

    @Column(name = "start_vertex", nullable = false)
    private Integer startVertex;

    @Column(name = "end_vertex", nullable = false)
    private Integer endVertex;

    @Column(name = "path_length", nullable = false)
    private Integer pathLength;

    @Column(name = "turns")
    private Integer turns;

    @Column(name = "number_segment")
    private Integer numberSegment;

}
