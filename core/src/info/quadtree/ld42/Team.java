package info.quadtree.ld42;

import com.badlogic.gdx.graphics.Color;
import info.quadtree.ld42.unit.Unit;

import java.util.Objects;

public enum Team {
    Nobody("Nobody", Color.WHITE, false),
    Contested("Contested", Color.ORANGE, false),
    Overminers("Overminers Inc.", Color.YELLOW, false),
    DigCorp("DigCorp Inc.", Color.RED, true),
    Underminers("Underminers Inc.", Color.BLUE, true),
    InterstellarElectric("Interstellar Electric Inc.", Color.GREEN, true),
    ;

    String name;
    Color color;
    boolean aiControlled;

    Team(String name, Color color, boolean aiControlled) {
        this.name = name;
        this.color = color;
        this.aiControlled = aiControlled;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public void beginTurn(){
        LD42.s.gs.hexStream()
                .map(it -> it.unit)
                .filter(Objects::nonNull)
                .filter(it -> it.getTeam() == this)
                .forEach(Unit::turnStart);

        LD42.s.gs.points.put(this, LD42.s.gs.points.get(this) +
                (int)LD42.s.gs.hexStream()
                    .filter(it -> it.owner == this)
                    .count());

        LD42.s.gs.money.put(this, LD42.s.gs.money.get(this) + 20);
    }

    public void takeTurn(){
        if (aiControlled){
            // ...
        }
    }

    public void endTurn(){
        LD42.s.gs.hexStream()
                .map(it -> it.unit)
                .filter(Objects::nonNull)
                .filter(it -> it.getTeam() == this)
                .forEach(Unit::executeMoves);
    }

    public boolean dropUnit(Hex it, Unit.UnitType type){
        if (it.owner == Team.Nobody || it.owner == this) {
            Unit u = Unit.factory(type);
            if (LD42.s.gs.money.get(this) >= u.getCost()) {
                LD42.s.gs.money.put(this, LD42.s.gs.money.get(this) - u.getCost());
                u.setTeam(this).moveTo(it);
                u.startFall();
                LD42.s.gs.recomputeOwnership();
                return true;
            }
        }

        return false;
    }
}
