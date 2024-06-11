package com.example.board_games.Controllers;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.board_games.Entities.Game;
import com.Exceptions.RecivedPartialData;

@RestController
@RequestMapping("publishers/{publisherId}/boardgames")
public class PublisherGamesController {

    public static List<String> usedTokens = new LinkedList<>();
    public static boolean checkToken(String token){
        if(usedTokens.contains(token)){
            return false;
        }
        usedTokens.add(token);
        return true;
    }

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
    public ResponseEntity<Game> postGameByPublisher(@PathVariable("publisherId") int publisherId, @RequestBody ReqBody reqBodyPost, @RequestHeader("Token") String token){
        if(!checkToken(token)){
            return ResponseEntity
                .status(403)
                .body(null);
        }
        String publisherName = PublishersController.getPublisherById(publisherId);
        if (reqBodyPost.name == null){
            throw new RecivedPartialData("Name is required");
        }
        Game game = new Game(GamesController.gId++, reqBodyPost.name, publisherName);
        GamesController.addGame(game);
        return ResponseEntity
            .created(URI.create("/boardgames/" + game.getId()))
            .eTag(Long.toString(game.hashCode()))
            .body(game);
    }
}
