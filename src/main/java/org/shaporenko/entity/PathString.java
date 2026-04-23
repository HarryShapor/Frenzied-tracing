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
@Table(name = "paths_string", indexes = {
        @Index(name = "idx_start_end", columnList = "startVertex, endVertex"),
        @Index(name = "idx_bitmask", columnList = "bitmaskHash")
})
public class PathString {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "path_string", nullable = false)
    private String pathString;

    @Column(name = "start_vertex", nullable = false)
    private Integer startVertex;

    @Column(name = "end_vertex", nullable = false)
    private Integer endVertex;

    @Column(name = "path_length", nullable = false)
    private Integer pathLength;

    @Column(name = "turns")
    private Integer turns;

}