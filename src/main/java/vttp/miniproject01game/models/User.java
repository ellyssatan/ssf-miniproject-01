package vttp.miniproject01game.models;

import org.springframework.security.crypto.bcrypt.BCrypt;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class User {

    private String name;
    private String email;
    private String password;
    private int highscore;
    private int totalQn;
    private String accuracy;
    
    public String getName() {   return name;    }
    public void setName(String name) {      this.name = name;   }

    public String getEmail() {      return email;       }
    public void setEmail(String email) {    this.email = email;     }

    public String getPassword() {   return password;        }
    public void setPassword(String password) {      this.password = password;   }

    public int getHighscore() {   return highscore;        }
    public void setHighscore(int highscore) {      this.highscore = highscore;   }

    public int getTotalQn() {   return totalQn;        }
    public void setTotalQn(int totalQn) {      this.totalQn = totalQn;   }

    public String getAccuracy() {   return accuracy;        }
    public void setAccuracy(String accuracy) {      this.accuracy = accuracy;   }

    // Create a user
    public User create(String name, String email, String password) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        String pw_hash = BCrypt.hashpw(password, BCrypt.gensalt());
        // System.out.printf("!!!!!! pw_hash: %s", pw_hash);
        u.setPassword(pw_hash);
        u.setHighscore(0);
        u.setTotalQn(0);
        u.setAccuracy("0");
        return u;
    }

    // Convert model to JsonObject
    public JsonObject toJson(User u) {
        return Json.createObjectBuilder()
            .add("name", name)
            .add("email", email)
            .add("password", password)
            .add("highscore", highscore)
            .add("totalQn", totalQn)
            .add("accuracy", accuracy)
            .build();
    }

    // Create Model from JsonObject
    public static User create(JsonObject jo) {
        User u = new User();
        u.setName(jo.getString("name"));
        u.setEmail(jo.getString("email"));
        u.setPassword(jo.getString("password"));
        u.setHighscore(jo.getInt("highscore"));
        u.setTotalQn(jo.getInt("totalQn"));
        u.setAccuracy(jo.getString("accuracy"));
        return u;

    }

}
