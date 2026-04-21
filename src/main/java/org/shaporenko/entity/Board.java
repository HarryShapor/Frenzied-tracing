package org.shaporenko.entity;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "boards")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "height")
    private Double height;

    @Column(name = "width")
    private Double width;

    @Column(name = "grid_pitch")
    private Double gridPitch;

    @Column(name = "layers")
    private Integer layers;

    @Column(name = "diagonals")
    private Boolean diagonals;

    @Column(name = "count_vertical_points")
    private Integer countVerticalPoints;

    @Column(name = "count_horizontal_points")
    private Integer countHorizontalPoints;

    @Column(name = "n")
    private Integer n;

}
