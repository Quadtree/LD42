package info.quadtree.ld42;

import com.badlogic.gdx.graphics.Color;

public enum Team {
    Overminers("Overminers Inc.", Color.YELLOW),
    DigCorp("DigCorp Inc.", Color.RED),
    Underminers("Underminers Inc.", Color.BLUE),
    InterstellarElectric("Interstellar Electric Inc.", Color.GREEN),
    ;

    String name;
    Color color;

    Team(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }
}
