package com.example.board_games.Controllers;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Exceptions.RecivedPartialData;
import com.example.board_games.Entities.Publisher;

@RestController
@RequestMapping("merge")
public class MergeController {

    public record ReqBody(
        String firstPublisherName,
        String secondPublisherName,
        String newPublisherName
    ){}
    
    
    @PostMapping
    public ResponseEntity<Publisher> mergePublishers(@RequestBody ReqBody reqBody, @RequestHeader("If-Match") String eTag){
        if(reqBody.firstPublisherName == null || reqBody.secondPublisherName == null || reqBody.newPublisherName == null){
            throw new RecivedPartialData("Missing data");
        }
        String[] eTags = eTag.split(";");
        if(eTags.length != 2){
            throw new RecivedPartialData("Missing eTag");
        }

        Publisher firstPublisher = PublishersController.getPublisherByName(reqBody.firstPublisherName());
        Publisher secondPublisher = PublishersController.getPublisherByName(reqBody.secondPublisherName());

        if (firstPublisher == null || secondPublisher == null){
            throw new RecivedPartialData("Publisher not found");
        }

        if (!(  (Long.toString(firstPublisher.hashCode()).equals(eTags[0]) && Long.toString(secondPublisher.hashCode()).equals(eTags[1])) || 
                (Long.toString(firstPublisher.hashCode()).equals(eTags[1]) && Long.toString(secondPublisher.hashCode()).equals(eTags[0])))){
            return ResponseEntity
                .status(Response.SC_PRECONDITION_FAILED)
                .eTag(Long.toString(firstPublisher.hashCode()) + ";" + Long.toString(secondPublisher.hashCode()))
                .body(null);
        }


        PublishersController.changePublisherName(reqBody.firstPublisherName(), reqBody.newPublisherName());
        PublishersController.changePublisherName(reqBody.secondPublisherName(), reqBody.newPublisherName());

        PublishersController.publishers.replaceAll(p -> {
            if(p.getName().equals(reqBody.firstPublisherName())){
                return new Publisher(p.getId(), reqBody.newPublisherName(), p.getCountryCode());
            }
            return p;
        });
        PublishersController.publishers.removeIf(p -> p.getName().equals(reqBody.secondPublisherName()));

        Publisher mergePublisher = PublishersController.getPublisherByName(reqBody.newPublisherName());

        return ResponseEntity
            .status(Response.SC_MOVED_PERMANENTLY)
            .eTag(Long.toString(mergePublisher.hashCode()))
            .body(mergePublisher);
    }
}
