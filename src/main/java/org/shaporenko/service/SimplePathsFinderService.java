package org.shaporenko.service;

import lombok.RequiredArgsConstructor;
import org.shaporenko.dto.board.BoardCreateDto;
import org.shaporenko.entity.Board;
import org.shaporenko.service.board.BoardService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SimplePathsFinderService {

    private final GraphService graphService;
    private final BoardService boardService;

    private int chooseTheDirectionOfThePath(int current, List<Integer> currentPath,
                                            Set<int[]> edges, List<List<Integer>> neightboringContacts){
        boolean flag;

        for (Integer currnetNeighboring : neightboringContacts.get(current)) {
            flag = false;
            //цикл по парам рёбер
            for (int[] edge : edges) {
                //если ребро с текущим контактом и его соседом есть, то идём дальше
                if (Arrays.equals(edge, new int[]{current, currnetNeighboring})
                        || Arrays.equals(edge, new int[]{currnetNeighboring, current})) {
                    flag = true;
                }
            }
            if (flag) {
                continue;
            }
            //если flag == false и в текущем питу нет текущего контакта
            if (!flag && !currentPath.contains(currnetNeighboring)) {
                current = currnetNeighboring;
                break;
            }
        }
        return current;
    }

    private int chooseTheDirectionOfThePath(int current, List<Integer> currentPath,
                                            Set<int[]> edges, Map<Integer, List<Integer>> neightboringContacts){
        boolean flag;
//        System.out.println(current);
        for (Integer currnetNeighboring : neightboringContacts.get(current)) {
            flag = false;
            //цикл по парам рёбер
            for (int[] edge : edges) {
                //если ребро с текущим контактом и его соседом есть, то идём дальше
                if (Arrays.equals(edge, new int[]{current, currnetNeighboring})
                        || Arrays.equals(edge, new int[]{currnetNeighboring, current})) {
                    flag = true;
                }
            }
            if (flag) {
                continue;
            }
            //если flag == false и в текущем питу нет текущего контакта
            if (!flag && !currentPath.contains(currnetNeighboring)) {
                current = currnetNeighboring;
                break;
            }
        }
        return current;
    }

    public Set<List<Integer>> findAllSimplePathsBetweenSourceAndTarget(int source, int target,
                                                                       Long id){

        //инициализация структур данных
        Set<List<Integer>> paths = new HashSet<>();
        List<Integer> currentPath = new ArrayList<>();
        Set<int[]> edges = new HashSet<>();

        List<List<Integer>> neightboringContacts = graphService.getNeighborhoodGraph(id);

        int current = source;
        int size;

        //Добавление в текущий путь начальную точку
        currentPath.add(current);
        while (true) {
            size = currentPath.size();

            int currentNew = chooseTheDirectionOfThePath(current, currentPath, edges, neightboringContacts);
            if (current != currentNew) {
                currentPath.add(currentNew); //добавляем текущий контакт
                edges.add(new int[]{current, currentNew}); //добавляем ребро с ним
                current = currentNew;
            }

            if (current != target && size == currentPath.size()) {
//              Возврат к вершине ветвления и удаление рёбер после неё
                if (size == 1) {
                    break;
                }
                int d = currentPath.get(currentPath.size() - 1);
                currentPath.remove(currentPath.size() - 1);
                try {
                    edges.removeIf(i -> i[0] == d);
                } catch (ConcurrentModificationException e) {
                }
                current = currentPath.get(currentPath.size() - 1);
            }

            if (current == target) {
                int d = currentPath.get(currentPath.size() - 1);
                List<Integer> pathLocale = new ArrayList<>(currentPath);
                paths.add(pathLocale);
                edges.removeIf(i -> i[0] == d);
                currentPath.remove(currentPath.size() - 1);
                current = currentPath.get(currentPath.size() - 1);
            }
        }
        return paths;
    }

    public Set<List<Integer>> findAllSimplePathsBetweenSourceAndTarget(int source, int target,
                                                                       List<Integer> segment,
                                                                       Integer sizeSegment){

        //инициализация структур данных
        Set<List<Integer>> paths = new HashSet<>();
        List<Integer> currentPath = new ArrayList<>();
        Set<int[]> edges = new HashSet<>();

        Double width = 0d;
        for (int i = 0; i < segment.size(); i++){
            width++;
            try {
                if (segment.get(i+1) - segment.get(i) != 1){
                    break;
                }
            }
            catch (IndexOutOfBoundsException e){
                width = (double) segment.size() / 2;
            }
        }

        Double height = segment.size() / width;

        Board board = boardService.createBoard(new BoardCreateDto(height, width,
                1.0,1,false));

        Map<Integer, List<Integer>> neightboringContacts
                = graphService.buildNeighborhoodGraph(board, segment);

        int current = source;
        int size;

        //Добавление в текущий путь начальную точку
        currentPath.add(current);
        while (true) {
            size = currentPath.size();

            int currentNew = chooseTheDirectionOfThePath(current, currentPath, edges, neightboringContacts);
            if (current != currentNew) {
                currentPath.add(currentNew); //добавляем текущий контакт
                edges.add(new int[]{current, currentNew}); //добавляем ребро с ним
                current = currentNew;
            }

            if (current != target && size == currentPath.size()) {
//              Возврат к вершине ветвления и удаление рёбер после неё
                if (size == 1) {
                    break;
                }
                int d = currentPath.get(currentPath.size() - 1);
                currentPath.remove(currentPath.size() - 1);
                try {
                    edges.removeIf(i -> i[0] == d);
                } catch (ConcurrentModificationException e) {
                }
                current = currentPath.get(currentPath.size() - 1);
//                System.out.println("current 1 " + current);
            }

            if (current == target) {
                int d = currentPath.get(currentPath.size() - 1);
                List<Integer> pathLocale = new ArrayList<>(currentPath);
                paths.add(pathLocale);
                edges.removeIf(i -> i[0] == d);
                currentPath.remove(currentPath.size() - 1);
                current = currentPath.get(currentPath.size() - 1);
//                System.out.println("current 2 " + current);
            }
        }
        return paths;
    }

    public static Set<List<Integer>> dfs2(int src, int dst, List<List<Integer>> boardList) {
        Set<List<Integer>> routes = new HashSet<>();
        List<Integer> path = new ArrayList<>();
        Set<int[]> edges = new HashSet<>();
        int p = src;
        path.clear();
        edges.clear();
        path.add(p);
        while (true) {
            int size = path.size();
            for (Integer i : boardList.get(p)) {
                boolean flag = false;
                for (int[] g : edges) {
                    if (Arrays.equals(g, new int[]{p, i}) || Arrays.equals(g, new int[]{i, p})) {
                        flag = true;
                    }
                }
                if (flag) {
                    continue;
                }
                if (!flag && !path.contains(i)) {
                    path.add(i);
                    edges.add(new int[]{p, i});
                    p = i;
                    break;
                }
            }
            if (p != dst && size == path.size()) {
                if (size == 1) {
                    break;
                }
                int d1 = path.get(path.size() - 1);
                path.remove(path.size() - 1);
                try {
                    edges.removeIf(i -> i[0] == d1);
                } catch (ConcurrentModificationException e) {

                }
                p = path.get(path.size() - 1);

            }
            if (p == dst) {
                int d1 = path.get(path.size() - 1);
                List<Integer> pathLocale = new ArrayList<>(path);
                routes.add(pathLocale);
                edges.removeIf(i -> i[0] == d1);

                path.remove(path.size() - 1);
                p = path.get(path.size() - 1);
            }
        }
        return routes;
    }


}
