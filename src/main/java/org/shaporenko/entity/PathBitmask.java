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
@Table(name = "paths_bitmask", indexes = {
        @Index(name = "idx_start_end", columnList = "startVertex, endVertex"),
        @Index(name = "idx_mask_brin", columnList = "vertexMask")
})
public class PathBitmask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vertex_mask", nullable = false)
    private byte[] vertexMask;

    @Column(name = "start_vertex", nullable = false)
    private Integer startVertex;

    @Column(name = "end_vertex", nullable = false)
    private Integer endVertex;

    @Column(name = "path_length", nullable = false)
    private Integer pathLength;

//    @Column(name = "number_segment", nullable = true)
//    private Integer numberSegment;

}
