package org.shaporenko.controller;


import org.shaporenko.service.BoardService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }


    //получение платы, её контактов

    //создание платы, ввод начальных параметров

    //получение параметров платы



}
