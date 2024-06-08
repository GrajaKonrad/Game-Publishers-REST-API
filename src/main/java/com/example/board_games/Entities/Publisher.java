package com.example.board_games.Entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Publisher {
    Integer id;
    String name;
    String countryCode;

    public Publisher(int id, String name, String countryCode){
        this.id = id;
        this.name = name;
        this.countryCode = countryCode;
    }
    
    public Publisher(){}
}
