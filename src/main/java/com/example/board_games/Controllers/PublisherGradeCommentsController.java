package com.example.board_games.Controllers;

import java.util.LinkedList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.bind.annotation.PostMapping;

import com.Exceptions.EntityNotFound;
import com.Exceptions.RecivedPartialData;
import com.example.board_games.Entities.Comment;

@RestController
@RequestMapping("publishers/{publisherId}/grades/{gradeId}/comment")
public class PublisherGradeCommentsController {
    public static List<Comment> comments = new LinkedList<>();
    static int cId = 0;

    public record ReqBody(
        String author,
        String title,
        String content
    ) {}

    public PublisherGradeCommentsController(){
        Comment comment;
        comment = new Comment(cId++, 0, "John", "Great Publisher", "I love this Publisher");
        comments.add(comment);
        comment = new Comment(cId++, 1, "Alice", "Good Publisher", "I like this Publisher");
        comments.add(comment);
        comment = new Comment(cId++, 2, "Bob", "Bad Publisher", "I don't like this Publisher");
        comments.add(comment);
        comment = new Comment(cId++, 3, "Charlie", "Great Publisher", "I love this Publisher");
        comments.add(comment);
        comment = new Comment(cId++, 4, "David", "Good Publisher", "I like this Publisher");
        comments.add(comment);
    }    

    public static int addEmptyComment(int gradeId){
        int commentId = cId++;
        Comment comment = new Comment(commentId, gradeId, "", "", "");
        comments.add(comment);

        return commentId;
    }

    public static void deleteCommentsByGradeId(int gradeId){
        comments.removeIf(c -> c.getGradeId() == gradeId);
    }

    private Comment FindComment(int gradeId){
        return comments.stream()
            .filter(c -> c.getGradeId() == gradeId)
            .findFirst()
            .orElse(null);
    }

    @GetMapping
    public ResponseEntity<Comment> getComment(@PathVariable("gradeId") int gradeId){
        Comment comment = FindComment(gradeId);
        if (comment == null){
            throw new EntityNotFound("Comment not found");
        }
        return ResponseEntity
            .ok()
            .eTag(Long.toString(comment.hashCode()))
            .body(comment);
    }

    // @PostMapping
    // public ResponseEntity<Comment> postComment(@PathVariable("gradeId") int gradeId, @RequestBody ReqBody reqBodyPost){
    //     if (FindComment(gradeId) != null){
    //         throw new ContentAlreadyExists("Comment already exists");
    //     }

    //     if (reqBodyPost.author == null || reqBodyPost.title == null){
    //         throw new RecivedPartialData("Author and title are required");
    //     }
    //     String content = "";
    //     if (reqBodyPost.content != null){
    //         content = reqBodyPost.content;
    //     }
    //     Comment comment = new Comment(cId++, gradeId, reqBodyPost.author, reqBodyPost.title, content);
    //     comments.add(comment);
    //     return ResponseEntity
    //         .ok()
    //         .eTag(Long.toString(comment.hashCode()))
    //         .body(comment);
    // }

    @PutMapping
    public ResponseEntity<Comment> putComment(@PathVariable("gradeId") int gradeId, @RequestBody ReqBody reqBodyPut, @RequestHeader("If-Match") String ifMatch){
        Comment comment = FindComment(gradeId);
        if (comment == null){
            throw new EntityNotFound("Comment not found");
        }
        
        if (!ifMatch.equals(Long.toString(comment.hashCode()))){
            return ResponseEntity
                .status(HttpStatus.PRECONDITION_FAILED)
                .eTag(Long.toString(comment.hashCode()))
                .build();
        }
        
        if (reqBodyPut.author == null || reqBodyPut.title == null || reqBodyPut.content == null){
            throw new RecivedPartialData("Author, title and content are required");
        }
        comment.setAuthor(reqBodyPut.author);
        comment.setTitle(reqBodyPut.title);
        comment.setContent(reqBodyPut.content);

        return ResponseEntity
            .ok()
            .eTag(Long.toString(comment.hashCode()))
            .body(comment);
    }


    @PatchMapping
    public ResponseEntity<Comment> patchComment(@PathVariable("gradeId") int gradeId, @RequestBody ReqBody reqBodyPatch, @RequestHeader("If-Match") String ifMatch){
        Comment comment = FindComment(gradeId);
        if (comment == null){
            throw new EntityNotFound("Comment not found");
        }

        if (!ifMatch.equals(Long.toString(comment.hashCode()))){
            return ResponseEntity
                .status(HttpStatus.PRECONDITION_FAILED)
                .eTag(Long.toString(comment.hashCode()))
                .build();
        }

        if (reqBodyPatch.author != null){
            comment.setAuthor(reqBodyPatch.author);
        }
        if (reqBodyPatch.title != null){
            comment.setTitle(reqBodyPatch.title);
        }
        if (reqBodyPatch.content != null){
            comment.setContent(reqBodyPatch.content);
        }

        return ResponseEntity
            .ok()
            .eTag(Long.toString(comment.hashCode()))
            .body(comment);
    }

    @DeleteMapping
    public ResponseEntity<Comment> deleteComment(@PathVariable("gradeId") int gradeId){
        Comment comment = FindComment(gradeId);
        if (comment == null){
            throw new EntityNotFound("Comment not found");
        }
        comments.remove(comment);
        PublisherGradesController.deleteGradeById(gradeId);
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }
}
