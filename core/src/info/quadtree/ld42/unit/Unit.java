package info.quadtree.ld42.unit;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.g2d.Sprite;
import info.quadtree.ld42.Hex;
import info.quadtree.ld42.LD42;
import info.quadtree.ld42.Team;

import java.util.ArrayList;
import java.util.List;

public abstract class Unit {
    public enum UnitType {
        Mine,
        Tank,
        Scout,
        Turret,
        Block
    }

    int health;

    int moves = 0;

    Hex hex;

    Hex currentDestination;

    Team team = Team.Contested;

    public Unit(){
    }

    public void turnStart(){
        moves = getMaxMoves();
    }

    public void turn(){

    }

    public void render(){
        if (LD42.s.gs.selectedUnit == this){
            Sprite sp = LD42.s.getSprite("selected_hex");

            sp.setBounds(hex.getScreenX(), hex.getScreenY(), Hex.HEX_SIZE, Hex.HEX_SIZE);
            sp.setColor(team.getColor());
            sp.draw(LD42.s.batch);
        }

        Sprite sp = LD42.s.getSprite(getMainGraphicName());

        sp.setBounds(hex.getScreenX(), hex.getScreenY(), Hex.HEX_SIZE, Hex.HEX_SIZE);
        sp.setColor(team.getColor());
        sp.draw(LD42.s.batch);
    }

    public void moveTo(Hex hex){
        if (this.hex != null) this.hex.unit = null;

        if (hex != null){
            if (hex.unit != null) return;
        }

        this.hex = hex;

        if (this.hex != null) {
            this.hex.unit = this;
        }
    }

    public abstract String getMainGraphicName();

    public static Unit factory(UnitType type){
        switch (type){
            case Mine: return new Mine();
            case Tank: return new Tank();
            case Scout: return new Scout();
            case Turret: return new Turret();
            case Block: return new Block();
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

    public int getHealth() {
        return health;
    }

    public Hex getHex() {
        return hex;
    }

    public boolean canBeSelected(){
        return true;
    }

    public List<Hex> pathTo(Hex destHex){
        GraphPath<Hex> hexPath = new DefaultGraphPath<>();
        LD42.s.gs.pathFinder.searchNodePath(this.getHex(), destHex, LD42.s.gs.defaultHeuristic, hexPath);

        List<Hex> ret = new ArrayList<>();

        //System.err.println("START");
        for (int i=1;i<hexPath.getCount();++i){
            //hexPath.get(i).isOnCurrentPath = true;
            ret.add(hexPath.get(i));
        }

        return ret;
    }

    public void executeMoves(){
        if (currentDestination != null && currentDestination != hex){
            List<Hex> hexes = pathTo(currentDestination);
            while (hexes.size() > 0 && moves > 0){
                moveTo(hexes.get(0));
                hexes.remove(0);
                --moves;
            }
        }
    }

    public int getMaxMoves(){
        return 0;
    }

    public Hex getCurrentDestination() {
        return currentDestination;
    }

    public Unit setCurrentDestination(Hex currentDestination) {
        this.currentDestination = currentDestination;
        return this;
    }
}
