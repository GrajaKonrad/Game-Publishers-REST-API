package com.example.board_games.Entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Game {
    Integer id;
    String name;
    String publisher;

    public Game(int gId, String name, String publisher){
        this.id = gId;
        this.name =name;
        this.publisher = publisher;
    }

    public Game(){}
}
