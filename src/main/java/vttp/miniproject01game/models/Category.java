package vttp.miniproject01game.models;

import jakarta.json.JsonObject;

public class Category {

    private String name;
    private int value;

    public String getName() {   return name;    }
    public void setName(String name) {      this.name = name;   }

    public int getValue() {     return value;   }
    public void setValue(int value) {       this.value = value;     }

    public static Category create(JsonObject jo) {
        Category c = new Category();
        c.setName(jo.getString("name"));
        c.setValue(jo.getInt("id"));
        return c;
    }
    
}
