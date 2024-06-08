package com.example.board_games.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.board_games.Entities.Game;
import com.Exceptions.RecivedPartialData;

@RestController
@RequestMapping("publishers/{publisherId}/boardgames")
public class PublisherGamesController {

    public record ReqBody(
        String name
    ) {}

    @GetMapping
    public ResponseEntity<List<Game>> getGamesByPublisher(@PathVariable("publisherId") int publisherId){
        String publisherName = PublishersController.getPublisherById(publisherId);
        List<Game> gamesByPublisher = GamesController.getGamesByPublisher(publisherName);
        return ResponseEntity
            .ok()
            .eTag(Long.toString(gamesByPublisher.hashCode()))
            .body(gamesByPublisher);
    }

    @PostMapping
    public ResponseEntity<Game> postGameByPublisher(@PathVariable("publisherId") int publisherId, @RequestBody ReqBody reqBodyPost){
        String publisherName = PublishersController.getPublisherById(publisherId);
        if (reqBodyPost.name == null){
            throw new RecivedPartialData("Name is required");
        }
        Game game = new Game(GamesController.gId++, reqBodyPost.name, publisherName);
        GamesController.addGame(game);
        return ResponseEntity
            .ok()
            .eTag(Long.toString(game.hashCode()))
            .body(game);
    }
}
