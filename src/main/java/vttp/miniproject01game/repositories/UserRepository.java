package vttp.miniproject01game.repositories;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.miniproject01game.models.User;

@Repository
public class UserRepository {
    
    @Autowired
    @Qualifier("redis")
    private RedisTemplate<String, String> redisTemplate;

    List<String> usersList = new ArrayList<>();
    String[] retrievedEmails;
    
    public Optional<String> getUser(String email) {

        // System.out.println("Entered idexists");

        if (redisTemplate.hasKey(email)) {

            System.out.println("USER EXISTS");

            ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
            String result = valueOps.get(email);
            return Optional.of(result);
        }

        System.out.println("USER DOESNT EXIST");
        return Optional.empty();
    }

    public void saveUser(User u) {
        // System.out.println("ENTERING SAVE USER........");

        String email = u.getEmail();
        // System.out.println("EMAIL: " + email);

        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();

        // System.out.println(">>>>>>>>> " + u.toJson(u).toString());
        valueOps.set(email, u.toJson(u).toString());
        System.out.println("saved user " + email);

        ValueOperations<String, String> userOps = redisTemplate.opsForValue();

        if (!redisTemplate.hasKey("list")) {

            usersList.add(email);

            System.out.println(">>>>>>>>> " + usersList.toString());
            userOps.set("list", usersList.toString());

        } else {

            List<String> usersList = new LinkedList<>();

            String retrievedUsers = userOps.get("list");
            retrievedUsers = retrievedUsers.replaceAll("\\[", "").replaceAll("\\]", "").trim();
            System.out.println(">>>>Get user list: " + retrievedUsers);

            retrievedEmails = retrievedUsers.split(",");
            System.out.println(">>>>Email list: " + retrievedEmails);
            for (String s: retrievedEmails) {

                usersList.add(s);
            }
            
            usersList.add(email);

            System.out.println(">>>>>>>>> " + usersList.toString());
            userOps.set("list", usersList.toString());
        }
    }

    public boolean userExists(String email) {

        if (redisTemplate.hasKey(email)) {
            return true;
        }
        return false;

    }

    public List<User> getAllUsers() {
        
        List<User> allUsers = new LinkedList<>();

        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();

        String retrievedUsers = valueOps.get("list").replaceAll("\\[", "").replaceAll("\\]", "").trim();

        retrievedEmails = retrievedUsers.split(",");

        for (String u : retrievedEmails) {

            String email = u.trim();

            // System.out.println("email: " + email);
            String payload = valueOps.get(email);
            // System.out.println("payload: " + payload);

            Reader strReader = new StringReader(payload);        
            JsonReader jsonReader = Json.createReader(strReader);
            JsonObject jObject = jsonReader.readObject();

            allUsers.add(User.create(jObject));
        }

        return allUsers;

    }

    // Update score and calculate accuracy
    public void updateScore(String email, int score, int total) {

        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        String userDetails = valueOps.get(email);
        
        Reader strReader = new StringReader(userDetails);        
        JsonReader jsonReader = Json.createReader(strReader);

        JsonObject jObject = jsonReader.readObject();
        User u = User.create(jObject);

        int userScore = u.getHighscore();
        double userTotalQn = u.getTotalQn();
        userScore += score;
        userTotalQn += total;
        
        double accuracy = userScore/userTotalQn*100;
        System.out.println("CALCULATED " + String.format("%.1f", accuracy));

        u.setHighscore(userScore);
        u.setTotalQn((int) userTotalQn);
        u.setAccuracy(String.format("%.1f", accuracy));

        System.out.printf(">> new highscore %d set for %s - ACCURACY: %s\n\n", userScore, email, accuracy);
        valueOps.set(email, u.toJson(u).toString());
    }
}
