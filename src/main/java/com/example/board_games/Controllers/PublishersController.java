package com.example.board_games.Controllers;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Exceptions.EntityNotFound;
import com.Exceptions.RecivedPartialData;
import com.Exceptions.ContentAlreadyExists;
import com.example.board_games.Entities.Publisher;

@RestController
@RequestMapping("publishers")
public class PublishersController {
    public static List<String> usedTokens = new LinkedList<>();
    public static boolean checkToken(String token){
        if(usedTokens.contains(token)){
            return false;
        }
        usedTokens.add(token);
        return true;
    }

    static List<Publisher> publishers = new LinkedList<>();
    static int pId = 0;

    /**
     * InnerPublishersController
     */
    public record ReqBody(
        String name,
        String countryCode
    ) {}

    public PublishersController(){
        Publisher publisher;
        publisher = new Publisher(pId++, "Rebel", "PL");
        publishers.add(publisher);
        publisher = new Publisher(pId++, "Galakta", "PL");
        publishers.add(publisher);
        publisher = new Publisher(pId++, "Fantasy Flight Games", "US");
        publishers.add(publisher);
    }

    public static String getPublisherById(int id){
        return publishers.stream()
            .filter(p -> p.getId() == id)
            .findFirst()
            .orElseThrow(() -> new EntityNotFound("Publisher not found"))
            .getName();
    }

    public static Publisher getPublisherByName(String name){
        return publishers.stream()
            .filter(p -> p.getName().equals(name))
            .findFirst()
            .orElseThrow(() -> new EntityNotFound("Publisher not found"));
    }

    public static void changePublisherName(String oldName, String newName){
        GamesController.changePublisher(oldName, newName);
        PublisherGradesController.changePublisherName(oldName, newName);
    }

    private static void deletePublisher(String name){
        GamesController.changePublisher(name, null);
        PublisherGradesController.deletePublisherGrades(name);
    }

    @GetMapping
    public ResponseEntity<List<Publisher>> getPublishers(){
        return ResponseEntity
            .ok()
            .eTag(Long.toString(publishers.hashCode()))
            .body(publishers);
    }

    @PostMapping()
    public ResponseEntity<Publisher> postPublisher(@RequestBody ReqBody reqBodyPost, @RequestHeader("Token") String token){
        if (!checkToken(token)) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
        }
        if (reqBodyPost.name == null) {
            throw new RecivedPartialData("No name provided");
        }
        for(Publisher publisher : publishers){
            if (publisher.getName().equals(reqBodyPost.name)){
                throw new ContentAlreadyExists("Publisher with this name already exists");
            }
        }
        Publisher publisher = new Publisher(pId++, reqBodyPost.name, reqBodyPost.countryCode);
        publishers.add(publisher);
        return ResponseEntity
            .created(URI.create("/publishers/"+publisher.getId()))
            .eTag(Long.toString(publisher.hashCode()))
            .body(publisher);
    }

    @GetMapping("{id}")
    public ResponseEntity<Publisher> getPublisher(@PathVariable int id){
        Publisher publisher = publishers.stream()
            .filter(p -> p.getId() == id)
            .findFirst()
            .orElseThrow(() -> new EntityNotFound("Publisher not found"));
        return ResponseEntity
            .ok()
            .eTag(Long.toString(publisher.hashCode()))
            .body(publisher);
    }

    @PutMapping("{id}")
    public ResponseEntity<Publisher> putPublisher(@PathVariable int id, @RequestBody ReqBody reqBodyPut, @RequestHeader("If-Match") String ifMatch){
        Publisher publisher = publishers.stream()
            .filter(p -> p.getId() == id)
            .findFirst()
            .orElseThrow(() -> new EntityNotFound("Publisher not found"));
        if (!Long.toString(publisher.hashCode()).equals(ifMatch)) {
            return ResponseEntity
                .status(HttpStatus.PRECONDITION_FAILED)
                .eTag(Long.toString(publisher.hashCode()))
                .build();
        }
        changePublisherName(publisher.getName(), reqBodyPut.name);
        publisher.setName(reqBodyPut.name);
        publisher.setCountryCode(reqBodyPut.countryCode);
        return ResponseEntity
            .ok()
            .eTag(Long.toString(publisher.hashCode()))
            .body(publisher);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Publisher> patchPublisher(@PathVariable int id, @RequestBody ReqBody reqBodyPatch, @RequestHeader("If-Match") String ifMatch){
        Publisher publisher = publishers.stream()
            .filter(p -> p.getId() == id)
            .findFirst()
            .orElseThrow(() -> new EntityNotFound("Publisher not found"));
        if (!Long.toString(publisher.hashCode()).equals(ifMatch)) {
            return ResponseEntity
                .status(HttpStatus.PRECONDITION_FAILED)
                .eTag(Long.toString(publisher.hashCode()))
                .build();
        }
        if (reqBodyPatch.name != null) {
            changePublisherName(publisher.getName(), reqBodyPatch.name);
            publisher.setName(reqBodyPatch.name);
        }
        if (reqBodyPatch.countryCode != null) {
            publisher.setCountryCode(reqBodyPatch.countryCode);
        }
        return ResponseEntity
            .ok()
            .eTag(Long.toString(publisher.hashCode()))
            .body(publisher);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable int id){
        Publisher publisher = publishers.stream()
            .filter(p -> p.getId() == id)
            .findFirst()
            .orElseThrow(() -> new EntityNotFound("Publisher not found"));
        
        deletePublisher(publisher.getName());
        publishers.remove(publisher);
        return ResponseEntity
            .noContent()
            .build();
    }
}
