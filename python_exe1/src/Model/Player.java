package Model;

public class Player {
    private String name;
    private String color; // for board display
    
    public Player(String name) {
        this.name = name;
        this.color = "";
    }
    
    public String getName() { return name; }
    public void setColor(String color) { this.color = color; }
    public String getColor() { return color; }
}
