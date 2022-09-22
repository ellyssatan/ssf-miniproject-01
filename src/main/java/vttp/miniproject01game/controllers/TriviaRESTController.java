package vttp.miniproject01game.controllers;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttp.miniproject01game.models.Trivia;
import vttp.miniproject01game.models.User;
import vttp.miniproject01game.services.TriviaService;
import vttp.miniproject01game.services.UserService;

@RestController
@RequestMapping
public class TriviaRESTController {

    @Autowired
    private UserService userSvc;
    
    @Autowired
    private TriviaService triviaSvc;
    
    @GetMapping(path = "/score/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUserDetails(@PathVariable(name = "email") String email) {

        User u = userSvc.getUser(email);

        if (u == null) {
            JsonObject errorMsg = Json.createObjectBuilder()
                .add("error", "Cannot find user score details")
                .build();
                
            String payload = errorMsg.toString();
            return ResponseEntity
                    .badRequest()
                    .body(payload);
        }

        String totalCompleted = String.valueOf(u.getHighscore()).concat("/").concat((String.valueOf(u.getTotalQn())));

        JsonObject builder = Json.createObjectBuilder()
                .add("Username", u.getName())
                .add("User email", email)
                .add("Answered Correctly/Total Questions", totalCompleted)
                .add("Accuracy Percentage", u.getAccuracy().concat("%"))
                .build();

        return ResponseEntity.ok(builder.toString());

    }

    @GetMapping(path = "/quiz", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getScoreboard(HttpSession sess) {

        List<Trivia> list = triviaSvc.getSavedTrivia();

        if (list == null) {
            JsonObject errorMsg = Json.createObjectBuilder()
                .add("error", "Cannot find trivia")
                .build();
                
            String payload = errorMsg.toString();
            return ResponseEntity
                    .badRequest()
                    .body(payload);
        }

        List<String> jos = new LinkedList<>();

        for (Trivia t: list) {
            JsonObject builder = Json.createObjectBuilder()
                .add("Question", t.getQuestion())
                .add("Options", t.getAnswers().toString())
                .add("Answer", t.getCorrect_answer())
                .build();
            String jo = builder.toString();
            jos.add(jo);
        }
        
        return ResponseEntity.ok(jos.toString());

    }
}
