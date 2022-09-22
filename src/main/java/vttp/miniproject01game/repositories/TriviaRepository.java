package vttp.miniproject01game.repositories;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Repository;

import vttp.miniproject01game.models.Trivia;

@Repository
public class TriviaRepository {
    
    List<Trivia> triviaList = new LinkedList<>();
    
    public List<Trivia> saveTrivia(List<Trivia> list) {

        triviaList.clear();

        for (Trivia t : list) {
            triviaList.add(t);
        }
        return triviaList;
    }

    public List<Trivia> getTrivia() {
        return this.triviaList;
    }

    // Get list of questions
    public List<String> getQuestions(List<Trivia> list) {

        List<String> questions = new LinkedList<>();

        for (Trivia t : list) {
            questions.add(t.getQuestion());
        }

        return questions;
    }

    // Get list of options
    public List<String> getOptions(List<Trivia> list) {

        List<String> options = new LinkedList<>();

        for (Trivia t :list) {
            options.addAll(t.getAnswers());
        }

        return options;
    }

    public List<String> getAnswers(List<Trivia> list) {
        
        List<String> answers = new LinkedList<>();

        for (Trivia t : list) {
            answers.add(t.getCorrect_answer());
        }

        return answers;
    }
}
