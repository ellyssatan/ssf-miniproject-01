package vttp.miniproject01game.models;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class User {

    private String name;
    private String email;
    private String password;

    public String getName() {   return name;    }
    public void setName(String name) {      this.name = name;   }

    public String getEmail() {      return email;       }
    public void setEmail(String email) {    this.email = email;     }

    public String getPassword() {   return password;        }
    public void setPassword(String password) {      this.password = password;   }

    // Create a user
    public User create(String name, String email, String password) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(password);
        return u;
    }

    // Convert model to JsonObject
    public JsonObject toJson(User u) {
        return Json.createObjectBuilder()
            .add("name", name)
            .add("email", email)
            .add("password", password)
            .build();
    }

    // public static String createJsonString(JsonObject jo) {
    //     System.out.printf("JSON >>>>> %s\n\n", jo);
    //     System.out.printf("JSON STRING >>>>> %s\n\n", jo.toString());

    //     return jo.toString();
    // }

    // Create Model from JsonObject
    public static User create(JsonObject jo) {
        User u = new User();
        u.setName(jo.getString("name"));
        u.setEmail(jo.getString("email"));
        u.setPassword(jo.getString("password"));
        return u;

    }

}
