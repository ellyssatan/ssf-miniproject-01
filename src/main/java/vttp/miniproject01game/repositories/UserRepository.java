package vttp.miniproject01game.repositories;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import vttp.miniproject01game.models.User;

@Repository
public class UserRepository {
    
    @Autowired
    @Qualifier("redis")
    private RedisTemplate<String, String> redisTemplate;
    
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
        System.out.println("ENTERING SAVE USER........");

        String email = u.getEmail();
        System.out.println("EMAIL: " + email);

        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();

        System.out.println(">>>>>>>>> " + u.toJson(u).toString());
        valueOps.set(email, u.toJson(u).toString());
        System.out.println("saved user " + email);

    }

    public boolean userExists(String email) {
        if (redisTemplate.hasKey(email)) {
            return true;
        }
        return false;

    }

}
