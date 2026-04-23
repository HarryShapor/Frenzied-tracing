package org.shaporenko.util;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NeighborhoodUtils {

    /**
     * Метод возвращающий являются ли контакты по координатам x1,y1 и x2,y2 соседними
     * Ортоганальное соседство
     * */
    public static boolean areOrthogonalNeighbors(int x1, int y1, int x2, int y2){
        if ( ((x1 - x2 == 1 || x1 - x2 == -1) && (y1 - y2 == 0))
                || ((y1 - y2 == 1 || y1 - y2 == -1) && (x1 - x2 == 0))
        ){
            return true;
        }
        return false;
    }

    /**
     * Метод возвращающий являются ли контакты по координатам x1,y1 и x2,y2 соседними,
     * учитывая диагональные
     * Евклидово соседство
     * */
    public static boolean areDiagonalNeighbors(int x1, int y1, int x2, int y2){
        if ( ((x1 - x2 == 1 || x1 - x2 == -1) && (y1-y2 <= 1 && y1-y2 >= -1))
                || ((y1 - y2 == 1 || y1 - y2 == -1) && (x1-x2 <= 1 && x1-x2 >= -1))){
            return true;
        }
        return false;
    }

}
