package org.shaporenko.dto.graph;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Getter
@Setter
@NoArgsConstructor
public class AdjacencyListResponse{
    private List<List<Integer>> adjacencyList;

    public AdjacencyListResponse(List<List<Integer>> customList) {
        this.adjacencyList = convertToList(customList);
    }

    private static List<List<Integer>> convertToList(List<List<Integer>> customList) {
        return customList.stream()
                .map(linkedList -> {
                    List<Integer> list = new ArrayList<>();
                    for (int i = 0; i < linkedList.size(); i++) {
                        list.add(linkedList.get(i));
                    }
                    return list;
                })
                .collect(Collectors.toList());
    }
}