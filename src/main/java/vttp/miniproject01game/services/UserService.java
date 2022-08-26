package vttp.miniproject01game.services;

import java.io.Reader;
import java.io.StringReader;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.miniproject01game.models.User;
import vttp.miniproject01game.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository uRepo;

    public User getUser(String email) {

        Optional<String> opt = uRepo.getUser(email);

        if (opt.isEmpty()) {
            return null;
        }

        String payload = opt.get();

        Reader strReader = new StringReader(payload);

        // Create a JsonReader from reader
        JsonReader jsonReader = Json.createReader(strReader);

        // Read and save the payload as Json Object
        JsonObject jObject = jsonReader.readObject();

        return User.create(jObject);
    }

    public void saveUser(User u) {

        uRepo.saveUser(u);
    }

    public boolean checkUser(String email) {
        return uRepo.userExists(email);
    }
}
