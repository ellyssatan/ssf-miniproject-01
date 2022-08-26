package vttp.miniproject01game.repositories;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import vttp.miniproject01game.models.Trivia;

@Repository
public class TriviaRepository {

    
    
    List<Trivia> triviaList = new LinkedList<>();
    
    public List<Trivia> saveTrivia(List<Trivia> list) {
        for (Trivia t : list) {
            triviaList.add(t);
        }
        return triviaList;
    }

    public List<Trivia> getTrivia(){
        return this.triviaList;
    }
}
