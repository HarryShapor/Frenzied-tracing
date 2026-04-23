package org.shaporenko.dto.graph;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.shaporenko.util.LinkedList;

import java.util.List;
import java.util.stream.Collectors;
@Getter
@Setter
@NoArgsConstructor
public class AdjacencyListResponse{
    private List<List<Integer>> adjacencyList;

    public AdjacencyListResponse(List<LinkedList<Integer>> customList) {
        this.adjacencyList = convertToList(customList);
    }

    private static List<List<Integer>> convertToList(List<LinkedList<Integer>> customList) {
        return customList.stream()
                .map(linkedList -> {
                    List<Integer> list = new java.util.ArrayList<>();
                    for (int i = 0; i < linkedList.length; i++) {
                        list.add(linkedList.ret(i));
                    }
                    return list;
                })
                .collect(Collectors.toList());
    }
}