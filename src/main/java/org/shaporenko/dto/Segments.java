package org.shaporenko.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Segments {

    private List<List<Integer>> segments;
    private Integer height;
    private Integer width;
    private Integer level;

}