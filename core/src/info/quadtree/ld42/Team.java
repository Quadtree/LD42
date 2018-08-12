package info.quadtree.ld42;

import com.badlogic.gdx.graphics.Color;
import info.quadtree.ld42.unit.*;

import java.util.Objects;

public enum Team {
    Nobody("Nobody", Color.GRAY, false),
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
        assert (it != null);

        if (it.unit != null) return false;

        if (it.owner == Team.Nobody || it.owner == this) {
            Unit u = Unit.factory(type);
            if (LD42.s.gs.money.get(this) >= u.getCost()) {
                LD42.s.gs.money.put(this, LD42.s.gs.money.get(this) - u.getCost());
                u.setTeam(this).moveTo(it);
                u.startFall();
                LD42.s.gs.recomputeOwnership();

                if (type != Unit.UnitType.Mine && this != Overminers){
                    Util.showTutorialText("COMBAT_START", "Hey, that's a " + u.getName().toLowerCase() +
                            "! Well, now it's open season. Use 2 to call in " + new Tank().getName().toLowerCase() + "s, 3 to call in " +
                            new Scout().getName().toLowerCase() + "s, and 4 to call in " + new Turret().getName().toLowerCase() + "s, or click them on the bar at the bottom. Place them like mines.");
                }

                if (this == Overminers){
                    if (type == Unit.UnitType.Tank){
                        Util.showTutorialText("The " + new Tank().getName().toLowerCase() + " is slow, but extremely powerful. Nothing can stop it. Select it with left click and right click to move.");
                    }
                    if (type == Unit.UnitType.Scout){
                        Util.showTutorialText("The " + new Scout().getName().toLowerCase() + " is fast, but vulnerable. It can be destroyed by " + new Tank().getName().toLowerCase() +
                                "s and " + new Turret().getName().toLowerCase() + "s. It is good for capturing mining sites. Select it with left click and right click on a target, like a mining site!");
                    }
                    if (type == Unit.UnitType.Turret){
                        Util.showTutorialText("The " + new Turret().getName().toLowerCase() + " cannot move, is cheap and has decent attack. Left click on it, then right click on an adjacent enemy to attack them.");
                    }
                }

                return true;
            }
        }



        return false;
    }
}
