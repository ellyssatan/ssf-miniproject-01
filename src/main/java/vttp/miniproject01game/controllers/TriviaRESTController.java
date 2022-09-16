package vttp.miniproject01game.controllers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttp.miniproject01game.models.User;
import vttp.miniproject01game.services.TriviaService;
import vttp.miniproject01game.services.UserService;

@RestController
@RequestMapping(path = "/news")
public class TriviaRESTController {

    @Autowired
    private TriviaService triviaService;

    @Autowired
    private UserService userSvc;
    
    @GetMapping(path = "/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getScoreboard() {

        List<User> users = userSvc.getAllUsers();

        if (users == null) {
            JsonObject errorMsg = Json.createObjectBuilder()
                .add("error", "Cannot find dashboard")
                .build();
                
            String payload = errorMsg.toString();
            return ResponseEntity
                    .badRequest()
                    .body(payload);
        }

        int highscore;
        String userName;
        
        List<String> scoreList = new LinkedList<>();
        
        for (User u : users) {

            highscore = u.getHighscore();
            userName = u.getName();
            String email = u.getEmail();
            String userScore = userName.concat(", ").concat(email).concat(", ").concat(Integer.toString(highscore));
            scoreList.add(userScore);
        }


        JsonObject builder = Json.createObjectBuilder()
            // .add(name, value)
                .build();

        return ResponseEntity.ok(builder.toString());

    }

    
}
