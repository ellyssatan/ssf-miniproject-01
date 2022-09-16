package vttp.miniproject01game.models;

import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

public class Trivia {

    private String difficulty;
    private String question;
    private String correct_answer;
    private List<String> incorrect_answers;
    private List<String> answers;

    public String getDifficulty() {     return difficulty;      }
    public void setDifficulty(String difficulty) {      this.difficulty = difficulty;   }

    public String getQuestion() {       return question;    }
    public void setQuestion(String question) {      this.question = question;   }
    
    public String getCorrect_answer() {     return correct_answer;      }
    public void setCorrect_answer(String correct_answer) {      this.correct_answer = correct_answer;   }

    public List<String> getIncorrect_answers() {    return incorrect_answers;   }
    public void setIncorrect_answers(List<String> incorrect_answers) {      this.incorrect_answers = incorrect_answers;     }

    public List<String> getAnswers() {      return answers;     }
    public void setAnswers(List<String> answers) {      this.answers = answers;     }

    // Create Model from JsonObject
    public static Trivia create(JsonObject jo, JsonArray ansJo) {
        
        Trivia n = new Trivia();
        List<String> incorrectList = new LinkedList<>();
        List<String> ansList = new LinkedList<>();

        n.setDifficulty(n.format(jo.getString("difficulty")));
        n.setQuestion(n.format(jo.getString("question")));
        n.setCorrect_answer(n.format(jo.getString("correct_answer")));
        for (int i = 0; i < ansJo.size(); i++) {
            incorrectList.add(n.format(ansJo.getString(i)));
            // System.out.println(">>>>>>" + ansJo.getString(i));
        }
        // System.out.println("Trivia: " + incorrectList);
        n.setIncorrect_answers(incorrectList);
        for (String i : incorrectList) {
            ansList.add(i);
        }
        ansList.add(n.format(n.getCorrect_answer()));
        n.setAnswers(ansList);
        return n;

    }

    // escape special char
    public String format(String s) {

        String text= Jsoup.parse(s).text();
        return text;

    }

    // Convert model to JsonObject
    // public JsonObject toJson(Articles a) {
    //     return Json.createObjectBuilder()
    //         .add("id", id)
    //         .add("title", title)
    //         .add("body", body)
    //         .add("published_on", publishedOn)
    //         .add("url", url)
    //         .add("imageurl", imageUrl)
    //         .add("tags", tags)
    //         .add("categories", categories)
    //         .build();
    // }
}
