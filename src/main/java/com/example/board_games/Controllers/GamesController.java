package com.example.board_games.Controllers;

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
import com.example.board_games.Entities.Game;

@RestController
@RequestMapping("boardgames")
public class GamesController {

    public static List<Game> games = new LinkedList<>();
    static int gId = 0;

    /**
     * InnerGamesController
     */
    public record ReqBody(
        String name,
        String publisher
    ) {}

    public GamesController(){
        Game game;
        game = new Game(gId++, "Splendor", "Rebel");
        games.add(game);
        game = new Game(gId++, "Carcassonne", "Rebel");
        games.add(game);
        game = new Game(gId++, "Zakazane Gwiazdy", "Galakta");
        games.add(game);
        
    }

    public static List<Game> getGamesByPublisher(String publisher){
        List<Game> gamesByPublisher = new LinkedList<>();
        for (Game game : games) {
            if(game.getPublisher().equals(publisher)){
                gamesByPublisher.add(game);
            }
        }
        return gamesByPublisher;
    }

    public static void addGame(Game game){
        games.add(game);
    }

    public static void changePublisher(String oldName, String newName){
        for (Game game : games) {
            if(game.getPublisher().equals(oldName)){
                game.setPublisher(newName);
            }
        }
    }
  
    @GetMapping
    public ResponseEntity<List<Game>> getGames(){
        return ResponseEntity
            .ok()
            .eTag(Long.toString(games.hashCode()))
            .body(games);
    }

    @PostMapping()
    public ResponseEntity<Game> postGame(@RequestBody ReqBody reqBodyPost){
        if (reqBodyPost.name == null || reqBodyPost.publisher == null) {
            throw new RecivedPartialData("Recived partial data");
        }
        Game game = new Game(gId++, reqBodyPost.name, reqBodyPost.publisher);
        games.add(game);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .eTag(Long.toString(game.hashCode()))
            .body(game);
    }
    
    @GetMapping("{gameId}")
    public ResponseEntity<Game> getGamesById(@PathVariable("gameId") int gId){
        for (Game game : games) {
            if(game.getId()==gId){
                return ResponseEntity
                    .ok()
                    .eTag(Long.toString(game.hashCode()))
                    .body(game);
                
            }
        }
        throw new EntityNotFound("Game not found");
    }

    @PutMapping("{gameId}")
    public ResponseEntity<Game> putGamesById(@RequestBody ReqBody reqBody, @PathVariable("gameId") int gId, @RequestHeader("If-Match") String request){
        if (reqBody.name == null || reqBody.publisher == null) {
            throw new RecivedPartialData("Recived partial data");
        }
        
        for (Game game : games) {
            if(game.getId()==gId){
                if (!request.equals(Long.toString(game.hashCode()))) {
                    return ResponseEntity
                        .status(HttpStatus.PRECONDITION_FAILED)
                        .eTag(Long.toString(game.hashCode()))
                        .build();
                }
                game.setName(reqBody.name);
                game.setPublisher(reqBody.publisher);
                return ResponseEntity
                    .ok()
                    .eTag(Long.toString(game.hashCode()))
                    .body(game);
            }
        }
        throw new EntityNotFound("Game not found");
    }

    @PatchMapping("{gameId}")
    public ResponseEntity<Game> patchGamesById(@RequestBody ReqBody reqBody, @PathVariable("gameId") int gId, @RequestHeader("If-Match") String request){
        for (Game game : games) {
            if(game.getId()==gId){
                if (!request.equals(Long.toString(game.hashCode()))) {
                    return ResponseEntity
                        .status(HttpStatus.PRECONDITION_FAILED)
                        .eTag(Long.toString(game.hashCode()))
                        .build();
                }
                if (reqBody.name != null) {
                    game.setName(reqBody.name);
                }
                if (reqBody.publisher != null) {
                    game.setPublisher(reqBody.publisher);
                }
                return ResponseEntity
                    .ok()
                    .eTag(Long.toString(game.hashCode()))
                    .body(game);
            }
        }
        throw new EntityNotFound("Game not found");
    }

    @DeleteMapping("{gameId}")
    public ResponseEntity<Game> deleteGamesById(@PathVariable("gameId") int gId){
        for (Game game : games) {
            if(game.getId()==gId){
                games.remove(game);
                return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            }
        }
        throw new EntityNotFound("Game not found");
    }
}
