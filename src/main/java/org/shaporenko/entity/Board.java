package org.shaporenko.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "")
public class Board {

    @Id
    private Long id;

    @Column(name = "")
    private double height;

    @Column(name = "")
    private double width;

    @Column(name = "")
    private double gridPitch;

    @Column(name = "")
    private int layers;

    @Column(name = "")
    private boolean diagonals;

    @Column(name = "")
    private int countVerticalPoints;

    @Column(name = "")
    private int countHorizontalPoints;

    @Column(name = "")
    private int n;

}
