package info.quadtree.ld42.unit;

import com.badlogic.gdx.graphics.g2d.Sprite;
import info.quadtree.ld42.Hex;
import info.quadtree.ld42.LD42;
import info.quadtree.ld42.Team;

public abstract class Unit {
    public enum UnitType {
        Mine,
        Tank,
        Scout,
        Turret,
        Block
    }

    int health;

    Hex hex;

    Team team = Team.Contested;



    public void turn(){

    }

    public void render(){
        Sprite sp = LD42.s.getSprite(getMainGraphicName());

        sp.setBounds(hex.getScreenX(), hex.getScreenY(), Hex.HEX_SIZE, Hex.HEX_SIZE);
        sp.setColor(team.getColor());
        sp.draw(LD42.s.batch);
    }

    public void moveTo(Hex hex){
        if (this.hex != null) this.hex.unit = null;

        this.hex = hex;

        if (this.hex != null) {
            this.hex.unit = this;
        }
    }

    public abstract String getMainGraphicName();

    public static Unit factory(UnitType type){
        switch (type){
            case Mine: return new Mine();
            case Tank: return new Mine();
            case Scout: return new Mine();
            case Turret: return new Mine();
            case Block: return new Mine();
        }

        return null;
    }

    public Team getTeam() {
        return team;
    }

    public Unit setTeam(Team team) {
        this.team = team;
        return this;
    }
}
