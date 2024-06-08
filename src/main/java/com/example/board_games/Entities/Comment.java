package com.example.board_games.Entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Comment {
    Integer id;
    Integer gradeId;
    String author;
    String title;
    String content;

    public Comment(Integer id, Integer gradeId, String author, String title, String content){
        this.id = id;
        this.gradeId = gradeId;
        this.author = author;
        this.title = title;
        this.content = content;
    }
}
