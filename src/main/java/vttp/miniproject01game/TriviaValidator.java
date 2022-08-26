package vttp.miniproject01game;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import vttp.miniproject01game.models.Trivia;


public class TriviaValidator implements Validator {
 
    public boolean supports(Class<?> paramClass) {
        return Trivia.class.equals(paramClass);
    }

    public void validate(Object obj, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "ans", "no.answer");
    }
}
