package com.example.board_games.Entities;

import java.time.LocalDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Grade {
    Integer id;
    Integer grade;
    String concernsName;
    LocalDate addedDate;

    public Grade(Integer id, Integer grade, String concernsName, LocalDate addedDate){
        this.id = id;
        if (grade < 1 || grade > 10){
            throw new IllegalArgumentException("Grade must be between 1 and 10");
        }
        this.grade = grade;
        this.concernsName = concernsName;
        if (!(addedDate.isBefore(LocalDate.now()) || addedDate.isEqual(LocalDate.now()))){
            throw new IllegalArgumentException("Date can not be in the future");
        }
        this.addedDate = addedDate;
    }

    public Grade(){}
}
