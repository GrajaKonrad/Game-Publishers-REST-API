package com.example.board_games.Controllers;

import java.util.LinkedList;
import java.util.List;
import java.net.URI;
import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.board_games.Entities.Grade;
import com.Exceptions.EntityNotFound;;

@RestController
@RequestMapping("publishers/{publisherId}/grades")
public class PublisherGradesController {
    public static List<Grade> gradesPublishers = new LinkedList<>();
    static int gId = 0;

    public static List<String> usedTokens = new LinkedList<>();
    public static boolean checkToken(String token){
        if(usedTokens.contains(token)){
            return false;
        }
        usedTokens.add(token);
        return true;
    }

    public PublisherGradesController(){
        Grade grade;
        grade = new Grade(gId++, 5, "Rebel;", LocalDate.now());
        gradesPublishers.add(grade);
        grade  = new Grade(gId++, 5, "Rebel", LocalDate.parse("2019-12-12"));
        gradesPublishers.add(grade);
        grade = new Grade(gId++, 6, "Galakta", LocalDate.now());
        gradesPublishers.add(grade);
        grade = new Grade(gId++, 7, "Galakta", LocalDate.now());
        gradesPublishers.add(grade);
        grade = new Grade(gId++, 7, "Rebel", LocalDate.now());
        gradesPublishers.add(grade);
    }

    public static void changePublisherName(String oldName, String newName){
        for (Grade grade : gradesPublishers) {
            if(grade.getConcernsName().equals(oldName)){
                grade.setConcernsName(newName);
            }
        }
    }

    public static void deletePublisherGrades(String publisherName){
        for (Grade grade : gradesPublishers) {
            if(grade.getConcernsName().equals(publisherName)){
                PublisherGradeCommentsController.deleteCommentsByGradeId(grade.getId());
            }
        }
        gradesPublishers.removeIf(g -> g.getConcernsName().equals(publisherName));
    }

    public static void deleteGradeById(int id){
        gradesPublishers.removeIf(g -> g.getId() == id);
    }

    public record ReqBody(
        Integer grade
    ) {}

    @GetMapping
    public ResponseEntity<List<Grade>> getGradesByPublisher(@PathVariable("publisherId") int publisherId){
        List<Grade> gradesByPublisher = new LinkedList<>();
        String publisherName = PublishersController.getPublisherById(publisherId);
        if (publisherName == null){
            throw new EntityNotFound("Publisher not found");
        }
        for (Grade grade : gradesPublishers) {
            if(grade.getConcernsName().equals(publisherName)){
                gradesByPublisher.add(grade);
            }
        }
        return ResponseEntity
            .ok()
            .eTag(Long.toString(gradesByPublisher.hashCode()))
            .body(gradesByPublisher);
    }

    public static class GradeWithComment {
        Grade grade;
        Integer comment;

        public GradeWithComment(Grade grade, int comment){
            this.grade = grade;
            this.comment = comment;
        }
    }

    @PostMapping
    public ResponseEntity<Grade> addGrade(@PathVariable("publisherId") int publisherId, @RequestBody ReqBody reqBody, @RequestHeader("Token") String token){
        if(!checkToken(token)){
            return ResponseEntity
                .status(403)
                .body(null);
        }
        String publisherName = PublishersController.getPublisherById(publisherId);
        Grade grade = new Grade(gId++, reqBody.grade(), publisherName, LocalDate.now());
        gradesPublishers.add(grade);
        int commentId = PublisherGradeCommentsController.addEmptyComment(grade.getId());
        GradeWithComment gradeWithComment = new GradeWithComment(grade, commentId);
        return ResponseEntity
            .created(URI.create("/publishers/" + publisherId  + "/grades/" + grade.getId() + "/comment"))
            .eTag(Long.toString(grade.hashCode()))
            .body(grade);
    }
    
}
